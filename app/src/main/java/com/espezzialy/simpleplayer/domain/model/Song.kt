package com.espezzialy.simpleplayer.domain.model

/**
 * Domain model for display and business rules.
 * [ItunesSongDto] stays in the data layer with all API fields.
 */
data class Song(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    /** iTunes collection ID (album lookup). */
    val collectionId: Long?,
    /** Cover URL 100×100 (`artworkUrl100` from the API). */
    val artworkUrl100: String?
)
