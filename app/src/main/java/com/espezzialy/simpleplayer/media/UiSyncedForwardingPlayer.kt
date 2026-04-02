package com.espezzialy.simpleplayer.media

import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.espezzialy.simpleplayer.presentation.player.PlayerUiState

class UiSyncedForwardingPlayer(
    exoPlayer: ExoPlayer,
    private val getUiState: () -> PlayerUiState?,
    private val transport: PlayerNotificationTransport,
) : ForwardingPlayer(exoPlayer) {
    /**
     * The wrapped [ExoPlayer] has no real media, so it stays IDLE and reports [Player.isPlaying]
     * false. [ForwardingPlayer] forwards those listener callbacks to [PlayerNotificationManager],
     * which then calls [pause] and cancels a mock "play" from the notification. We drop delegate
     * "not playing / IDLE" signals whenever the app UI state says we are playing a track.
     */
    override fun addListener(listener: Player.Listener) {
        super.addListener(PlayerListenerForUiFilter(listener, getUiState))
    }

    override fun getDuration(): Long = durationMsFromUiState(getUiState())

    override fun getCurrentPosition(): Long {
        val d = duration
        return currentPositionMs(getUiState(), d)
    }

    override fun getBufferedPosition(): Long {
        val d = duration
        if (d == C.TIME_UNSET || d <= 0L) return 0L
        return d
    }

    override fun getPlaybackState(): Int = playbackStateFromUiState(getUiState())

    override fun isPlaying(): Boolean = getUiState()?.isPlaying == true

    override fun getPlayWhenReady(): Boolean = getUiState()?.isPlaying == true

    override fun isCurrentMediaItemSeekable(): Boolean = getUiState()?.trackId?.let { it > 0L } == true

    override fun play() {
        transport.onPlayFromNotification?.invoke()
    }

    override fun pause() {
        transport.onPauseFromNotification?.invoke()
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        if (playWhenReady) {
            transport.onPlayFromNotification?.invoke()
        } else {
            transport.onPauseFromNotification?.invoke()
        }
    }

    override fun seekTo(positionMs: Long) {
        val d = duration
        if (d == C.TIME_UNSET || d <= 0L) return
        val progress = (positionMs.toFloat() / d.toFloat()).coerceIn(0f, 1f)
        transport.onSeekFromNotification?.invoke(progress)
    }

    override fun seekTo(
        mediaItemIndex: Int,
        positionMs: Long,
    ) {
        seekTo(positionMs)
    }

    override fun seekToPrevious() {
        transport.onSkipPrevious?.invoke()
    }

    override fun seekToNext() {
        transport.onSkipNext?.invoke()
    }

    override fun seekToPreviousMediaItem() {
        transport.onSkipPrevious?.invoke()
    }

    override fun seekToNextMediaItem() {
        transport.onSkipNext?.invoke()
    }
}

private class PlayerListenerForUiFilter(
    private val delegate: Player.Listener,
    private val getUiState: () -> PlayerUiState?,
) : Player.Listener by delegate {
    private fun shouldSuppressDelegateIdleWhileUiPlaying(): Boolean {
        val ui = getUiState() ?: return false
        return ui.isPlaying && ui.trackId > 0L
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (!isPlaying && shouldSuppressDelegateIdleWhileUiPlaying()) return
        delegate.onIsPlayingChanged(isPlaying)
    }

    override fun onPlayWhenReadyChanged(
        playWhenReady: Boolean,
        reason: Int,
    ) {
        if (!playWhenReady && shouldSuppressDelegateIdleWhileUiPlaying()) return
        delegate.onPlayWhenReadyChanged(playWhenReady, reason)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_IDLE && shouldSuppressDelegateIdleWhileUiPlaying()) return
        delegate.onPlaybackStateChanged(playbackState)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onPlayerStateChanged(
        playWhenReady: Boolean,
        playbackState: Int,
    ) {
        if (playbackState == Player.STATE_IDLE && shouldSuppressDelegateIdleWhileUiPlaying()) return
        delegate.onPlayerStateChanged(playWhenReady, playbackState)
    }
}
