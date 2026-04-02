package com.espezzialy.simpleplayer.data.repository

import com.espezzialy.simpleplayer.data.fakes.FakeSongsRemoteDataSource
import com.espezzialy.simpleplayer.data.remote.RemoteSearchSongsPage
import com.espezzialy.simpleplayer.data.remote.model.ItunesSongDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class SongRepositoryImplTest {
    private lateinit var remote: FakeSongsRemoteDataSource
    private lateinit var repository: SongRepositoryImpl

    @Before
    fun setup() {
        remote = FakeSongsRemoteDataSource()
        repository = SongRepositoryImpl(remote)
    }

    @Test
    fun searchSongs_mapsDtosAndPassesCounts() =
        runTest {
            val dto =
                ItunesSongDto(
                    trackId = 5L,
                    trackName = "X",
                    artistName = "Y",
                    collectionName = "Z",
                    collectionId = 1L,
                    artworkUrl100 = "u",
                )
            remote.searchPage =
                RemoteSearchSongsPage(
                    dtos = listOf(dto),
                    apiConsumedCount = 3,
                )

            val page = repository.searchSongs("q", limit = 25, offset = 0)

            assertEquals("q", remote.lastSearchTerm)
            assertEquals(25, remote.lastSearchLimit)
            assertEquals(0, remote.lastSearchOffset)
            assertEquals(1, page.songs.size)
            assertEquals(5L, page.songs[0].trackId)
            assertEquals(3, page.apiConsumedCount)
        }

    @Test
    fun getAlbumDetail_delegatesLookupAndMaps() =
        runTest {
            remote.lookupResults =
                listOf(
                    ItunesSongDto(
                        wrapperType = "collection",
                        collectionId = 9L,
                        collectionName = "Album",
                        artistName = "Art",
                        artworkUrl100 = "http://c.jpg",
                    ),
                    ItunesSongDto(
                        kind = "song",
                        trackId = 1L,
                        trackName = "One",
                        artistName = "Art",
                        collectionId = 9L,
                        collectionName = "Album",
                        discNumber = 1,
                        trackNumber = 1,
                    ),
                )

            val album = repository.getAlbumDetail(9L)

            assertEquals(9L, remote.lastLookupCollectionId)
            assertEquals(9L, album.collectionId)
            assertEquals("Album", album.title)
            assertEquals(1, album.tracks.size)
            assertEquals("One", album.tracks[0].trackName)
        }

    @Test
    fun getAlbumDetail_throwsWhenMapperReturnsNull() =
        runTest {
            remote.lookupResults = emptyList()
            try {
                repository.getAlbumDetail(1L)
                fail("Expected IllegalStateException")
            } catch (e: IllegalStateException) {
                assertTrue(e.message!!.contains("album", ignoreCase = true))
            }
        }
}
