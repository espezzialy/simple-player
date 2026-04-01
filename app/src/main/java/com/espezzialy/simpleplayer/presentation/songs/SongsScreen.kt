@file:OptIn(androidx.compose.material.ExperimentalMaterialApi::class)

package com.espezzialy.simpleplayer.presentation.songs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.domain.model.SongsEffect
import com.espezzialy.simpleplayer.domain.model.SongsIntent
import com.espezzialy.simpleplayer.domain.model.SongsUiState
import com.espezzialy.simpleplayer.presentation.common.components.CenteredLoading
import com.espezzialy.simpleplayer.presentation.common.components.ErrorWithRetry
import com.espezzialy.simpleplayer.presentation.common.components.SongsSearchField
import com.espezzialy.simpleplayer.presentation.songs.components.SongRow
import com.espezzialy.simpleplayer.presentation.songs.components.SongsPullRefreshIndicator
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerBreakpoints
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

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
        onNavigateToPlayer = { song, fromRecentSection ->
            viewModel.onSongSelectedForPlayer(song, fromRecentSection) {
                onNavigateToPlayer(song)
            }
        },
        modifier = modifier
    )
}

@Composable
fun SongsScreen(
    state: SongsUiState,
    onIntent: (SongsIntent) -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    onNavigateToPlayer: (Song, fromRecentSection: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val isTabletLayout =
        LocalConfiguration.current.screenWidthDp >= SimplePlayerBreakpoints.tabletMinWidthDp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = SimplePlayerDimens.screenHorizontalPadding)
            .padding(
                top = if (isTabletLayout) {
                    SimplePlayerDimens.Songs.screenTitlePaddingTopTablet
                } else {
                    SimplePlayerDimens.Songs.screenTitlePaddingTopPhone
                },
                bottom = SimplePlayerDimens.Songs.screenBottomPadding
            )
    ) {
        Text(
            text = stringResource(R.string.songs_title),
            style = typography.displaySmall,
            color = colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(SimplePlayerDimens.Songs.afterTitleSpacing))
        SongsSearchField(
            query = state.query,
            onQueryChange = { onIntent(SongsIntent.QueryChanged(it)) }
        )
        Spacer(modifier = Modifier.height(SimplePlayerDimens.Songs.afterTitleSpacing))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                state.isLoading && state.songs.isEmpty() -> {
                    CenteredLoading(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillMaxWidth()
                    )
                }

                state.errorMessage != null && state.songs.isEmpty() -> {
                    ErrorWithRetry(
                        message = state.errorMessage,
                        retryLabel = stringResource(R.string.retry),
                        onRetry = { onIntent(SongsIntent.RetrySearch) },
                        modifier = Modifier
                            .fillMaxSize()
                            .fillMaxWidth()
                    )
                }

                state.query.isBlank() -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (state.recentSongs.isEmpty()) {
                            Text(
                                text = stringResource(R.string.songs_empty_query_hint),
                                style = typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.songs_recent_title),
                                    style = typography.titleMedium,
                                    color = colorScheme.onBackground,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = stringResource(R.string.songs_clear_recent),
                                    modifier = Modifier
                                        .clickable {
                                            onIntent(SongsIntent.ClearRecentSongs)
                                        },
                                    style = typography.bodyMedium.copy(
                                        textDecoration = TextDecoration.Underline
                                    ),
                                    color = colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(SimplePlayerDimens.Songs.recentHeaderSpacing))
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(bottom = SimplePlayerDimens.Songs.listContentBottom),
                                verticalArrangement = Arrangement.spacedBy(SimplePlayerDimens.Songs.listVerticalItemSpacing)
                            ) {
                                items(state.recentSongs, key = { it.trackId }) { song ->
                                    SongRow(
                                        song = song,
                                        onSongClick = { onNavigateToPlayer(song, true) },
                                        onViewAlbum = song.collectionId?.let { id ->
                                            { onNavigateToAlbum(id) }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                state.songs.isEmpty() && !state.isLoading -> {
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        text = stringResource(R.string.songs_no_results),
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    val listState = rememberLazyListState()
                    val pullRefreshEnabled =
                        state.query.isNotBlank() && !(state.isLoading && state.songs.isEmpty())
                    val pullRefreshState = rememberPullRefreshState(
                        refreshing = state.isRefreshing,
                        onRefresh = { onIntent(SongsIntent.Refresh) }
                    )
                    val pullRefreshLabel = stringResource(R.string.content_desc_pull_to_refresh)

                    Box(modifier = Modifier.fillMaxSize()) {
                        LaunchedEffect(
                            listState,
                            state.query,
                            state.hasMore,
                            state.isLoadingMore,
                            state.isLoading,
                            state.isRefreshing,
                            state.songs.size
                        ) {
                            if (!state.hasMore || state.isLoadingMore || state.isLoading ||
                                state.isRefreshing
                            ) {
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
                                .fillMaxSize()
                                .fillMaxWidth()
                                .then(
                                    if (pullRefreshEnabled) {
                                        Modifier.pullRefresh(pullRefreshState)
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentPadding = PaddingValues(bottom = SimplePlayerDimens.Songs.listContentBottom),
                            verticalArrangement = Arrangement.spacedBy(SimplePlayerDimens.Songs.listVerticalItemSpacing)
                        ) {
                            items(state.songs, key = { it.trackId }) { song ->
                                SongRow(
                                    song = song,
                                    onSongClick = { onNavigateToPlayer(song, false) },
                                    onViewAlbum = song.collectionId?.let { id -> { onNavigateToAlbum(id) } }
                                )
                            }
                            if (state.isLoadingMore) {
                                item(key = "loading_more") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = SimplePlayerDimens.Songs.loadMoreIndicatorPadding),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(SimplePlayerDimens.Songs.loadMoreProgressSize),
                                            color = colorScheme.primary,
                                            strokeWidth = SimplePlayerDimens.Songs.loadMoreProgressStrokeWidth
                                        )
                                    }
                                }
                            }
                        }

                        if (pullRefreshEnabled) {
                            SongsPullRefreshIndicator(
                                pullRefreshState = pullRefreshState,
                                isRefreshing = state.isRefreshing,
                                contentDescription = pullRefreshLabel,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = SimplePlayerDimens.Songs.pullRefreshIndicatorTop)
                            )
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
            state = SongsUiState(
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
            onNavigateToPlayer = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 1280, heightDp = 800)
@Composable
private fun SongsScreenTabletPreview() {
    SimplePlayerTheme {
        SongsScreen(
            state = SongsUiState(
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
            onNavigateToPlayer = { _, _ -> }
        )
    }
}
