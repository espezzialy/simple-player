package com.espezzialy.simpleplayer.data.local

import com.espezzialy.simpleplayer.core.media.toItunesArtwork600
import com.espezzialy.simpleplayer.domain.model.Song

data class RecentSongSnapshot(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val collectionId: Long?,
    val artworkUrlSmall: String?,
    val artworkUrlLarge: String?,
    val trackTimeMillis: Long? = null,
) {
    fun toSong(): Song =
        Song(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            collectionName = collectionName,
            collectionId = collectionId,
            artworkUrl100 = artworkUrlSmall,
            trackTimeMillis = trackTimeMillis,
        )

    companion object {
        fun fromSong(song: Song): RecentSongSnapshot =
            RecentSongSnapshot(
                trackId = song.trackId,
                trackName = song.trackName,
                artistName = song.artistName,
                collectionName = song.collectionName,
                collectionId = song.collectionId,
                artworkUrlSmall = song.artworkUrl100,
                artworkUrlLarge = song.artworkUrl100.toItunesArtwork600(),
                trackTimeMillis = song.trackTimeMillis,
            )
    }
}
