package com.espezzialy.simpleplayer.presentation.album.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.espezzialy.simpleplayer.core.media.toItunesArtwork200
import com.espezzialy.simpleplayer.domain.model.AlbumTrack
import com.espezzialy.simpleplayer.presentation.common.components.ArtworkThumbnail
import com.espezzialy.simpleplayer.presentation.common.components.SongListCellArtistColorTablet
import com.espezzialy.simpleplayer.presentation.common.components.SongListCellArtistStyleTablet
import com.espezzialy.simpleplayer.presentation.common.components.SongListCellArtworkSizeTablet
import com.espezzialy.simpleplayer.presentation.common.components.SongListCellTitleStyleTablet
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerBreakpoints
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun AlbumTrackRow(
    track: AlbumTrack,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val isTabletLayout =
        LocalConfiguration.current.screenWidthDp >= SimplePlayerBreakpoints.tabletMinWidthDp
    val thumbSize =
        if (isTabletLayout) {
            SongListCellArtworkSizeTablet
        } else {
            SimplePlayerDimens.Album.rowThumb
        }
    val artworkUrl =
        if (isTabletLayout) {
            track.artworkUrl100.toItunesArtwork200()
        } else {
            track.artworkUrl100
        }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtworkThumbnail(
            imageUrl = artworkUrl,
            contentDescription = track.trackName,
            size = thumbSize,
            cornerRadius = SimplePlayerDimens.Album.rowThumbnailCornerRadius,
            placeholderColor = colorScheme.surfaceContainerLowest,
        )
        Spacer(modifier = Modifier.width(SimplePlayerDimens.Album.trackRowThumbSpacing))
        Column(modifier = Modifier.weight(1f)) {
            if (isTabletLayout) {
                Text(
                    text = track.trackName,
                    style = SongListCellTitleStyleTablet,
                    color = colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.trackRowTitleToArtistTablet))
                Text(
                    text = track.artistName,
                    style = SongListCellArtistStyleTablet,
                    color = SongListCellArtistColorTablet,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                Text(
                    text = track.trackName,
                    style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.trackRowTitleToArtistPhone))
                Text(
                    text = track.artistName,
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
