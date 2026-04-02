package com.espezzialy.simpleplayer.presentation.player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerColors
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun PlayerTransportControls(
    isPlaying: Boolean,
    repeatEnabled: Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onRepeat: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val pauseDesc = stringResource(R.string.content_desc_pause)
    val playDesc = stringResource(R.string.content_desc_play)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = SimplePlayerDimens.Player.transportBarVerticalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SimplePlayerDimens.Player.transportMainClusterSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = onPlayPause,
                modifier = Modifier.size(SimplePlayerDimens.Player.playSurfaceSize),
                shape = CircleShape,
                color = colorScheme.surfaceContainerHigh,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) pauseDesc else playDesc,
                        tint = colorScheme.onSurface,
                        modifier = Modifier.size(SimplePlayerDimens.Player.playIconSize),
                    )
                }
            }
            IconButton(onClick = onPrevious) {
                Icon(
                    painter = painterResource(R.drawable.ic_backward_bar_fill),
                    contentDescription = stringResource(R.string.content_desc_previous_track),
                    tint = colorScheme.onSurface,
                    modifier = Modifier.size(SimplePlayerDimens.Player.skipIconSize),
                )
            }
            IconButton(onClick = onNext) {
                Icon(
                    painter = painterResource(R.drawable.ic_forward_bar_fill),
                    contentDescription = stringResource(R.string.content_desc_next_track),
                    tint = colorScheme.onSurface,
                    modifier = Modifier.size(SimplePlayerDimens.Player.skipIconSize),
                )
            }
        }
        IconButton(onClick = onRepeat) {
            Icon(
                painter = painterResource(R.drawable.ic_play_on_repeat),
                contentDescription = stringResource(R.string.content_desc_repeat),
                tint =
                    if (repeatEnabled) {
                        SimplePlayerColors.PlayerRepeatActive
                    } else {
                        Color.White
                    },
                modifier = Modifier.size(SimplePlayerDimens.Player.repeatIconSize),
            )
        }
    }
}
