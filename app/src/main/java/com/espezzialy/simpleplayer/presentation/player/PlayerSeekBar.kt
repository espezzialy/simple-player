package com.espezzialy.simpleplayer.presentation.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private val SeekInactiveTrack = Color(0xFF3A3A3C)
private val SeekActiveAndThumb = Color(0xFFFFFFFF)

/** Thin track + white circular thumb (Library – Player / Figma). */
@Composable
fun PlayerSeekBar(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbRadius = 8.dp
    val trackHeight = 2.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        if (widthPx <= 0f) return@BoxWithConstraints

        val density = LocalDensity.current
        val thumbPx = with(density) { thumbRadius.toPx() }
        val trackPx = with(density) { trackHeight.toPx() }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(widthPx) {
                    detectTapGestures { offset ->
                        onProgressChange((offset.x / widthPx).coerceIn(0f, 1f))
                    }
                }
                .pointerInput(widthPx) {
                    detectDragGestures { change, _ ->
                        onProgressChange((change.position.x / widthPx).coerceIn(0f, 1f))
                    }
                }
        ) {
            val centerY = size.height / 2f
            val trackY = centerY - trackPx / 2f

            drawRoundRect(
                color = SeekInactiveTrack,
                topLeft = Offset(0f, trackY),
                size = Size(size.width, trackPx),
                cornerRadius = CornerRadius(trackPx / 2f, trackPx / 2f)
            )

            val activeW = size.width * progress.coerceIn(0f, 1f)
            drawRoundRect(
                color = SeekActiveAndThumb,
                topLeft = Offset(0f, trackY),
                size = Size(activeW, trackPx),
                cornerRadius = CornerRadius(trackPx / 2f, trackPx / 2f)
            )

            val thumbCenterX = (size.width * progress.coerceIn(0f, 1f))
                .coerceIn(thumbPx, size.width - thumbPx)
            drawCircle(
                color = SeekActiveAndThumb,
                radius = thumbPx,
                center = Offset(thumbCenterX, centerY)
            )
        }
    }
}
