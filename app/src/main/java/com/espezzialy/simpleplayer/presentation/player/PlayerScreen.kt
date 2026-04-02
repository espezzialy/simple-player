package com.espezzialy.simpleplayer.presentation.player

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.domain.model.SongsEffect
import com.espezzialy.simpleplayer.domain.model.SongsIntent
import com.espezzialy.simpleplayer.media.PlayerNotificationEntryPoint
import com.espezzialy.simpleplayer.presentation.common.components.TabletNavBarPaddingTop
import com.espezzialy.simpleplayer.presentation.player.components.PlayerMainColumn
import com.espezzialy.simpleplayer.presentation.player.components.PlayerOverflowSheetContent
import com.espezzialy.simpleplayer.presentation.player.components.PlayerPhoneTopBar
import com.espezzialy.simpleplayer.presentation.player.components.PlayerSidePlaylistPanel
import com.espezzialy.simpleplayer.presentation.player.components.PlayerTabletTopBar
import com.espezzialy.simpleplayer.presentation.player.components.rememberIsInMultiWindowMode
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerBreakpoints
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerColors
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme
import dagger.hilt.android.EntryPointAccessors
import android.content.res.Configuration as AndroidConfiguration

@Composable
fun PlayerRoute(
    onBack: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sidePanelUiState by viewModel.sidePanelUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val appContext = context.applicationContext
    val notificationController =
        remember(appContext) {
            EntryPointAccessors.fromApplication(
                appContext,
                PlayerNotificationEntryPoint::class.java,
            ).playerNotificationController()
        }

    val postNotificationsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            val granted =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                postNotificationsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    DisposableEffect(notificationController, viewModel) {
        val transport = notificationController.transport
        transport.onSkipPrevious = {
            viewModel.onIntent(PlayerIntent.SkipPreviousClicked)
        }
        transport.onSkipNext = {
            viewModel.onIntent(PlayerIntent.SkipNextClicked)
        }
        transport.onPlayFromNotification = {
            val s = viewModel.state.value
            if (!s.isPlaying) viewModel.onIntent(PlayerIntent.PlayPauseClicked)
        }
        transport.onPauseFromNotification = {
            val s = viewModel.state.value
            if (s.isPlaying) viewModel.onIntent(PlayerIntent.PlayPauseClicked)
        }
        transport.onSeekFromNotification = { progress ->
            viewModel.onIntent(PlayerIntent.ProgressChanged(progress))
        }
        notificationController.attach(state)
        onDispose {
            transport.clear()
            notificationController.detach()
        }
    }

    LaunchedEffect(state) {
        notificationController.update(state)
    }

    LaunchedEffect(Unit) {
        viewModel.songsSearchEffect.collect { effect ->
            when (effect) {
                is SongsEffect.ShowError -> Unit
            }
        }
    }

    PlayerScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        onNavigateToAlbum = onNavigateToAlbum,
        sidePanelUiState = sidePanelUiState,
        onRetrySearch = { viewModel.onSongsSearchIntent(SongsIntent.RetrySearch) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    state: PlayerUiState,
    onIntent: (PlayerIntent) -> Unit,
    onBack: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    sidePanelUiState: PlayerSidePanelUiState =
        PlayerSidePanelUiState(
            songs = emptyList(),
            panelTitle = null,
            isSearchMode = true,
            isLoading = false,
            errorMessage = null,
            showEmptyQueryHint = true,
        ),
    onRetrySearch: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var overflowSheetVisible by remember { mutableStateOf(false) }
    val overflowSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current
    val isTabletLayout =
        configuration.screenWidthDp >= SimplePlayerBreakpoints.tabletMinWidthDp
    val seekTrackHeight =
        if (isTabletLayout) SimplePlayerDimens.Player.seekTrackHeightTablet else SimplePlayerDimens.Player.seekTrackHeightPhone
    val seekThumbDiameter =
        if (isTabletLayout) SimplePlayerDimens.Player.seekThumbTablet else SimplePlayerDimens.Player.seekThumbPhone
    val isInMultiWindowMode = rememberIsInMultiWindowMode()
    val showSidePanel = isTabletLayout && !isInMultiWindowMode
    val isTabletLandscape =
        configuration.screenHeightDp < configuration.screenWidthDp

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colorScheme.background),
    ) {
        if (isTabletLayout && showSidePanel) {
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(start = SimplePlayerDimens.Player.horizontalPadding),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                ) {
                    PlayerTabletTopBar(
                        onBack = onBack,
                        title = stringResource(R.string.player_now_playing),
                    )
                    PlayerMainColumn(
                        state = state,
                        onIntent = onIntent,
                        artistNameColor = colorScheme.onBackground,
                        artworkSize = SimplePlayerDimens.Player.artworkTablet,
                        contentPaddingTop = SimplePlayerDimens.Player.paddingBelowTopBarTablet,
                        seekTrackHeight = seekTrackHeight,
                        seekThumbDiameter = seekThumbDiameter,
                        isTabletLayout = true,
                    )
                }
                IconButton(
                    onClick = { overflowSheetVisible = true },
                    modifier =
                        Modifier
                            .align(Alignment.Top)
                            .padding(top = TabletNavBarPaddingTop),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = stringResource(R.string.content_desc_menu),
                        tint = colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(SimplePlayerDimens.Player.sidePanelMenuGap))
                PlayerSidePlaylistPanel(
                    sidePanel = sidePanelUiState,
                    currentTrackId = state.trackId,
                    isCurrentPlaying = state.isPlaying,
                    onRetrySearch = onRetrySearch,
                    onSongClick = { song: Song ->
                        onIntent(PlayerIntent.SongSelectedFromPlaylist(song))
                    },
                    modifier =
                        Modifier
                            .then(
                                if (isTabletLandscape) {
                                    Modifier.weight(0.38f)
                                } else {
                                    Modifier.width(SimplePlayerDimens.Player.sidePanelWidth)
                                },
                            )
                            .fillMaxHeight()
                            .padding(end = SimplePlayerDimens.Player.sidePanelEndPadding),
                )
            }
        } else if (isTabletLayout) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = SimplePlayerDimens.Player.horizontalPadding),
            ) {
                PlayerTabletTopBar(
                    onBack = onBack,
                    title = stringResource(R.string.player_now_playing),
                    onOverflowClick = { overflowSheetVisible = true },
                )
                PlayerMainColumn(
                    state = state,
                    onIntent = onIntent,
                    artistNameColor = colorScheme.onBackground,
                    artworkSize = SimplePlayerDimens.Player.artworkTablet,
                    contentPaddingTop = SimplePlayerDimens.Player.paddingBelowTopBarTablet,
                    seekTrackHeight = seekTrackHeight,
                    seekThumbDiameter = seekThumbDiameter,
                    isTabletLayout = true,
                )
            }
        } else {
            Scaffold(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                containerColor = colorScheme.background,
                topBar = {
                    PlayerPhoneTopBar(
                        onBack = onBack,
                        title = stringResource(R.string.player_now_playing),
                        onOverflowClick = { overflowSheetVisible = true },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = SimplePlayerDimens.Player.horizontalPadding),
                ) {
                    PlayerMainColumn(
                        state = state,
                        onIntent = onIntent,
                        artistNameColor = colorScheme.onSurfaceVariant,
                        artworkSize = SimplePlayerDimens.Player.artworkPhone,
                        contentPaddingTop = SimplePlayerDimens.contentBelowTopAppBarPhone,
                        seekTrackHeight = seekTrackHeight,
                        seekThumbDiameter = seekThumbDiameter,
                        isTabletLayout = false,
                    )
                }
            }
        }

        if (overflowSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { overflowSheetVisible = false },
                sheetState = overflowSheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                containerColor = colorScheme.surfaceContainerHigh,
                shape =
                    RoundedCornerShape(
                        topStart = SimplePlayerDimens.Player.bottomSheetTopCorner,
                        topEnd = SimplePlayerDimens.Player.bottomSheetTopCorner,
                    ),
                scrimColor = SimplePlayerColors.ModalSheetScrim,
                tonalElevation = 0.dp,
            ) {
                PlayerOverflowSheetContent(
                    trackName = state.trackName,
                    artistName = state.artistName,
                    showViewAlbum = state.collectionId != null,
                    onViewAlbumClick = {
                        overflowSheetVisible = false
                        state.collectionId?.let(onNavigateToAlbum)
                    },
                )
            }
        }
    }
}

private const val TabletPreviewWidthDp = 1024
private const val TabletPreviewHeightDp = 600

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = TabletPreviewWidthDp,
    heightDp = TabletPreviewHeightDp,
    name = "Player (tablet)",
)
@Composable
private fun PlayerScreenTabletPreview() {
    val tabletConfiguration =
        AndroidConfiguration(LocalConfiguration.current).apply {
            screenWidthDp = TabletPreviewWidthDp
            screenHeightDp = TabletPreviewHeightDp
        }
    CompositionLocalProvider(LocalConfiguration provides tabletConfiguration) {
        SimplePlayerTheme {
            PlayerScreen(
                state =
                    PlayerUiState(
                        trackId = 1L,
                        trackName = "Perfect",
                        artistName = "Ed Sheeran",
                        collectionId = 1L,
                        artworkUrl = null,
                        trackTimeMillis = 260_000L,
                        progress = 0f,
                        isPlaying = true,
                        currentTimeLabel = "0:00",
                        remainingTimeLabel = "-4:20",
                        repeatEnabled = false,
                        totalDurationSeconds = 260,
                    ),
                onIntent = {},
                onBack = {},
                onNavigateToAlbum = {},
                sidePanelUiState =
                    PlayerSidePanelUiState(
                        songs =
                            listOf(
                                Song(
                                    trackId = 1L,
                                    trackName = "Perfect",
                                    artistName = "Ed Sheeran",
                                    collectionName = "÷",
                                    collectionId = 1L,
                                    artworkUrl100 = null,
                                ),
                                Song(
                                    trackId = 2L,
                                    trackName = "Shape of You",
                                    artistName = "Ed Sheeran",
                                    collectionName = "÷",
                                    collectionId = 1L,
                                    artworkUrl100 = null,
                                ),
                            ),
                        panelTitle = "÷",
                        isSearchMode = false,
                        isLoading = false,
                        errorMessage = null,
                        showEmptyQueryHint = false,
                    ),
                onRetrySearch = {},
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 844)
@Composable
private fun PlayerScreenPreview() {
    SimplePlayerTheme {
        PlayerScreen(
            state =
                PlayerUiState(
                    trackId = 1L,
                    trackName = "Get Lucky",
                    artistName = "Daft Punk feat. Pharrell Williams",
                    collectionId = 1L,
                    artworkUrl = null,
                    trackTimeMillis = 260_000L,
                    progress = 0f,
                    isPlaying = false,
                    currentTimeLabel = "0:00",
                    remainingTimeLabel = "-4:20",
                    repeatEnabled = false,
                    totalDurationSeconds = 260,
                ),
            onIntent = {},
            onBack = {},
            onNavigateToAlbum = {},
        )
    }
}
