package com.espezzialy.simpleplayer.data.local

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecentSongsJsonCodecTest {
    private lateinit var codec: RecentSongsJsonCodec

    @Before
    fun setup() {
        codec = RecentSongsJsonCodec(Gson())
    }

    @Test
    fun decode_nullOrBlank_returnsEmpty() {
        assertTrue(codec.decode(null).isEmpty())
        assertTrue(codec.decode("").isEmpty())
        assertTrue(codec.decode("   ").isEmpty())
    }

    @Test
    fun decode_invalidJson_returnsEmpty() {
        assertTrue(codec.decode("{not json").isEmpty())
    }

    @Test
    fun encodeDecode_roundTrip() {
        val snapshots =
            listOf(
                RecentSongSnapshot(
                    trackId = 1L,
                    trackName = "Song",
                    artistName = "Artist",
                    collectionName = "Album",
                    collectionId = 2L,
                    artworkUrlSmall = "http://s.jpg",
                    artworkUrlLarge = "http://l.jpg",
                    trackTimeMillis = 180_000L,
                ),
            )
        val json = codec.encode(snapshots)
        val decoded = codec.decode(json)
        assertEquals(1, decoded.size)
        assertEquals(snapshots[0], decoded[0])
    }
}
