package com.espezzialy.simpleplayer.media

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.espezzialy.simpleplayer.MainActivity
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.presentation.navigation.playerRouteFor
import com.espezzialy.simpleplayer.presentation.player.PlayerUiState
import com.espezzialy.simpleplayer.presentation.player.toSongForNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(UnstableApi::class)
@Singleton
class PlayerNotificationController
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val prefs =
            context.getSharedPreferences(PlayerNotificationConstants.PREFS_NAME, Context.MODE_PRIVATE)

        private val exoPlayer: ExoPlayer by lazy { ExoPlayer.Builder(context).build() }
        private val imageLoader: ImageLoader by lazy { ImageLoader(context) }

        private val supervisorJob = SupervisorJob()
        private val notificationScope = CoroutineScope(supervisorJob + Dispatchers.Main.immediate)

        val transport = PlayerNotificationTransport()

        private var mediaSession: MediaSession? = null
        private var playerNotificationManager: PlayerNotificationManager? = null
        private var artworkLoadJob: Job? = null

        @Volatile
        private var latestUiState: PlayerUiState? = null

        private var cachedArtworkUrl: String? = null
        private var cachedArtworkBitmap: Bitmap? = null
        private var cachedAccentArgb: Int = defaultAccentArgb

        fun update(state: PlayerUiState) {
            latestUiState = state
            val route = playerRouteFor(state.toSongForNotification())
            prefs.edit().putString(PlayerNotificationConstants.KEY_PLAYER_ROUTE, route).apply()
            if (mediaSession == null) return

            playerNotificationManager?.invalidate()

            artworkLoadJob?.cancel()
            artworkLoadJob =
                notificationScope.launch {
                    val url = state.artworkUrl?.takeIf { it.isNotBlank() }
                    if (url == null) {
                        cachedArtworkUrl = null
                        cachedArtworkBitmap = null
                        cachedAccentArgb = defaultAccentArgb
                        playerNotificationManager?.setColor(defaultAccentArgb)
                        playerNotificationManager?.invalidate()
                        return@launch
                    }
                    if (url == cachedArtworkUrl && cachedArtworkBitmap != null) {
                        playerNotificationManager?.invalidate()
                        return@launch
                    }
                    val bitmap = loadBitmapFromNetwork(url)
                    cachedArtworkUrl = url
                    cachedArtworkBitmap = bitmap
                    cachedAccentArgb =
                        bitmap?.let { b ->
                            withContext(Dispatchers.Default) {
                                computeAccentArgbFromBitmap(b, defaultAccentArgb)
                            }
                        } ?: defaultAccentArgb
                    playerNotificationManager?.setColor(cachedAccentArgb)
                    playerNotificationManager?.setColorized(true)
                    playerNotificationManager?.invalidate()
                }
        }

        fun consumePendingPlayerRoute(): String? {
            val route = prefs.getString(PlayerNotificationConstants.KEY_PLAYER_ROUTE, null) ?: return null
            prefs.edit().remove(PlayerNotificationConstants.KEY_PLAYER_ROUTE).apply()
            return route
        }

        fun attach(initialState: PlayerUiState) {
            if (mediaSession != null) return
            latestUiState = initialState
            ensureChannel()

            val uiPlayer =
                UiSyncedForwardingPlayer(
                    exoPlayer,
                    getUiState = { latestUiState },
                    transport = transport,
                )
            val session =
                MediaSession.Builder(context, uiPlayer)
                    .setSessionActivity(buildSessionActivityPendingIntent())
                    .build()
            mediaSession = session

            val adapter =
                object : PlayerNotificationManager.MediaDescriptionAdapter {
                    override fun createCurrentContentIntent(player: Player): PendingIntent {
                        return buildSessionActivityPendingIntent()
                    }

                    override fun getCurrentContentTitle(player: Player): CharSequence {
                        return latestUiState?.trackName ?: context.getString(R.string.app_name)
                    }

                    override fun getCurrentContentText(player: Player): CharSequence? {
                        return latestUiState?.artistName
                    }

                    override fun getCurrentSubText(player: Player): CharSequence? = null

                    override fun getCurrentLargeIcon(
                        player: Player,
                        callback: PlayerNotificationManager.BitmapCallback,
                    ) = cachedArtworkBitmap
                }

            val nm =
                PlayerNotificationManager.Builder(
                    context,
                    PlayerNotificationConstants.NOTIFICATION_ID,
                    PlayerNotificationConstants.CHANNEL_ID,
                    adapter,
                )
                    .setChannelNameResourceId(R.string.player_notification_channel_name)
                    .setChannelImportance(NotificationManager.IMPORTANCE_DEFAULT)
                    .setSmallIconResourceId(R.drawable.ic_play)
                    .build()

            nm.setMediaSessionToken(session.platformToken)
            nm.setUsePlayPauseActions(true)
            nm.setUsePreviousAction(true)
            nm.setUseNextAction(true)
            nm.setUseRewindAction(false)
            nm.setUseFastForwardAction(false)
            nm.setUsePreviousActionInCompactView(true)
            nm.setUseNextActionInCompactView(true)
            nm.setUseChronometer(false)
            nm.setColorized(true)
            nm.setColor(cachedAccentArgb)
            nm.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            nm.setPlayer(uiPlayer)
            playerNotificationManager = nm
            update(initialState)
        }

        fun detach() {
            artworkLoadJob?.cancel()
            supervisorJob.cancelChildren()
            latestUiState = null
            cachedArtworkUrl = null
            cachedArtworkBitmap = null
            cachedAccentArgb = defaultAccentArgb
            playerNotificationManager?.setPlayer(null)
            playerNotificationManager = null
            mediaSession?.release()
            mediaSession = null
            transport.clear()
        }

        private suspend fun loadBitmapFromNetwork(url: String): Bitmap? =
            withContext(Dispatchers.IO) {
                val request =
                    ImageRequest.Builder(context)
                        .data(url)
                        .allowHardware(false)
                        .size(ARTWORK_SIZE_PX, ARTWORK_SIZE_PX)
                        .build()
                when (val result = imageLoader.execute(request)) {
                    is SuccessResult -> result.drawable.toBitmap()
                    else -> null
                }
            }

        private fun ensureChannel() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(PlayerNotificationConstants.CHANNEL_ID) != null) return
            val channel =
                android.app.NotificationChannel(
                    PlayerNotificationConstants.CHANNEL_ID,
                    context.getString(R.string.player_notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    setSound(null, null)
                    enableVibration(false)
                    setShowBadge(false)
                }
            manager.createNotificationChannel(channel)
        }

        private fun buildSessionActivityPendingIntent(): PendingIntent {
            val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(PlayerNotificationConstants.EXTRA_OPEN_PLAYER, true)
                }
            return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        private companion object {
            const val ARTWORK_SIZE_PX = 512
            val defaultAccentArgb: Int = 0xFF1E1E1E.toInt()
        }
    }
