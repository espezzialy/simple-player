package com.espezzialy.simpleplayer.presentation.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.core.media.toItunesArtwork600
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.AlbumTrack
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.presentation.common.ArtworkThumbnail
import com.espezzialy.simpleplayer.presentation.common.CenteredLoading
import com.espezzialy.simpleplayer.presentation.common.ErrorWithRetry
import com.espezzialy.simpleplayer.presentation.common.SimplePlayerDarkPalette
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme

private val HeroArtworkSize = 240.dp
private val RowThumbSize = 56.dp

@Composable
fun AlbumDetailRoute(
    onBack: () -> Unit,
    onNavigateToPlayer: (Song) -> Unit,
    viewModel: AlbumDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AlbumDetailScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        onNavigateToPlayer = onNavigateToPlayer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    state: AlbumDetailState,
    onIntent: (AlbumDetailIntent) -> Unit,
    onBack: () -> Unit,
    onNavigateToPlayer: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val fallbackTitle = stringResource(R.string.album_fallback_title)
    val title = state.album?.title.orEmpty().ifBlank { fallbackTitle }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SimplePlayerDarkPalette.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        color = SimplePlayerDarkPalette.OnBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = SimplePlayerDarkPalette.OnBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SimplePlayerDarkPalette.Background,
                    titleContentColor = SimplePlayerDarkPalette.OnBackground,
                    navigationIconContentColor = SimplePlayerDarkPalette.OnBackground
                )
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                CenteredLoading(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            state.errorMessage != null -> {
                ErrorWithRetry(
                    message = state.errorMessage,
                    retryLabel = stringResource(R.string.retry),
                    onRetry = { onIntent(AlbumDetailIntent.Retry) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp)
                )
            }

            state.album != null -> {
                val album = state.album
                AlbumContent(
                    album = album,
                    onSongClick = { track ->
                        onNavigateToPlayer(track.toSong(album))
                    },
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
    onSongClick: (AlbumTrack) -> Unit,
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
                AlbumHeroArtwork(album = album)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = album.title,
                    color = SimplePlayerDarkPalette.OnBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = album.artistName,
                    color = SimplePlayerDarkPalette.OnBackgroundMuted,
                    fontSize = 16.sp
                )
            }
        }
        items(
            items = album.tracks,
            key = { it.trackId }
        ) { track ->
            TrackRow(
                track = track,
                onClick = { onSongClick(track) }
            )
        }
    }
}

@Composable
private fun AlbumHeroArtwork(album: AlbumDetail) {
    val shape = RoundedCornerShape(8.dp)
    if (!album.artworkUrl.isNullOrBlank()) {
        AsyncImage(
            model = album.artworkUrl.toItunesArtwork600() ?: album.artworkUrl,
            contentDescription = album.title,
            modifier = Modifier
                .size(HeroArtworkSize)
                .clip(shape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(HeroArtworkSize)
                .clip(shape)
                .background(SimplePlayerDarkPalette.ArtworkPlaceholder)
        )
    }
}

@Composable
private fun TrackRow(
    track: AlbumTrack,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtworkThumbnail(
            imageUrl = track.artworkUrl100,
            contentDescription = track.trackName,
            size = RowThumbSize,
            cornerRadius = 8.dp,
            placeholderColor = SimplePlayerDarkPalette.ArtworkPlaceholder
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.trackName,
                color = SimplePlayerDarkPalette.OnBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = track.artistName,
                color = SimplePlayerDarkPalette.OnBackgroundMuted,
                fontSize = 14.sp
            )
        }
    }
}

private fun AlbumTrack.toSong(album: AlbumDetail): Song =
    Song(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        collectionName = album.title,
        collectionId = album.collectionId,
        artworkUrl100 = artworkUrl100
    )

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
            onBack = {},
            onNavigateToPlayer = {}
        )
    }
}
