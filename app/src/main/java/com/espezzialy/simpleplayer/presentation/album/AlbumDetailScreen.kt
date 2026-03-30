package com.espezzialy.simpleplayer.presentation.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.core.media.toItunesArtwork200
import com.espezzialy.simpleplayer.core.media.toItunesArtwork600
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.AlbumTrack
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.presentation.common.ArtworkThumbnail
import com.espezzialy.simpleplayer.presentation.common.CenteredLoading
import com.espezzialy.simpleplayer.presentation.common.ErrorWithRetry
import com.espezzialy.simpleplayer.presentation.common.SongListCellArtistColorTablet
import com.espezzialy.simpleplayer.presentation.common.SongListCellArtistStyleTablet
import com.espezzialy.simpleplayer.presentation.common.SongListCellArtworkSizeTablet
import com.espezzialy.simpleplayer.presentation.common.SongListCellTabletMinWidthDp
import com.espezzialy.simpleplayer.presentation.common.SongListCellTitleStyleTablet
import com.espezzialy.simpleplayer.presentation.common.TabletBackIconButton
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme

private val HeroArtworkSize = 240.dp
private val RowThumbSize = 56.dp

/** Material width breakpoint: at 600dp and above, album hero uses tablet (side-by-side) layout. */
private const val TabletMinWidthDp = 600

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
        onNavigateToPlayer = onNavigateToPlayer,
        onBeforeNavigateToPlayerFromAlbum = viewModel::preparePlayerFromAlbum
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    state: AlbumDetailState,
    onIntent: (AlbumDetailIntent) -> Unit,
    onBack: () -> Unit,
    onNavigateToPlayer: (Song) -> Unit,
    onBeforeNavigateToPlayerFromAlbum: (AlbumDetail) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val fallbackTitle = stringResource(R.string.album_fallback_title)
    val title = state.album?.title.orEmpty().ifBlank { fallbackTitle }
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val isTabletLayout =
        LocalConfiguration.current.screenWidthDp >= TabletMinWidthDp

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = typography.titleLarge,
                        color = colorScheme.onBackground,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    if (isTabletLayout) {
                        TabletBackIconButton(
                            onClick = onBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = colorScheme.onBackground,
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            iconSize = 28.dp
                        )
                    } else {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.content_desc_back),
                                tint = colorScheme.onBackground
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onBackground,
                    navigationIconContentColor = colorScheme.onBackground
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
                        onBeforeNavigateToPlayerFromAlbum(album)
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
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val isTabletLayout =
        LocalConfiguration.current.screenWidthDp >= TabletMinWidthDp

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (isTabletLayout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    AlbumHeroArtwork(album = album)
                    Spacer(modifier = Modifier.width(24.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = album.title,
                            style = typography.headlineSmall,
                            color = colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = album.artistName,
                            style = typography.bodyLarge,
                            color = colorScheme.onBackground
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AlbumHeroArtwork(album = album)
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = album.title,
                        style = typography.headlineSmall,
                        color = colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = album.artistName,
                        style = typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant
                    )
                }
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
    val colorScheme = MaterialTheme.colorScheme
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
                .background(colorScheme.surfaceContainerLowest)
        )
    }
}

@Composable
private fun TrackRow(
    track: AlbumTrack,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val isTabletLayout =
        LocalConfiguration.current.screenWidthDp >= SongListCellTabletMinWidthDp
    val thumbSize = if (isTabletLayout) SongListCellArtworkSizeTablet else RowThumbSize
    val artworkUrl = if (isTabletLayout) {
        track.artworkUrl100.toItunesArtwork200()
    } else {
        track.artworkUrl100
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtworkThumbnail(
            imageUrl = artworkUrl,
            contentDescription = track.trackName,
            size = thumbSize,
            cornerRadius = 8.dp,
            placeholderColor = colorScheme.surfaceContainerLowest
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            if (isTabletLayout) {
                Text(
                    text = track.trackName,
                    style = SongListCellTitleStyleTablet,
                    color = colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = track.artistName,
                    style = SongListCellArtistStyleTablet,
                    color = SongListCellArtistColorTablet,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    text = track.trackName,
                    style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = track.artistName,
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
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

@Preview(showBackground = true, widthDp = 900, heightDp = 600, name = "Album (tablet)")
@Composable
private fun AlbumDetailScreenTabletPreview() {
    SimplePlayerTheme {
        AlbumDetailScreen(
            state = AlbumDetailState(
                isLoading = false,
                album = AlbumDetail(
                    collectionId = 1L,
                    title = "Divide",
                    artistName = "Ed Sheeran",
                    artworkUrl = null,
                    tracks = listOf(
                        AlbumTrack(1L, "Perfect", "Ed Sheeran", null),
                        AlbumTrack(2L, "Shape of You", "Ed Sheeran", null)
                    )
                )
            ),
            onIntent = {},
            onBack = {},
            onNavigateToPlayer = {}
        )
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
            onBack = {},
            onNavigateToPlayer = {}
        )
    }
}
