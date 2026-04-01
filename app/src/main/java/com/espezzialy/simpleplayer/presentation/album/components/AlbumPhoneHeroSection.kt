package com.espezzialy.simpleplayer.presentation.album.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.ui.theme.AlbumPhoneHeroTextStyles
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun AlbumPhoneHeroSection(album: AlbumDetail) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlbumHeroArtwork(
            album = album,
            size = SimplePlayerDimens.Album.phoneHeroArtwork,
            cornerRadius = SimplePlayerDimens.Album.phoneHeroCornerRadius
        )
        Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.phoneTitleAfterImage))
        Text(
            text = album.title,
            modifier = Modifier.fillMaxWidth(),
            style = AlbumPhoneHeroTextStyles.title,
            color = colorScheme.onBackground,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.phoneArtistAfterTitle))
        Text(
            text = album.artistName,
            modifier = Modifier.fillMaxWidth(),
            style = AlbumPhoneHeroTextStyles.artist,
            color = colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.phoneTracksAfterArtist))
    }
}
