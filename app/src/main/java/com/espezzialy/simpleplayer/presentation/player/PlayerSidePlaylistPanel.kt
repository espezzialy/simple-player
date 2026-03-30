package com.espezzialy.simpleplayer.presentation.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.presentation.common.ArtworkThumbnail
import com.espezzialy.simpleplayer.presentation.common.CenteredLoading
import com.espezzialy.simpleplayer.presentation.common.ErrorWithRetry

private val SideRowThumbSize = 52.dp
private val SidePanelListPaddingTop = 20.dp
private val SideRowVerticalPadding = 8.dp
private val SideRowTextStartSpacing = 16.dp
private val SideRowTextSpacing = 4.dp

@Composable
fun PlayerSidePlaylistPanel(
    sidePanel: PlayerSidePanelUiState,
    currentTrackId: Long,
    isCurrentPlaying: Boolean,
    onRetrySearch: () -> Unit,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(28.dp),
        color = colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = if (sidePanel.panelTitle != null) 8.dp else 0.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_music_list),
                    contentDescription = null,
                    tint = colorScheme.onBackground,
                    modifier = Modifier.size(22.dp)
                )
            }
            sidePanel.panelTitle?.let { title ->
                Text(
                    text = title,
                    style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 0.dp)
                )
            }

            when {
                sidePanel.isSearchMode && sidePanel.isLoading -> {
                    CenteredLoading(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                sidePanel.isSearchMode && sidePanel.errorMessage != null -> {
                    ErrorWithRetry(
                        message = sidePanel.errorMessage,
                        retryLabel = stringResource(R.string.retry),
                        onRetry = onRetrySearch,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                sidePanel.isSearchMode && sidePanel.showEmptyQueryHint -> {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.songs_empty_query_hint),
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                sidePanel.isSearchMode && sidePanel.songs.isEmpty() -> {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.songs_no_results),
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                sidePanel.songs.isEmpty() -> {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.player_side_panel_empty_album),
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = SidePanelListPaddingTop)
                    ) {
                        items(sidePanel.songs, key = { it.trackId }) { song ->
                            PlayerSidePlaylistRow(
                                song = song,
                                showPlayingIndicator = song.trackId == currentTrackId && isCurrentPlaying,
                                onClick = { onSongClick(song) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerSidePlaylistRow(
    song: Song,
    showPlayingIndicator: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = SideRowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtworkThumbnail(
            imageUrl = song.artworkUrl100,
            contentDescription = song.trackName,
            size = SideRowThumbSize,
            cornerRadius = 8.dp,
            placeholderColor = colorScheme.surfaceContainerLowest
        )
        Spacer(modifier = Modifier.width(SideRowTextStartSpacing))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.trackName,
                style = typography.titleMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 19.2.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(SideRowTextSpacing))
            Text(
                text = song.artistName,
                style = typography.bodySmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.8.sp
                ),
                color = colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (showPlayingIndicator) {
            Icon(
                imageVector = Icons.Filled.GraphicEq,
                contentDescription = stringResource(R.string.content_desc_playing_indicator),
                tint = colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
