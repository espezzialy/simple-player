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
        val collection =
            ItunesSongDto(
                wrapperType = "collection",
                collectionId = 99L,
                collectionName = "Album X",
                artistName = "Artist",
                artworkUrl100 = "http://cover.jpg",
            )
        val b =
            ItunesSongDto(
                kind = "song",
                trackId = 2L,
                trackName = "Second",
                artistName = "Artist",
                collectionId = 99L,
                collectionName = "Album X",
                discNumber = 1,
                trackNumber = 2,
            )
        val a =
            ItunesSongDto(
                kind = "song",
                trackId = 1L,
                trackName = "First",
                artistName = "Artist",
                collectionId = 99L,
                collectionName = "Album X",
                discNumber = 1,
                trackNumber = 1,
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
        val t =
            ItunesSongDto(
                kind = "song",
                trackId = 5L,
                trackName = "Only",
                artistName = "Solo",
                collectionId = 42L,
                collectionName = "From Track",
                artworkUrl100 = "http://t.jpg",
            )

        val album = ItunesAlbumMapper.mapLookupResultsToAlbumDetail(listOf(t))

        assertNotNull(album)
        assertEquals(42L, album!!.collectionId)
        assertEquals("From Track", album.title)
        assertEquals("Solo", album.artistName)
        assertEquals(1, album.tracks.size)
    }

    @Test
    fun mapLookupResultsToAlbumDetail_returnsNull_whenNoCollectionIdAnywhere() {
        val bad =
            ItunesSongDto(
                kind = "song",
                trackId = 0L,
                collectionId = null,
            )
        assertNull(ItunesAlbumMapper.mapLookupResultsToAlbumDetail(listOf(bad)))
    }

    @Test
    fun mapLookupResultsToAlbumDetail_filtersNonPositiveTrackIds() {
        val invalid =
            ItunesSongDto(
                kind = "song",
                trackId = 0L,
                collectionId = 10L,
                collectionName = "Alb",
                artistName = "A",
            )
        val valid =
            ItunesSongDto(
                kind = "song",
                trackId = 1L,
                trackName = "Ok",
                artistName = "A",
                collectionId = 10L,
                collectionName = "Alb",
                trackNumber = 1,
            )
        val album =
            ItunesAlbumMapper.mapLookupResultsToAlbumDetail(listOf(invalid, valid))
        assertNotNull(album)
        assertEquals(1, album!!.tracks.size)
        assertEquals("Ok", album.tracks[0].trackName)
    }

    @Test
    fun mapLookupResultsToAlbumDetail_sortsByDiscThenTrackNumber() {
        val d2 =
            ItunesSongDto(
                kind = "song",
                trackId = 2L,
                trackName = "D2",
                artistName = "A",
                collectionId = 1L,
                collectionName = "X",
                discNumber = 2,
                trackNumber = 1,
            )
        val d1t2 =
            ItunesSongDto(
                kind = "song",
                trackId = 3L,
                trackName = "D1T2",
                artistName = "A",
                collectionId = 1L,
                collectionName = "X",
                discNumber = 1,
                trackNumber = 2,
            )
        val d1t1 =
            ItunesSongDto(
                kind = "song",
                trackId = 1L,
                trackName = "D1T1",
                artistName = "A",
                collectionId = 1L,
                collectionName = "X",
                discNumber = 1,
                trackNumber = 1,
            )
        val album =
            ItunesAlbumMapper.mapLookupResultsToAlbumDetail(listOf(d2, d1t2, d1t1))
        assertNotNull(album)
        assertEquals(listOf("D1T1", "D1T2", "D2"), album!!.tracks.map { it.trackName })
    }

    @Test
    fun mapLookupResultsToAlbumDetail_prefersArtwork600OnCollection() {
        val collection =
            ItunesSongDto(
                wrapperType = "collection",
                collectionId = 1L,
                collectionName = "A",
                artistName = "B",
                artworkUrl600 = "http://big.jpg",
                artworkUrl100 = "http://small.jpg",
            )
        val album =
            ItunesAlbumMapper.mapLookupResultsToAlbumDetail(
                listOf(
                    collection,
                    ItunesSongDto(
                        kind = "song",
                        trackId = 1L,
                        trackName = "T",
                        artistName = "B",
                        collectionId = 1L,
                        collectionName = "A",
                    ),
                ),
            )
        assertEquals("http://big.jpg", album!!.artworkUrl)
    }
}
