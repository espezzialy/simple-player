package com.espezzialy.simpleplayer.presentation.songs

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme

/** Alinhado ao layout escuro do Figma (Songs / busca). */
private val SongsBg = Color(0xFF000000)
private val SongsOnBg = Color(0xFFFFFFFF)
private val SongsOnBgMuted = Color(0xFFB3B3B3)
private val SongsSearchSurface = Color(0xFF1C1C1E)
private val SongsRowSpacing = 16.dp

@Composable
fun SongsRoute(
    onNavigateToAlbum: (Long) -> Unit,
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
        modifier = modifier
    )
}

@Composable
fun SongsScreen(
    state: SongsState,
    onIntent: (SongsIntent) -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SongsBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Songs",
            color = SongsOnBg,
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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SongsOnBgMuted)
                }
            }

            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.errorMessage,
                        color = SongsOnBgMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onIntent(SongsIntent.RetrySearch) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SongsSearchSurface,
                            contentColor = SongsOnBg
                        )
                    ) {
                        Text("Tentar novamente")
                    }
                }
            }

            state.query.isBlank() -> {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Digite algo para pesquisar músicas na Apple API.",
                    color = SongsOnBgMuted,
                    fontSize = 15.sp
                )
            }

            state.songs.isEmpty() -> {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Nenhum resultado encontrado.",
                    color = SongsOnBgMuted,
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
                text = "Search",
                color = SongsOnBgMuted,
                fontSize = 17.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = SongsOnBgMuted,
                modifier = Modifier.size(22.dp)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SongsSearchSurface,
            unfocusedContainerColor = SongsSearchSurface,
            disabledContainerColor = SongsSearchSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = SongsOnBg,
            focusedTextColor = SongsOnBg,
            unfocusedTextColor = SongsOnBg,
            focusedLeadingIconColor = SongsOnBgMuted,
            unfocusedLeadingIconColor = SongsOnBgMuted,
            focusedPlaceholderColor = SongsOnBgMuted,
            unfocusedPlaceholderColor = SongsOnBgMuted
        ),
        textStyle = TextStyle(fontSize = 17.sp)
    )
}

private val SongArtworkSize = 64.dp

@Composable
private fun SongRow(
    song: Song,
    onViewAlbum: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!song.artworkUrl100.isNullOrBlank()) {
            AsyncImage(
                model = song.artworkUrl100,
                contentDescription = song.trackName,
                modifier = Modifier
                    .size(SongArtworkSize)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(SongArtworkSize)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SongsSearchSurface)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.trackName,
                color = SongsOnBg,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = song.artistName,
                color = SongsOnBgMuted,
                fontSize = 15.sp,
                maxLines = 2
            )
        }
        if (onViewAlbum != null) {
            var menuExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = "Mais opções",
                        tint = SongsOnBgMuted
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = SongsSearchSurface
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Ver álbum",
                                color = SongsOnBg
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onViewAlbum()
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = SongsOnBg
                        )
                    )
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
            onNavigateToAlbum = {}
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
            onNavigateToAlbum = {}
        )
    }
}
