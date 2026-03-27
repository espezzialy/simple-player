package com.espezzialy.simpleplayer.domain.model

data class AlbumDetail(
    val collectionId: Long,
    val title: String,
    val artistName: String,
    val artworkUrl: String?,
    val tracks: List<AlbumTrack>
)

data class AlbumTrack(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val artworkUrl100: String?
)
