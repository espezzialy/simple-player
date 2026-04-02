package com.espezzialy.simpleplayer.presentation.album.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.ui.theme.AlbumTabletHeroTextStyles
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun AlbumTabletHeroSection(album: AlbumDetail) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = SimplePlayerDimens.Album.tabletHeroPaddingTop),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AlbumHeroArtwork(
            album = album,
            size = SimplePlayerDimens.Album.tabletHeroArtwork,
            cornerRadius = SimplePlayerDimens.Album.tabletHeroCornerRadius,
        )
        Spacer(modifier = Modifier.width(SimplePlayerDimens.Album.tabletHeroImageToText))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = album.title,
                style = AlbumTabletHeroTextStyles.title,
                color = colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.phoneArtistAfterTitle))
            Text(
                text = album.artistName,
                style = AlbumTabletHeroTextStyles.artist,
                color = colorScheme.onBackground,
            )
        }
    }
}
