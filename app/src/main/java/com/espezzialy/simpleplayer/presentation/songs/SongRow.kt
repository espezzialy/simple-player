package com.espezzialy.simpleplayer.presentation.songs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.core.media.toItunesArtwork200
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.presentation.common.ArtworkThumbnail
import com.espezzialy.simpleplayer.presentation.common.SongListCellArtistColorTablet
import com.espezzialy.simpleplayer.presentation.common.SongListCellArtistStyleTablet
import com.espezzialy.simpleplayer.presentation.common.SongListCellArtworkSizePhone
import com.espezzialy.simpleplayer.presentation.common.SongListCellArtworkSizeTablet
import com.espezzialy.simpleplayer.presentation.common.SongListCellTabletMinWidthDp
import com.espezzialy.simpleplayer.presentation.common.SongListCellTitleStyleTablet

@Composable
fun SongRow(
    song: Song,
    onSongClick: () -> Unit,
    onViewAlbum: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val isTabletLayout =
        LocalConfiguration.current.screenWidthDp >= SongListCellTabletMinWidthDp
    val artworkSize =
        if (isTabletLayout) SongListCellArtworkSizeTablet else SongListCellArtworkSizePhone
    val artworkUrl = if (isTabletLayout) {
        song.artworkUrl100.toItunesArtwork200()
    } else {
        song.artworkUrl100
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onSongClick)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArtworkThumbnail(
                imageUrl = artworkUrl,
                contentDescription = song.trackName,
                size = artworkSize,
                modifier = Modifier,
                cornerRadius = 8.dp,
                placeholderColor = colorScheme.surface
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (isTabletLayout) {
                    Text(
                        text = song.trackName,
                        style = SongListCellTitleStyleTablet,
                        color = colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = song.artistName,
                        style = SongListCellArtistStyleTablet,
                        color = SongListCellArtistColorTablet,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = song.trackName,
                        style = typography.titleMedium,
                        color = colorScheme.onSurface,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = song.artistName,
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
        if (onViewAlbum != null) {
            var menuExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = stringResource(R.string.content_desc_more_options),
                        tint = colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = colorScheme.surface
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.view_album),
                                style = typography.bodyLarge,
                                color = colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_view_album),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = colorScheme.onSurface
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onViewAlbum()
                        },
                        modifier = Modifier.width(200.dp),
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        colors = MenuDefaults.itemColors(
                            textColor = colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}
