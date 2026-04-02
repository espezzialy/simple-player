package com.espezzialy.simpleplayer.data.mapper

import com.espezzialy.simpleplayer.data.remote.model.ItunesSongDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ItunesSongMapperTest {
    @Test
    fun toSong_mapsFields() {
        val dto =
            ItunesSongDto(
                trackId = 9L,
                trackName = "Track",
                artistName = "Artist",
                collectionName = "Album",
                collectionId = 3L,
                artworkUrl100 = "http://img/100.jpg",
            )
        val song = ItunesSongMapper.toSong(dto)
        assertEquals(9L, song.trackId)
        assertEquals("Track", song.trackName)
        assertEquals("Artist", song.artistName)
        assertEquals("Album", song.collectionName)
        assertEquals(3L, song.collectionId)
        assertEquals("http://img/100.jpg", song.artworkUrl100)
    }

    @Test
    fun toSong_nullTrackId_becomesZero() {
        val dto = ItunesSongDto(trackName = "X")
        assertEquals(0L, ItunesSongMapper.toSong(dto).trackId)
    }

    @Test
    fun toSong_blankArtwork_becomesNull() {
        val dto = ItunesSongDto(trackId = 1L, artworkUrl100 = "  ")
        assertNull(ItunesSongMapper.toSong(dto).artworkUrl100)
    }

    @Test
    fun toSong_nullStringsBecomeEmptyExceptOptionalArtwork() {
        val dto = ItunesSongDto(trackId = 1L)
        val song = ItunesSongMapper.toSong(dto)
        assertEquals("", song.trackName)
        assertEquals("", song.artistName)
        assertEquals("", song.collectionName)
    }
}
