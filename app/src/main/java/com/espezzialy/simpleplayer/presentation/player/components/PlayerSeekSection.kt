package com.espezzialy.simpleplayer.presentation.player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun PlayerSeekSection(
    progress: Float,
    currentLabel: String,
    remainingLabel: String,
    onProgressChange: (Float) -> Unit,
    trackHeight: Dp,
    thumbDiameter: Dp
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(modifier = Modifier.fillMaxWidth()) {
        PlayerSeekBar(
            progress = progress,
            onProgressChange = onProgressChange,
            modifier = Modifier.fillMaxWidth(),
            trackHeight = trackHeight,
            thumbDiameter = thumbDiameter
        )
        Spacer(modifier = Modifier.height(SimplePlayerDimens.Player.seekTimeRowSpacing))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentLabel,
                style = typography.labelSmall,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = remainingLabel,
                style = typography.labelSmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}
