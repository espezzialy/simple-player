package com.espezzialy.simpleplayer.presentation.player

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.core.app.MultiWindowModeChangedInfo
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.core.media.toItunesArtwork600
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.presentation.songs.SongsEffect
import com.espezzialy.simpleplayer.presentation.common.PhoneContentPaddingBelowTopBar
import com.espezzialy.simpleplayer.presentation.common.TabletBackIconButton
import com.espezzialy.simpleplayer.presentation.common.TabletNavBarPaddingTop
import com.espezzialy.simpleplayer.presentation.songs.SongsIntent
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme

private const val PlayerTabletMinWidthDp = 600

/** Figma tablet (ex.: 834px wide): painel lateral com largura fixa 288px. */
private val PlayerSidePanelWidth = 288.dp

/** Figma: phone artwork 264×264. */
private val PlayerArtworkSizePhone = 264.dp

/** Figma: tablet artwork 286×286. */
private val PlayerArtworkSizeTablet = 286.dp

/** Figma: padding above player content on tablet. */
private val PlayerContentPaddingTopTablet = 62.dp

/** Espaço entre a barra (voltar + título) e o conteúdo principal no tablet. */
private val PlayerTabletMainPaddingBelowTopBar = 16.dp

/** Espaço entre o botão voltar e o título "Now playing". */
private val PlayerTopBarTitleInsetAfterBackTablet = 10.dp

private val PlayerSeekTrackHeightPhone = 4.dp
private val PlayerSeekTrackHeightTablet = 8.dp

private val PlayerSeekThumbDiameterPhone = 16.dp
private val PlayerSeekThumbDiameterTablet = 24.dp

@Composable
fun PlayerRoute(
    onBack: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sidePanelUiState by viewModel.sidePanelUiState.collectAsStateWithLifecycle()

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
        onRetrySearch = { viewModel.onSongsSearchIntent(SongsIntent.RetrySearch) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    state: PlayerState,
    onIntent: (PlayerIntent) -> Unit,
    onBack: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    sidePanelUiState: PlayerSidePanelUiState = PlayerSidePanelUiState(
        songs = emptyList(),
        panelTitle = null,
        isSearchMode = true,
        isLoading = false,
        errorMessage = null,
        showEmptyQueryHint = true
    ),
    onRetrySearch: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var overflowSheetVisible by remember { mutableStateOf(false) }
    val overflowSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current
    val isTabletLayout =
        configuration.screenWidthDp >= PlayerTabletMinWidthDp
    val seekTrackHeight =
        if (isTabletLayout) PlayerSeekTrackHeightTablet else PlayerSeekTrackHeightPhone
    val seekThumbDiameter =
        if (isTabletLayout) PlayerSeekThumbDiameterTablet else PlayerSeekThumbDiameterPhone
    val isInMultiWindowMode = rememberIsInMultiWindowMode()
    val showSidePanel = isTabletLayout && !isInMultiWindowMode
    val isTabletLandscape =
        configuration.screenHeightDp < configuration.screenWidthDp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        if (isTabletLayout && showSidePanel) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(start = 24.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    PlayerTabletTopBar(
                        onBack = onBack,
                        title = stringResource(R.string.player_now_playing)
                    )
                    PlayerMainColumn(
                        state = state,
                        onIntent = onIntent,
                        artistNameColor = colorScheme.onBackground,
                        artworkSize = PlayerArtworkSizeTablet,
                        contentPaddingTop = PlayerTabletMainPaddingBelowTopBar,
                        seekTrackHeight = seekTrackHeight,
                        seekThumbDiameter = seekThumbDiameter,
                        isTabletLayout = true
                    )
                }
                IconButton(
                    onClick = { overflowSheetVisible = true },
                    modifier = Modifier
                        .align(Alignment.Top)
                        .padding(top = TabletNavBarPaddingTop)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = stringResource(R.string.content_desc_menu),
                        tint = colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                PlayerSidePlaylistPanel(
                    sidePanel = sidePanelUiState,
                    currentTrackId = state.trackId,
                    isCurrentPlaying = state.isPlaying,
                    onRetrySearch = onRetrySearch,
                    onSongClick = { song: Song ->
                        onIntent(PlayerIntent.SongSelectedFromPlaylist(song))
                    },
                    modifier = Modifier
                        .then(
                            if (isTabletLandscape) {
                                Modifier.weight(0.38f)
                            } else {
                                Modifier.width(PlayerSidePanelWidth)
                            }
                        )
                        .fillMaxHeight()
                        .padding(end = 24.dp)
                )
            }
        } else if (isTabletLayout) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
            ) {
                PlayerTabletTopBar(
                    onBack = onBack,
                    title = stringResource(R.string.player_now_playing),
                    onOverflowClick = { overflowSheetVisible = true }
                )
                PlayerMainColumn(
                    state = state,
                    onIntent = onIntent,
                    artistNameColor = colorScheme.onBackground,
                    artworkSize = PlayerArtworkSizeTablet,
                    contentPaddingTop = PlayerTabletMainPaddingBelowTopBar,
                    seekTrackHeight = seekTrackHeight,
                    seekThumbDiameter = seekThumbDiameter,
                    isTabletLayout = true
                )
            }
        } else {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                containerColor = colorScheme.background,
                topBar = {
                    PlayerPhoneTopBar(
                        onBack = onBack,
                        title = stringResource(R.string.player_now_playing),
                        onOverflowClick = { overflowSheetVisible = true }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                ) {
                    PlayerMainColumn(
                        state = state,
                        onIntent = onIntent,
                        artistNameColor = colorScheme.onSurfaceVariant,
                        artworkSize = PlayerArtworkSizePhone,
                        contentPaddingTop = PhoneContentPaddingBelowTopBar,
                        seekTrackHeight = seekTrackHeight,
                        seekThumbDiameter = seekThumbDiameter,
                        isTabletLayout = false
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
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                scrimColor = Color.Black.copy(alpha = 0.45f),
                tonalElevation = 0.dp
            ) {
                PlayerOverflowSheetContent(
                    trackName = state.trackName,
                    artistName = state.artistName,
                    showViewAlbum = state.collectionId != null,
                    onViewAlbumClick = {
                        overflowSheetVisible = false
                        state.collectionId?.let(onNavigateToAlbum)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerPhoneTopBar(
    onBack: () -> Unit,
    title: String,
    onOverflowClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    TopAppBar(
        title = {
            Text(
                text = title,
                style = typography.titleLarge,
                color = colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = stringResource(R.string.content_desc_back),
                    tint = colorScheme.onBackground
                )
            }
        },
        actions = {
            IconButton(onClick = onOverflowClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = stringResource(R.string.content_desc_menu),
                    tint = colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.background,
            titleContentColor = colorScheme.onBackground,
            navigationIconContentColor = colorScheme.onBackground
        )
    )
}

@Composable
private fun PlayerTabletTopBar(
    onBack: () -> Unit,
    title: String,
    onOverflowClick: (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = TabletNavBarPaddingTop),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabletBackIconButton(
            onClick = onBack,
            contentDescription = stringResource(R.string.content_desc_back),
            tint = colorScheme.onBackground,
            painter = painterResource(R.drawable.ic_arrow_left),
            iconSize = 28.dp
        )
        Spacer(modifier = Modifier.width(PlayerTopBarTitleInsetAfterBackTablet))
        Text(
            text = title,
            style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start
        )
        if (onOverflowClick != null) {
            IconButton(onClick = onOverflowClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = stringResource(R.string.content_desc_menu),
                    tint = colorScheme.onSurfaceVariant
                )
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

private tailrec fun findComponentActivity(context: Context): ComponentActivity? {
    return when (context) {
        is ComponentActivity -> context
        is ContextWrapper -> findComponentActivity(context.baseContext)
        else -> null
    }
}

@Composable
private fun rememberIsInMultiWindowMode(): Boolean {
    val context = LocalContext.current
    val activity = remember(context) { findComponentActivity(context) }
    var inMultiWindow by remember(activity) {
        mutableStateOf(activity?.isInMultiWindowMode == true)
    }
    DisposableEffect(activity) {
        val act = activity ?: return@DisposableEffect onDispose { }
        val listener = Consumer<MultiWindowModeChangedInfo> { info ->
            inMultiWindow = info.isInMultiWindowMode
        }
        act.addOnMultiWindowModeChangedListener(listener)
        inMultiWindow = act.isInMultiWindowMode
        onDispose {
            act.removeOnMultiWindowModeChangedListener(listener)
        }
    }
    return inMultiWindow
}

@Composable
private fun PlayerMainColumn(
    state: PlayerState,
    onIntent: (PlayerIntent) -> Unit,
    artistNameColor: Color,
    artworkSize: Dp,
    contentPaddingTop: Dp,
    seekTrackHeight: Dp,
    seekThumbDiameter: Dp,
    isTabletLayout: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val trackNameStyle = if (isTabletLayout) {
        typography.headlineSmall.copy(fontSize = 40.sp, lineHeight = 48.sp)
    } else {
        typography.headlineSmall.copy(fontSize = 32.sp, lineHeight = 38.4.sp)
    }
    val artistNameStyle = if (isTabletLayout) {
        typography.bodyLarge.copy(fontSize = 20.sp, lineHeight = 24.sp)
    } else {
        typography.bodyLarge.copy(fontSize = 16.sp, lineHeight = 19.2.sp)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPaddingTop),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isTabletLayout) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PlayerArtwork(
                    artworkUrl = state.artworkUrl,
                    trackName = state.trackName,
                    size = artworkSize
                )
            }
        } else {
            PlayerArtwork(
                artworkUrl = state.artworkUrl,
                trackName = state.trackName,
                size = artworkSize
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = state.trackName,
                style = trackNameStyle,
                color = colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.artistName,
                style = artistNameStyle,
                color = artistNameColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(28.dp))
            PlayerSeekSection(
                progress = state.progress,
                currentLabel = state.currentTimeLabel,
                remainingLabel = state.remainingTimeLabel,
                onProgressChange = { onIntent(PlayerIntent.ProgressChanged(it)) },
                trackHeight = seekTrackHeight,
                thumbDiameter = seekThumbDiameter
            )
        }
        Spacer(modifier = Modifier.height(if (isTabletLayout) 24.dp else 20.dp))
        PlayerTransportControls(
            isPlaying = state.isPlaying,
            repeatEnabled = state.repeatEnabled,
            onPlayPause = { onIntent(PlayerIntent.PlayPauseClicked) },
            onPrevious = { onIntent(PlayerIntent.SkipPreviousClicked) },
            onNext = { onIntent(PlayerIntent.SkipNextClicked) },
            onRepeat = { onIntent(PlayerIntent.RepeatClicked) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PlayerArtwork(artworkUrl: String?, trackName: String, size: Dp) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        if (!artworkUrl.isNullOrBlank()) {
            AsyncImage(
                model = artworkUrl.toItunesArtwork600() ?: artworkUrl,
                contentDescription = trackName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun PlayerSeekSection(
    progress: Float,
    currentLabel: String,
    remainingLabel: String,
    onProgressChange: (Float) -> Unit,
    trackHeight: Dp,
    thumbDiameter: Dp
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(modifier = Modifier.fillMaxWidth()) {
        PlayerSeekBar(
            progress = progress,
            onProgressChange = onProgressChange,
            modifier = Modifier.fillMaxWidth(),
            trackHeight = trackHeight,
            thumbDiameter = thumbDiameter
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentLabel,
                style = typography.labelSmall,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = remainingLabel,
                style = typography.labelSmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlayerTransportControls(
    isPlaying: Boolean,
    repeatEnabled: Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onRepeat: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val pauseDesc = stringResource(R.string.content_desc_pause)
    val playDesc = stringResource(R.string.content_desc_play)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onPlayPause,
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = colorScheme.surfaceContainerHigh
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) pauseDesc else playDesc,
                        tint = colorScheme.onSurface,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            IconButton(onClick = onPrevious) {
                Icon(
                    painter = painterResource(R.drawable.ic_backward_bar_fill),
                    contentDescription = stringResource(R.string.content_desc_previous_track),
                    tint = colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = onNext) {
                Icon(
                    painter = painterResource(R.drawable.ic_forward_bar_fill),
                    contentDescription = stringResource(R.string.content_desc_next_track),
                    tint = colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        IconButton(onClick = onRepeat) {
            Icon(
                painter = painterResource(R.drawable.ic_play_on_repeat),
                contentDescription = stringResource(R.string.content_desc_repeat),
                tint = if (repeatEnabled) {
                    colorScheme.onSurface
                } else {
                    colorScheme.onSurface
                },
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 1024, heightDp = 600, name = "Player (tablet)")
@Composable
private fun PlayerScreenTabletPreview() {
    SimplePlayerTheme {
        PlayerScreen(
            state = PlayerState(
                trackId = 1L,
                trackName = "Perfect",
                artistName = "Ed Sheeran",
                collectionId = 1L,
                artworkUrl = null,
                progress = 0f,
                isPlaying = true,
                currentTimeLabel = "0:00",
                remainingTimeLabel = "-4:20",
                repeatEnabled = false
            ),
            onIntent = {},
            onBack = {},
            onNavigateToAlbum = {},
            sidePanelUiState = PlayerSidePanelUiState(
                songs = listOf(
                    Song(
                        trackId = 1L,
                        trackName = "Perfect",
                        artistName = "Ed Sheeran",
                        collectionName = "÷",
                        collectionId = 1L,
                        artworkUrl100 = null
                    ),
                    Song(
                        trackId = 2L,
                        trackName = "Shape of You",
                        artistName = "Ed Sheeran",
                        collectionName = "÷",
                        collectionId = 1L,
                        artworkUrl100 = null
                    )
                ),
                panelTitle = "÷",
                isSearchMode = false,
                isLoading = false,
                errorMessage = null,
                showEmptyQueryHint = false
            ),
            onRetrySearch = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 844)
@Composable
private fun PlayerScreenPreview() {
    SimplePlayerTheme {
        PlayerScreen(
            state = PlayerState(
                trackId = 1L,
                trackName = "Get Lucky",
                artistName = "Daft Punk feat. Pharrell Williams",
                collectionId = 1L,
                artworkUrl = null,
                progress = 0f,
                isPlaying = false,
                currentTimeLabel = "0:00",
                remainingTimeLabel = "-4:20",
                repeatEnabled = false
            ),
            onIntent = {},
            onBack = {},
            onNavigateToAlbum = {}
        )
    }
}
