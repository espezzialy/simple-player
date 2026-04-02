package com.espezzialy.simpleplayer.presentation.album.components

import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.AlbumTrack
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumTrackMappingTest {
    @Test
    fun toSong_mapsAlbumMetadataOntoTrack() {
        val album =
            AlbumDetail(
                collectionId = 99L,
                title = "Album Title",
                artistName = "Band",
                artworkUrl = "http://cover",
                tracks = emptyList(),
            )
        val track =
            AlbumTrack(
                trackId = 5L,
                trackName = "Song",
                artistName = "Feat",
                artworkUrl100 = "http://thumb",
                trackTimeMillis = 222_000L,
            )
        val song = track.toSong(album)
        assertEquals(5L, song.trackId)
        assertEquals("Song", song.trackName)
        assertEquals("Feat", song.artistName)
        assertEquals("Album Title", song.collectionName)
        assertEquals(99L, song.collectionId)
        assertEquals("http://thumb", song.artworkUrl100)
        assertEquals(222_000L, song.trackTimeMillis)
    }
}
