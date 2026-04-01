package com.espezzialy.simpleplayer.presentation.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.Dp
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
import com.espezzialy.simpleplayer.presentation.common.SongListCellTitleStyleTablet
import com.espezzialy.simpleplayer.presentation.common.TabletBackIconButton
import com.espezzialy.simpleplayer.presentation.common.TabletNavBarPaddingTop
import com.espezzialy.simpleplayer.ui.theme.AlbumPhoneHeroTextStyles
import com.espezzialy.simpleplayer.ui.theme.AlbumTabletHeroTextStyles
import com.espezzialy.simpleplayer.ui.theme.AlbumTabletNavTitleTextStyle
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerBreakpoints
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme

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
        LocalConfiguration.current.screenWidthDp >= SimplePlayerBreakpoints.tabletMinWidthDp

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        topBar = {
            if (isTabletLayout) {
                AlbumTabletTopBar(onBack = onBack)
            } else {
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
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.content_desc_back),
                                tint = colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.background,
                        titleContentColor = colorScheme.onBackground,
                        navigationIconContentColor = colorScheme.onBackground
                    )
                )
            }
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
                        .padding(horizontal = SimplePlayerDimens.screenHorizontalPadding)
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
private fun AlbumTabletTopBar(onBack: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                start = SimplePlayerDimens.Album.tabletNavPaddingStart,
                top = TabletNavBarPaddingTop
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabletBackIconButton(
            onClick = onBack,
            contentDescription = stringResource(R.string.content_desc_back),
            tint = colorScheme.onBackground,
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            iconSize = 28.dp
        )
        Spacer(modifier = Modifier.width(SimplePlayerDimens.Album.tabletNavIconToTitle))
        Text(
            text = stringResource(R.string.album_fallback_title),
            style = AlbumTabletNavTitleTextStyle,
            color = colorScheme.onBackground
        )
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
        LocalConfiguration.current.screenWidthDp >= SimplePlayerBreakpoints.tabletMinWidthDp

    if (isTabletLayout) {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = SimplePlayerDimens.Album.tabletContentPaddingStart,
                end = SimplePlayerDimens.Album.listHorizontalEnd,
                bottom = SimplePlayerDimens.Album.listBottomPadding
            )
        ) {
            item {
                AlbumTabletHeroSection(album = album)
                Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.tabletTracksTopSpacer))
            }
            itemsIndexed(
                items = album.tracks,
                key = { _, track -> track.trackId }
            ) { index, track ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    TrackRow(
                        track = track,
                        onClick = { onSongClick(track) }
                    )
                    if (index < album.tracks.lastIndex) {
                        Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.tabletTrackSpacing))
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = SimplePlayerDimens.screenHorizontalPadding,
                end = SimplePlayerDimens.screenHorizontalPadding,
                top = SimplePlayerDimens.Album.phoneHeroPaddingTop,
                bottom = SimplePlayerDimens.Album.listBottomPadding
            )
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AlbumHeroArtwork(
                        album = album,
                        size = SimplePlayerDimens.Album.phoneHeroArtwork,
                        cornerRadius = SimplePlayerDimens.Album.phoneHeroCornerRadius
                    )
                    Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.phoneTitleAfterImage))
                    Text(
                        text = album.title,
                        modifier = Modifier.fillMaxWidth(),
                        style = AlbumPhoneHeroTextStyles.title,
                        color = colorScheme.onBackground,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.phoneArtistAfterTitle))
                    Text(
                        text = album.artistName,
                        modifier = Modifier.fillMaxWidth(),
                        style = AlbumPhoneHeroTextStyles.artist,
                        color = colorScheme.onBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.phoneTracksAfterArtist))
                }
            }
            itemsIndexed(
                items = album.tracks,
                key = { _, track -> track.trackId }
            ) { index, track ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    TrackRow(
                        track = track,
                        onClick = { onSongClick(track) }
                    )
                    if (index < album.tracks.lastIndex) {
                        Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.phoneTrackRowGap))
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumTabletHeroSection(album: AlbumDetail) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = SimplePlayerDimens.Album.tabletHeroPaddingTop),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumHeroArtwork(
            album = album,
            size = SimplePlayerDimens.Album.tabletHeroArtwork,
            cornerRadius = SimplePlayerDimens.Album.tabletHeroCornerRadius
        )
        Spacer(modifier = Modifier.width(SimplePlayerDimens.Album.tabletHeroImageToText))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = album.title,
                style = AlbumTabletHeroTextStyles.title,
                color = colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.phoneArtistAfterTitle))
            Text(
                text = album.artistName,
                style = AlbumTabletHeroTextStyles.artist,
                color = colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun AlbumHeroArtwork(
    album: AlbumDetail,
    size: Dp = SimplePlayerDimens.Album.heroArtworkFallback,
    cornerRadius: Dp = SimplePlayerDimens.Album.rowThumbnailCornerRadius
) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(cornerRadius)
    if (!album.artworkUrl.isNullOrBlank()) {
        AsyncImage(
            model = album.artworkUrl.toItunesArtwork600() ?: album.artworkUrl,
            contentDescription = album.title,
            modifier = Modifier
                .size(size)
                .clip(shape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
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
        LocalConfiguration.current.screenWidthDp >= SimplePlayerBreakpoints.tabletMinWidthDp
    val thumbSize = if (isTabletLayout) {
        SongListCellArtworkSizeTablet
    } else {
        SimplePlayerDimens.Album.rowThumb
    }
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
            cornerRadius = SimplePlayerDimens.Album.rowThumbnailCornerRadius,
            placeholderColor = colorScheme.surfaceContainerLowest
        )
        Spacer(modifier = Modifier.width(SimplePlayerDimens.Album.trackRowThumbSpacing))
        Column(modifier = Modifier.weight(1f)) {
            if (isTabletLayout) {
                Text(
                    text = track.trackName,
                    style = SongListCellTitleStyleTablet,
                    color = colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.trackRowTitleToArtistTablet))
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
                Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.trackRowTitleToArtistPhone))
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
