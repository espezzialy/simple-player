@file:OptIn(androidx.compose.material.ExperimentalMaterialApi::class)

package com.espezzialy.simpleplayer.presentation.songs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private val IndicatorSize = 48.dp
private val IconSize = 26.dp
private val ProgressSize = 28.dp

/**
 * Indicador de pull-to-refresh alinhado ao Material 3 (superfície circular + ícone / progresso),
 * com [pullRefreshIndicatorTransform] para seguir o gesto.
 */
@Composable
fun SongsPullRefreshIndicator(
    pullRefreshState: PullRefreshState,
    isRefreshing: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .pullRefreshIndicatorTransform(pullRefreshState)
            .semantics { this.contentDescription = contentDescription }
    ) {
        Surface(
            shape = CircleShape,
            color = colorScheme.surfaceContainerHigh,
            tonalElevation = 3.dp,
            shadowElevation = 3.dp,
            modifier = Modifier.size(IndicatorSize)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(ProgressSize),
                        color = colorScheme.primary,
                        strokeWidth = 2.5.dp,
                        strokeCap = StrokeCap.Round
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier
                            .size(IconSize)
                            .rotate(pullRefreshState.progress * 180f),
                        tint = colorScheme.primary
                    )
                }
            }
        }
    }
}
