package com.espezzialy.simpleplayer.data.local

import com.espezzialy.simpleplayer.domain.model.Song
import org.junit.Assert.assertEquals
import org.junit.Test

class RecentSongSnapshotTest {
    @Test
    fun fromSong_toSong_preservesDomainFields() {
        val song =
            Song(
                trackId = 10L,
                trackName = "Title",
                artistName = "Band",
                collectionName = "LP",
                collectionId = 20L,
                artworkUrl100 = "https://is1-ssl.mzstatic.com/x/100x100bb.jpg",
            )
        val snapshot = RecentSongSnapshot.fromSong(song)
        assertEquals(song.trackId, snapshot.trackId)
        assertEquals(song.trackName, snapshot.trackName)
        assertEquals(song.artistName, snapshot.artistName)
        assertEquals(song.collectionName, snapshot.collectionName)
        assertEquals(song.collectionId, snapshot.collectionId)
        assertEquals(song.artworkUrl100, snapshot.artworkUrlSmall)
        assertEquals(
            "https://is1-ssl.mzstatic.com/x/600x600bb.jpg",
            snapshot.artworkUrlLarge,
        )

        val back = snapshot.toSong()
        assertEquals(song.trackId, back.trackId)
        assertEquals(song.trackName, back.trackName)
        assertEquals(song.artistName, back.artistName)
        assertEquals(song.collectionName, back.collectionName)
        assertEquals(song.collectionId, back.collectionId)
        assertEquals(song.artworkUrl100, back.artworkUrl100)
    }
}
