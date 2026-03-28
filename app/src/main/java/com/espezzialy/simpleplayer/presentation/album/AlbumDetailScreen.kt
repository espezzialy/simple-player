package com.espezzialy.simpleplayer.presentation.album

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.espezzialy.simpleplayer.core.media.toItunesArtwork600
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.AlbumTrack
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme

private val AlbumBackground = Color(0xFF000000)
private val AlbumOnBackground = Color(0xFFFFFFFF)
private val AlbumOnBackgroundMuted = Color(0xFFB3B3B3)
private val HeroArtworkSize = 240.dp
private val RowThumbSize = 56.dp

@Composable
fun AlbumDetailRoute(
    onBack: () -> Unit,
    viewModel: AlbumDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AlbumDetailScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    state: AlbumDetailState,
    onIntent: (AlbumDetailIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = state.album?.title.orEmpty().ifBlank { "Álbum" }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AlbumBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        color = AlbumOnBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = AlbumOnBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AlbumBackground,
                    titleContentColor = AlbumOnBackground,
                    navigationIconContentColor = AlbumOnBackground
                )
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AlbumOnBackgroundMuted)
                }
            }

            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.errorMessage,
                        color = AlbumOnBackgroundMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { onIntent(AlbumDetailIntent.Retry) }) {
                        Text("Tentar novamente")
                    }
                }
            }

            state.album != null -> {
                AlbumContent(
                    album = state.album,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun AlbumContent(
    album: AlbumDetail,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!album.artworkUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = album.artworkUrl.toItunesArtwork600() ?: album.artworkUrl,
                        contentDescription = album.title,
                        modifier = Modifier
                            .size(HeroArtworkSize)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(HeroArtworkSize)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A1A1A))
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = album.title,
                    color = AlbumOnBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = album.artistName,
                    color = AlbumOnBackgroundMuted,
                    fontSize = 16.sp
                )
            }
        }
        items(
            items = album.tracks,
            key = { it.trackId }
        ) { track ->
            TrackRow(track = track)
        }
    }
}

@Composable
private fun TrackRow(track: AlbumTrack) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!track.artworkUrl100.isNullOrBlank()) {
            AsyncImage(
                model = track.artworkUrl100,
                contentDescription = track.trackName,
                modifier = Modifier
                    .size(RowThumbSize)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(RowThumbSize)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1A1A1A))
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.trackName,
                color = AlbumOnBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = track.artistName,
                color = AlbumOnBackgroundMuted,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AlbumDetailScreenPreview() {
    SimplePlayerTheme {
        AlbumDetailScreen(
            state = AlbumDetailState(
                isLoading = false,
                album = AlbumDetail(
                    collectionId = 1L,
                    title = "Random Access Memories",
                    artistName = "Daft Punk",
                    artworkUrl = null,
                    tracks = listOf(
                        AlbumTrack(1L, "Give Life Back to Music", "Daft Punk", null),
                        AlbumTrack(2L, "The Game of Love", "Daft Punk", null)
                    )
                )
            ),
            onIntent = {},
            onBack = {}
        )
    }
}
