package com.espezzialy.simpleplayer.domain.model

data class Song(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val collectionId: Long?,
    val artworkUrl100: String?,
    val trackTimeMillis: Long? = null,
)
