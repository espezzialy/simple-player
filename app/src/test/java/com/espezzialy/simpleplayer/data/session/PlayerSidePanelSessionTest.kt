package com.espezzialy.simpleplayer.data.session

import com.espezzialy.simpleplayer.domain.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerSidePanelSessionTest {
    private val sampleSong =
        Song(
            trackId = 1L,
            trackName = "A",
            artistName = "B",
            collectionName = "C",
            collectionId = 2L,
            artworkUrl100 = null,
        )

    @Test
    fun initialSource_isSearchResults() {
        val session = PlayerSidePanelSession()
        assertEquals(PlayerSidePanelSource.SearchResults, session.source.value)
    }

    @Test
    fun setAlbumTracks_updatesSource() {
        val session = PlayerSidePanelSession()
        session.setAlbumTracks("My Album", listOf(sampleSong))
        val source = session.source.value
        assertTrue(source is PlayerSidePanelSource.AlbumTracks)
        val album = source as PlayerSidePanelSource.AlbumTracks
        assertEquals("My Album", album.albumTitle)
        assertEquals(1, album.songs.size)
        assertEquals(sampleSong, album.songs[0])
    }

    @Test
    fun setRecentSongs_updatesSource() {
        val session = PlayerSidePanelSession()
        session.setRecentSongs(listOf(sampleSong))
        val source = session.source.value
        assertTrue(source is PlayerSidePanelSource.RecentSongs)
        assertEquals(listOf(sampleSong), (source as PlayerSidePanelSource.RecentSongs).songs)
    }

    @Test
    fun setSearchResults_resetsToSearch() {
        val session = PlayerSidePanelSession()
        session.setRecentSongs(emptyList())
        session.setSearchResults()
        assertEquals(PlayerSidePanelSource.SearchResults, session.source.value)
    }
}
