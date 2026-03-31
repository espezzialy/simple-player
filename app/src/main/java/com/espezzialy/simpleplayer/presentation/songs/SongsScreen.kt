package com.espezzialy.simpleplayer.presentation.songs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.presentation.common.CenteredLoading
import com.espezzialy.simpleplayer.presentation.common.ErrorWithRetry
import com.espezzialy.simpleplayer.presentation.common.SongListCellTabletMinWidthDp
import com.espezzialy.simpleplayer.presentation.common.SongsSearchField
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/** Matches the dark Figma layout (Songs / search). */
private val SongsRowSpacing = 16.dp

private val SongsScreenTitlePaddingTopTablet = 32.dp

/** Dispara [SongsIntent.LoadMore] quando o último item visível está a N linhas do fim. */
private const val LOAD_MORE_FROM_END_ITEM_COUNT = 4

@Composable
fun SongsRoute(
    onNavigateToAlbum: (Long) -> Unit,
    onNavigateToPlayer: (Song) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SongsEffect.ShowError -> Unit
            }
        }
    }

    SongsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateToAlbum = onNavigateToAlbum,
        onNavigateToPlayer = { song ->
            viewModel.preparePlayerFromSearch()
            onNavigateToPlayer(song)
        },
        modifier = modifier
    )
}

@Composable
fun SongsScreen(
    state: SongsState,
    onIntent: (SongsIntent) -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    onNavigateToPlayer: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val isTabletLayout =
        LocalConfiguration.current.screenWidthDp >= SongListCellTabletMinWidthDp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(
                top = if (isTabletLayout) SongsScreenTitlePaddingTopTablet else 8.dp,
                bottom = 16.dp
            )
    ) {
        Text(
            text = stringResource(R.string.songs_title),
            style = typography.displaySmall,
            color = colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(20.dp))
        SongsSearchField(
            query = state.query,
            onQueryChange = { onIntent(SongsIntent.QueryChanged(it)) }
        )
        Spacer(modifier = Modifier.height(20.dp))

        when {
            state.isLoading && state.songs.isEmpty() -> {
                CenteredLoading(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            state.errorMessage != null && state.songs.isEmpty() -> {
                ErrorWithRetry(
                    message = state.errorMessage,
                    retryLabel = stringResource(R.string.retry),
                    onRetry = { onIntent(SongsIntent.RetrySearch) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            state.query.isBlank() -> {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.songs_empty_query_hint),
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            state.songs.isEmpty() && !state.isLoading -> {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.songs_no_results),
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            else -> {
                val listState = rememberLazyListState()
                LaunchedEffect(
                    listState,
                    state.query,
                    state.hasMore,
                    state.isLoadingMore,
                    state.isLoading,
                    state.songs.size
                ) {
                    if (!state.hasMore || state.isLoadingMore || state.isLoading) {
                        return@LaunchedEffect
                    }
                    snapshotFlow {
                        val layoutInfo = listState.layoutInfo
                        val total = layoutInfo.totalItemsCount
                        if (total == 0) return@snapshotFlow false
                        val lastVisible =
                            layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        lastVisible >= total - LOAD_MORE_FROM_END_ITEM_COUNT
                    }
                        .distinctUntilChanged()
                        .filter { it }
                        .collect { onIntent(SongsIntent.LoadMore) }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(SongsRowSpacing)
                ) {
                    items(state.songs, key = { it.trackId }) { song ->
                        SongRow(
                            song = song,
                            onSongClick = { onNavigateToPlayer(song) },
                            onViewAlbum = song.collectionId?.let { id -> { onNavigateToAlbum(id) } }
                        )
                    }
                    if (state.isLoadingMore) {
                        item(key = "loading_more") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = colorScheme.primary,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 844)
@Composable
private fun SongsScreenPhonePreview() {
    SimplePlayerTheme {
        SongsScreen(
            state = SongsState(
                query = "wall",
                songs = listOf(
                    Song(
                        1L,
                        "Wall",
                        "Good Kid",
                        "Wall - Single",
                        collectionId = 1L,
                        artworkUrl100 = null
                    ),
                    Song(
                        2L,
                        "Off the Wall",
                        "Michael Jackson",
                        "Off the Wall",
                        collectionId = 2L,
                        artworkUrl100 = null
                    )
                )
            ),
            onIntent = {},
            onNavigateToAlbum = {},
            onNavigateToPlayer = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 1280, heightDp = 800)
@Composable
private fun SongsScreenTabletPreview() {
    SimplePlayerTheme {
        SongsScreen(
            state = SongsState(
                query = "wall",
                songs = listOf(
                    Song(
                        1L,
                        "Wall",
                        "Good Kid",
                        "Wall - Single",
                        collectionId = 1L,
                        artworkUrl100 = null
                    ),
                    Song(
                        2L,
                        "Off the Wall",
                        "Michael Jackson",
                        "Off the Wall",
                        collectionId = 2L,
                        artworkUrl100 = null
                    )
                )
            ),
            onIntent = {},
            onNavigateToAlbum = {},
            onNavigateToPlayer = {}
        )
    }
}
