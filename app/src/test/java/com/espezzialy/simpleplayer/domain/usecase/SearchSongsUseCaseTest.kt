package com.espezzialy.simpleplayer.domain.usecase

import com.espezzialy.simpleplayer.domain.fakes.FakeSongRepository
import com.espezzialy.simpleplayer.domain.model.PagedSongs
import com.espezzialy.simpleplayer.domain.model.Song
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SearchSongsUseCaseTest {
    private lateinit var repository: FakeSongRepository
    private lateinit var useCase: SearchSongsUseCase

    @Before
    fun setup() {
        repository = FakeSongRepository()
        useCase = SearchSongsUseCase(repository)
    }

    @Test
    fun blankQuery_doesNotCallRepository() =
        runTest {
            val result = useCase("   ")
            assertEquals(0, result.songs.size)
            assertNull(repository.lastSearchTerm)
        }

    @Test
    fun trimsQuery_beforeSearch() =
        runTest {
            repository.searchSongsResult =
                PagedSongs(
                    songs =
                        listOf(
                            Song(1L, "A", "B", "C", 1L, null),
                        ),
                    apiConsumedCount = 1,
                )
            useCase("  daft  ")
            assertEquals("daft", repository.lastSearchTerm)
        }

    @Test
    fun offsetAtOrAboveMax_returnsEmptyWithoutRepositoryCall() =
        runTest {
            val result = useCase("test", limit = 25, offset = SearchSongsUseCase.MAX_ITUNES_TOTAL)
            assertEquals(0, result.songs.size)
            assertNull(repository.lastSearchTerm)
        }

    @Test
    fun effectiveLimit_cappedByRemainingQuota() =
        runTest {
            repository.searchSongsResult = PagedSongs(emptyList(), 0)
            val offset = SearchSongsUseCase.MAX_ITUNES_TOTAL - 10
            useCase("x", limit = 25, offset = offset)
            assertEquals(10, repository.lastSearchLimit)
        }

    @Test
    fun delegatesToRepository_withNormalizedTerm() =
        runTest {
            repository.searchSongsResult = PagedSongs(emptyList(), 0)
            useCase("radiohead", limit = 25, offset = 0)
            assertEquals("radiohead", repository.lastSearchTerm)
            assertEquals(25, repository.lastSearchLimit)
            assertEquals(0, repository.lastSearchOffset)
        }

    @Test
    fun effectiveLimit_whenOneResultRemaining() =
        runTest {
            repository.searchSongsResult = PagedSongs(emptyList(), 0)
            val offset = SearchSongsUseCase.MAX_ITUNES_TOTAL - 1
            useCase("q", limit = 25, offset = offset)
            assertEquals(1, repository.lastSearchLimit)
        }

    @Test
    fun negativeOffset_treatsRemainingAsMoreThanMax() =
        runTest {
            repository.searchSongsResult = PagedSongs(emptyList(), 0)
            useCase("q", limit = 25, offset = -5)
            assertEquals(25, repository.lastSearchLimit)
        }
}
