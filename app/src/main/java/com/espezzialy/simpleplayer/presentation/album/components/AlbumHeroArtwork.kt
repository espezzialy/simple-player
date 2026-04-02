package com.espezzialy.simpleplayer.presentation.album.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.espezzialy.simpleplayer.core.media.toItunesArtwork600
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun AlbumHeroArtwork(
    album: AlbumDetail,
    size: Dp = SimplePlayerDimens.Album.heroArtworkFallback,
    cornerRadius: Dp = SimplePlayerDimens.Album.rowThumbnailCornerRadius,
) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(cornerRadius)
    if (!album.artworkUrl.isNullOrBlank()) {
        AsyncImage(
            model = album.artworkUrl.toItunesArtwork600() ?: album.artworkUrl,
            contentDescription = album.title,
            modifier =
                Modifier
                    .size(size)
                    .clip(shape),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier =
                Modifier
                    .size(size)
                    .clip(shape)
                    .background(colorScheme.surfaceContainerLowest),
        )
    }
}
