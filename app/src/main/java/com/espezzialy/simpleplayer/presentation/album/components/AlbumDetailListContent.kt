package com.espezzialy.simpleplayer.presentation.album.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.AlbumTrack
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerBreakpoints
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun AlbumDetailListContent(
    album: AlbumDetail,
    onSongClick: (AlbumTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTabletLayout =
        LocalConfiguration.current.screenWidthDp >= SimplePlayerBreakpoints.tabletMinWidthDp

    if (isTabletLayout) {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = SimplePlayerDimens.Album.tabletContentPaddingStart,
                end = SimplePlayerDimens.Album.listHorizontalEnd,
                bottom = SimplePlayerDimens.Album.listBottomPadding
            )
        ) {
            item {
                AlbumTabletHeroSection(album = album)
                Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.tabletTracksTopSpacer))
            }
            itemsIndexed(
                items = album.tracks,
                key = { _, track -> track.trackId }
            ) { index, track ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    AlbumTrackRow(
                        track = track,
                        onClick = { onSongClick(track) }
                    )
                    if (index < album.tracks.lastIndex) {
                        Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.tabletTrackSpacing))
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = SimplePlayerDimens.screenHorizontalPadding,
                end = SimplePlayerDimens.screenHorizontalPadding,
                top = SimplePlayerDimens.Album.phoneHeroPaddingTop,
                bottom = SimplePlayerDimens.Album.listBottomPadding
            )
        ) {
            item {
                AlbumPhoneHeroSection(album = album)
            }
            itemsIndexed(
                items = album.tracks,
                key = { _, track -> track.trackId }
            ) { index, track ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    AlbumTrackRow(
                        track = track,
                        onClick = { onSongClick(track) }
                    )
                    if (index < album.tracks.lastIndex) {
                        Spacer(modifier = Modifier.height(SimplePlayerDimens.Album.phoneTrackRowGap))
                    }
                }
            }
        }
    }
}
