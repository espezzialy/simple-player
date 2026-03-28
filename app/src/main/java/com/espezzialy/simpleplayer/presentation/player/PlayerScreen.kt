package com.espezzialy.simpleplayer.presentation.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.espezzialy.simpleplayer.core.media.toItunesArtwork600
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme

private val PlayerBg = Color(0xFF000000)
private val PlayerOnBg = Color(0xFFFFFFFF)
private val PlayerMuted = Color(0xFFB3B3B3)
private val PlayerControlSurface = Color(0xFF2C2C2C)

@Composable
fun PlayerRoute(
    onBack: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PlayerScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        onNavigateToAlbum = onNavigateToAlbum
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    state: PlayerState,
    onIntent: (PlayerIntent) -> Unit,
    onBack: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var overflowSheetVisible by remember { mutableStateOf(false) }
    val overflowSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PlayerBg)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            containerColor = PlayerBg,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Now playing",
                            color = PlayerOnBg,
                            fontWeight = FontWeight.Medium,
                            fontSize = 17.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_left),
                                contentDescription = "Voltar",
                                tint = PlayerOnBg
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { overflowSheetVisible = true }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_more),
                                contentDescription = "Menu",
                                tint = PlayerMuted
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = PlayerBg,
                        titleContentColor = PlayerOnBg,
                        navigationIconContentColor = PlayerOnBg,
                        actionIconContentColor = PlayerOnBg
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                PlayerArtwork(artworkUrl = state.artworkUrl, trackName = state.trackName)
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = state.trackName,
                    color = PlayerOnBg,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.artistName,
                    color = PlayerMuted,
                    fontSize = 17.sp,
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
                    onProgressChange = { onIntent(PlayerIntent.ProgressChanged(it)) }
                )
                Spacer(modifier = Modifier.weight(1f))
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

        if (overflowSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { overflowSheetVisible = false },
                sheetState = overflowSheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                containerColor = PlayerControlSurface,
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

@Composable
private fun PlayerArtwork(artworkUrl: String?, trackName: String) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(shape)
            .background(PlayerControlSurface),
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
    onProgressChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        PlayerSeekBar(
            progress = progress,
            onProgressChange = onProgressChange,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentLabel,
                color = PlayerMuted,
                fontSize = 13.sp
            )
            Text(
                text = remainingLabel,
                color = PlayerMuted,
                fontSize = 13.sp
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = onPlayPause,
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = PlayerControlSurface
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                    tint = PlayerOnBg,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        IconButton(onClick = onPrevious) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = "Faixa anterior",
                tint = PlayerOnBg,
                modifier = Modifier.size(32.dp)
            )
        }
        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "Próxima faixa",
                tint = PlayerOnBg,
                modifier = Modifier.size(32.dp)
            )
        }
        IconButton(onClick = onRepeat) {
            Icon(
                imageVector = Icons.Filled.Repeat,
                contentDescription = "Repetir",
                tint = if (repeatEnabled) PlayerOnBg else PlayerMuted,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 844)
@Composable
private fun PlayerScreenPreview() {
    SimplePlayerTheme {
        PlayerScreen(
            state = PlayerState(
                trackName = "Get Lucky",
                artistName = "Daft Punk feat. Pharrell Williams",
                collectionId = 1L,
                artworkUrl = null,
                progress = 86f / 260f,
                isPlaying = false,
                currentTimeLabel = "1:26",
                remainingTimeLabel = "-2:54",
                repeatEnabled = false
            ),
            onIntent = {},
            onBack = {},
            onNavigateToAlbum = {}
        )
    }
}
