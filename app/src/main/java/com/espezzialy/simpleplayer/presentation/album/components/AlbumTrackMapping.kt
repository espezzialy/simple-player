package com.espezzialy.simpleplayer.presentation.album.components

import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.AlbumTrack
import com.espezzialy.simpleplayer.domain.model.Song

fun AlbumTrack.toSong(album: AlbumDetail): Song =
    Song(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        collectionName = album.title,
        collectionId = album.collectionId,
        artworkUrl100 = artworkUrl100
    )
