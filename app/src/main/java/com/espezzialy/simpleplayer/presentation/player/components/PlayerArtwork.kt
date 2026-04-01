package com.espezzialy.simpleplayer.presentation.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.espezzialy.simpleplayer.core.media.toItunesArtwork600
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun PlayerArtwork(artworkUrl: String?, trackName: String, size: Dp) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(SimplePlayerDimens.Player.artworkCornerRadius)
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        if (!artworkUrl.isNullOrBlank()) {
            AsyncImage(
                model = artworkUrl.toItunesArtwork600() ?: artworkUrl,
                contentDescription = trackName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
