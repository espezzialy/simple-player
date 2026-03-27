package com.espezzialy.simpleplayer.data.mapper

import com.espezzialy.simpleplayer.data.remote.model.ItunesSongDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ItunesAlbumMapperTest {

    @Test
    fun mapLookupResultsToAlbumDetail_returnsNull_whenEmpty() {
        assertNull(ItunesAlbumMapper.mapLookupResultsToAlbumDetail(emptyList()))
    }

    @Test
    fun mapLookupResultsToAlbumDetail_returnsNull_whenNoCollectionId() {
        val dto = ItunesSongDto(kind = "song", trackId = 1L)
        assertNull(ItunesAlbumMapper.mapLookupResultsToAlbumDetail(listOf(dto)))
    }

    @Test
    fun mapLookupResultsToAlbumDetail_usesCollectionRow_andSortsTracksByDiscAndTrackNumber() {
        val collection = ItunesSongDto(
            wrapperType = "collection",
            collectionId = 99L,
            collectionName = "Album X",
            artistName = "Artist",
            artworkUrl100 = "http://cover.jpg"
        )
        val b = ItunesSongDto(
            kind = "song",
            trackId = 2L,
            trackName = "Second",
            artistName = "Artist",
            collectionId = 99L,
            collectionName = "Album X",
            discNumber = 1,
            trackNumber = 2
        )
        val a = ItunesSongDto(
            kind = "song",
            trackId = 1L,
            trackName = "First",
            artistName = "Artist",
            collectionId = 99L,
            collectionName = "Album X",
            discNumber = 1,
            trackNumber = 1
        )

        val album = ItunesAlbumMapper.mapLookupResultsToAlbumDetail(listOf(collection, b, a))

        assertNotNull(album)
        assertEquals(99L, album!!.collectionId)
        assertEquals("Album X", album.title)
        assertEquals("Artist", album.artistName)
        assertEquals("http://cover.jpg", album.artworkUrl)
        assertEquals(2, album.tracks.size)
        assertEquals("First", album.tracks[0].trackName)
        assertEquals("Second", album.tracks[1].trackName)
    }

    @Test
    fun mapLookupResultsToAlbumDetail_fallsBackToTracks_whenNoCollectionRow() {
        val t = ItunesSongDto(
            kind = "song",
            trackId = 5L,
            trackName = "Only",
            artistName = "Solo",
            collectionId = 42L,
            collectionName = "From Track",
            artworkUrl100 = "http://t.jpg"
        )

        val album = ItunesAlbumMapper.mapLookupResultsToAlbumDetail(listOf(t))

        assertNotNull(album)
        assertEquals(42L, album!!.collectionId)
        assertEquals("From Track", album.title)
        assertEquals("Solo", album.artistName)
        assertEquals(1, album.tracks.size)
    }
}
