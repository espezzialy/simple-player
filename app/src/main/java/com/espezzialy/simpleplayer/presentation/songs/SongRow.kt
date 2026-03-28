package com.espezzialy.simpleplayer.presentation.songs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.presentation.common.ArtworkThumbnail
import com.espezzialy.simpleplayer.presentation.common.SimplePlayerDarkPalette

private val SongArtworkSize = 64.dp

@Composable
fun SongRow(
    song: Song,
    onSongClick: () -> Unit,
    onViewAlbum: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
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
                imageUrl = song.artworkUrl100,
                contentDescription = song.trackName,
                size = SongArtworkSize,
                modifier = Modifier,
                cornerRadius = 8.dp,
                placeholderColor = SimplePlayerDarkPalette.Surface
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.trackName,
                    color = SimplePlayerDarkPalette.OnBackground,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.artistName,
                    color = SimplePlayerDarkPalette.OnBackgroundMuted,
                    fontSize = 15.sp,
                    maxLines = 2
                )
            }
        }
        if (onViewAlbum != null) {
            var menuExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = stringResource(R.string.content_desc_more_options),
                        tint = SimplePlayerDarkPalette.OnBackgroundMuted
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = SimplePlayerDarkPalette.Surface
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(R.string.view_album),
                                color = SimplePlayerDarkPalette.OnBackground
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onViewAlbum()
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = SimplePlayerDarkPalette.OnBackground
                        )
                    )
                }
            }
        }
    }
}
