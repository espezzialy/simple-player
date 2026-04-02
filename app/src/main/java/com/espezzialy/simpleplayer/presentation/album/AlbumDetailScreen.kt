package com.espezzialy.simpleplayer.presentation.album

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.AlbumTrack
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.presentation.album.components.AlbumDetailListContent
import com.espezzialy.simpleplayer.presentation.album.components.AlbumPhoneTopBar
import com.espezzialy.simpleplayer.presentation.album.components.AlbumTabletTopBar
import com.espezzialy.simpleplayer.presentation.album.components.toSong
import com.espezzialy.simpleplayer.presentation.common.components.CenteredLoading
import com.espezzialy.simpleplayer.presentation.common.components.ErrorWithRetry
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerBreakpoints
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme
import android.content.res.Configuration as AndroidConfiguration

@Composable
fun AlbumDetailRoute(
    onBack: () -> Unit,
    onNavigateToPlayer: (Song) -> Unit,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AlbumDetailScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        onNavigateToPlayer = onNavigateToPlayer,
        onBeforeNavigateToPlayerFromAlbum = viewModel::preparePlayerFromAlbum,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    state: AlbumDetailUiState,
    onIntent: (AlbumDetailIntent) -> Unit,
    onBack: () -> Unit,
    onNavigateToPlayer: (Song) -> Unit,
    onBeforeNavigateToPlayerFromAlbum: (AlbumDetail) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val fallbackTitle = stringResource(R.string.album_fallback_title)
    val title = state.album?.title.orEmpty().ifBlank { fallbackTitle }
    val colorScheme = MaterialTheme.colorScheme
    val isTabletLayout =
        LocalConfiguration.current.screenWidthDp >= SimplePlayerBreakpoints.tabletMinWidthDp

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        topBar = {
            if (isTabletLayout) {
                AlbumTabletTopBar(onBack = onBack, title = title)
            } else {
                AlbumPhoneTopBar(title = title, onBack = onBack)
            }
        },
    ) { innerPadding ->
        when {
            state.isLoading -> {
                CenteredLoading(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                )
            }

            state.errorMessage != null -> {
                ErrorWithRetry(
                    message = state.errorMessage,
                    retryLabel = stringResource(R.string.retry),
                    onRetry = { onIntent(AlbumDetailIntent.Retry) },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = SimplePlayerDimens.screenHorizontalPadding),
                )
            }

            state.album != null -> {
                val album = state.album
                AlbumDetailListContent(
                    album = album,
                    onSongClick = { track: AlbumTrack ->
                        onBeforeNavigateToPlayerFromAlbum(album)
                        onNavigateToPlayer(track.toSong(album))
                    },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                )
            }
        }
    }
}

private const val TabletPreviewWidthDp = 900
private const val TabletPreviewHeightDp = 600
private const val PhonePreviewWidthDp = 390
private const val PhonePreviewHeightDp = 844

@Preview(
    showBackground = true,
    widthDp = TabletPreviewWidthDp,
    heightDp = TabletPreviewHeightDp,
    name = "Album (tablet)",
)
@Composable
private fun AlbumDetailScreenTabletPreview() {
    val tabletConfiguration =
        AndroidConfiguration(LocalConfiguration.current).apply {
            screenWidthDp = TabletPreviewWidthDp
            screenHeightDp = TabletPreviewHeightDp
        }
    CompositionLocalProvider(LocalConfiguration provides tabletConfiguration) {
        SimplePlayerTheme {
            AlbumDetailScreen(
                state =
                    AlbumDetailUiState(
                        isLoading = false,
                        album =
                            AlbumDetail(
                                collectionId = 1L,
                                title = "Divide",
                                artistName = "Ed Sheeran",
                                artworkUrl = null,
                                tracks =
                                    listOf(
                                        AlbumTrack(1L, "Perfect", "Ed Sheeran", null),
                                        AlbumTrack(2L, "Shape of You", "Ed Sheeran", null),
                                    ),
                            ),
                    ),
                onIntent = {},
                onBack = {},
                onNavigateToPlayer = {},
            )
        }
    }
}

@Preview(showBackground = true, widthDp = PhonePreviewWidthDp, heightDp = PhonePreviewHeightDp)
@Composable
private fun AlbumDetailScreenPreview() {
    val phoneConfiguration =
        AndroidConfiguration(LocalConfiguration.current).apply {
            screenWidthDp = PhonePreviewWidthDp
            screenHeightDp = PhonePreviewHeightDp
        }
    CompositionLocalProvider(LocalConfiguration provides phoneConfiguration) {
        SimplePlayerTheme {
            AlbumDetailScreen(
                state =
                    AlbumDetailUiState(
                        isLoading = false,
                        album =
                            AlbumDetail(
                                collectionId = 1L,
                                title = "Random Access Memories",
                                artistName = "Daft Punk",
                                artworkUrl = null,
                                tracks =
                                    listOf(
                                        AlbumTrack(1L, "Give Life Back to Music", "Daft Punk", null),
                                        AlbumTrack(2L, "The Game of Love", "Daft Punk", null),
                                    ),
                            ),
                    ),
                onIntent = {},
                onBack = {},
                onNavigateToPlayer = {},
            )
        }
    }
}
