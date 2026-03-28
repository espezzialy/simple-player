package com.espezzialy.simpleplayer.presentation.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.presentation.common.ArtworkThumbnail
import com.espezzialy.simpleplayer.presentation.common.CenteredLoading
import com.espezzialy.simpleplayer.presentation.common.ErrorWithRetry
import com.espezzialy.simpleplayer.presentation.common.SongsSearchField
import com.espezzialy.simpleplayer.presentation.songs.SongsIntent
import com.espezzialy.simpleplayer.presentation.songs.SongsState

private val SideRowThumbSize = 56.dp

@Composable
fun PlayerSidePlaylistPanel(
    songsState: SongsState,
    currentTrackId: Long,
    isCurrentPlaying: Boolean,
    onSongsIntent: (SongsIntent) -> Unit,
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
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_music_list),
                    contentDescription = null,
                    tint = colorScheme.onBackground,
                    modifier = Modifier.size(22.dp)
                )
            }
            SongsSearchField(
                query = songsState.query,
                onQueryChange = { onSongsIntent(SongsIntent.QueryChanged(it)) }
            )
            Spacer(modifier = Modifier.height(12.dp))

            when {
                songsState.isLoading -> {
                    CenteredLoading(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                songsState.errorMessage != null -> {
                    ErrorWithRetry(
                        message = songsState.errorMessage,
                        retryLabel = stringResource(R.string.retry),
                        onRetry = { onSongsIntent(SongsIntent.RetrySearch) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                songsState.query.isBlank() -> {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.songs_empty_query_hint),
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                songsState.songs.isEmpty() -> {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.songs_no_results),
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(songsState.songs, key = { it.trackId }) { song ->
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
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtworkThumbnail(
            imageUrl = song.artworkUrl100,
            contentDescription = song.trackName,
            size = SideRowThumbSize,
            cornerRadius = 8.dp,
            placeholderColor = colorScheme.surfaceContainerLowest
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.trackName,
                style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colorScheme.onBackground,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = song.artistName,
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant,
                maxLines = 2
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
