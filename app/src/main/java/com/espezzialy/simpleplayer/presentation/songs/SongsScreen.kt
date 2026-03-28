package com.espezzialy.simpleplayer.presentation.songs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.presentation.common.CenteredLoading
import com.espezzialy.simpleplayer.presentation.common.ErrorWithRetry
import com.espezzialy.simpleplayer.presentation.common.SimplePlayerDarkPalette
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme

/** Matches the dark Figma layout (Songs / search). */
private val SongsRowSpacing = 16.dp

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
        onNavigateToPlayer = onNavigateToPlayer,
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SimplePlayerDarkPalette.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.songs_title),
            color = SimplePlayerDarkPalette.OnBackground,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        SongsSearchField(
            query = state.query,
            onQueryChange = { onIntent(SongsIntent.QueryChanged(it)) }
        )
        Spacer(modifier = Modifier.height(20.dp))

        when {
            state.isLoading -> {
                CenteredLoading(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            state.errorMessage != null -> {
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
                    color = SimplePlayerDarkPalette.OnBackgroundMuted,
                    fontSize = 15.sp
                )
            }

            state.songs.isEmpty() -> {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.songs_no_results),
                    color = SimplePlayerDarkPalette.OnBackgroundMuted,
                    fontSize = 15.sp
                )
            }

            else -> {
                LazyColumn(
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
                }
            }
        }
    }
}

@Composable
private fun SongsSearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        placeholder = {
            Text(
                text = stringResource(R.string.songs_search_placeholder),
                color = SimplePlayerDarkPalette.OnBackgroundMuted,
                fontSize = 17.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = SimplePlayerDarkPalette.OnBackgroundMuted,
                modifier = Modifier.size(22.dp)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SimplePlayerDarkPalette.Surface,
            unfocusedContainerColor = SimplePlayerDarkPalette.Surface,
            disabledContainerColor = SimplePlayerDarkPalette.Surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = SimplePlayerDarkPalette.OnBackground,
            focusedTextColor = SimplePlayerDarkPalette.OnBackground,
            unfocusedTextColor = SimplePlayerDarkPalette.OnBackground,
            focusedLeadingIconColor = SimplePlayerDarkPalette.OnBackgroundMuted,
            unfocusedLeadingIconColor = SimplePlayerDarkPalette.OnBackgroundMuted,
            focusedPlaceholderColor = SimplePlayerDarkPalette.OnBackgroundMuted,
            unfocusedPlaceholderColor = SimplePlayerDarkPalette.OnBackgroundMuted
        ),
        textStyle = TextStyle(fontSize = 17.sp)
    )
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
