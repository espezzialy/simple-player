package com.espezzialy.simpleplayer.presentation.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import coil.compose.AsyncImage
import com.espezzialy.simpleplayer.core.media.toItunesArtwork600
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun PlayerArtwork(
    artworkUrl: String?,
    trackName: String,
    size: Dp,
    onSwipeToNext: () -> Unit,
    onSwipeToPrevious: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(SimplePlayerDimens.Player.artworkCornerRadius)
    val layoutDirection = LocalLayoutDirection.current
    val thresholdPx =
        with(LocalDensity.current) { SimplePlayerDimens.Player.artworkSwipeHorizontalThreshold.toPx() }
    val swipeModifier =
        Modifier.pointerInput(layoutDirection, thresholdPx) {
            var totalDragX = 0f
            detectDragGestures(
                onDragStart = { totalDragX = 0f },
                onDrag = { change, dragAmount ->
                    totalDragX += dragAmount.x
                    change.consume()
                },
                onDragEnd = {
                    val towardNext =
                        when (layoutDirection) {
                            LayoutDirection.Rtl -> totalDragX >= thresholdPx
                            else -> totalDragX <= -thresholdPx
                        }
                    val towardPrevious =
                        when (layoutDirection) {
                            LayoutDirection.Rtl -> totalDragX <= -thresholdPx
                            else -> totalDragX >= thresholdPx
                        }
                    when {
                        towardNext -> onSwipeToNext()
                        towardPrevious -> onSwipeToPrevious()
                    }
                },
            )
        }
    Box(
        modifier =
            Modifier
                .size(size)
                .clip(shape)
                .background(colorScheme.surfaceContainerHigh)
                .then(swipeModifier),
        contentAlignment = Alignment.Center,
    ) {
        if (!artworkUrl.isNullOrBlank()) {
            AsyncImage(
                model = artworkUrl.toItunesArtwork600() ?: artworkUrl,
                contentDescription = trackName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}
