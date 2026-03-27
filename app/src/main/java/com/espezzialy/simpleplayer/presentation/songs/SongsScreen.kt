package com.espezzialy.simpleplayer.presentation.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme

@Composable
fun SongsRoute(
    viewModel: SongsViewModel,
    modifier: Modifier = Modifier
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
        modifier = modifier
    )
}

@Composable
fun SongsScreen(
    state: SongsState,
    onIntent: (SongsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = if (LocalConfiguration.current.screenWidthDp >= 840) 2 else 1

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Songs",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = state.query,
            onValueChange = { onIntent(SongsIntent.QueryChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Search songs...") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage != null -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { onIntent(SongsIntent.RetrySearch) }) {
                        Text("Tentar novamente")
                    }
                }
            }

            state.query.isBlank() -> {
                Text(
                    text = "Digite algo para pesquisar musicas na Apple API.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            state.songs.isEmpty() -> {
                Text(
                    text = "Nenhum resultado encontrado.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.songs, key = { it.trackId }) { song ->
                        SongCard(song = song)
                    }
                }
            }
        }
    }
}

@Composable
private fun SongCard(song: Song) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = song.trackName,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = song.artistName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = song.collectionName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SongsScreenPhonePreview() {
    SimplePlayerTheme {
        SongsScreen(
            state = SongsState(
                query = "wall",
                songs = listOf(
                    Song(1L, "Wall", "Good Kid", "Wall - Single"),
                    Song(2L, "Off the Wall", "Michael Jackson", "Off the Wall")
                )
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun SongsScreenTabletPreview() {
    SimplePlayerTheme {
        SongsScreen(
            state = SongsState(
                query = "wall",
                songs = listOf(
                    Song(1L, "Wall", "Good Kid", "Wall - Single"),
                    Song(2L, "Off the Wall", "Michael Jackson", "Off the Wall")
                )
            ),
            onIntent = {}
        )
    }
}
