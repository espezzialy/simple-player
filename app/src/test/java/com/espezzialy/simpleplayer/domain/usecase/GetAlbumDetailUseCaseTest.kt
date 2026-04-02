package com.espezzialy.simpleplayer.domain.usecase

import com.espezzialy.simpleplayer.domain.fakes.FakeSongRepository
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.AlbumTrack
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAlbumDetailUseCaseTest {
    private lateinit var repository: FakeSongRepository
    private lateinit var useCase: GetAlbumDetailUseCase

    @Before
    fun setup() {
        repository = FakeSongRepository()
        useCase = GetAlbumDetailUseCase(repository)
    }

    @Test
    fun forwardsCollectionIdToRepository() =
        runTest {
            val expected =
                AlbumDetail(
                    collectionId = 42L,
                    title = "RAM",
                    artistName = "Daft Punk",
                    artworkUrl = null,
                    tracks =
                        listOf(
                            AlbumTrack(1L, "A", "Daft Punk", null),
                        ),
                )
            repository.albumDetailResult = expected

            val result = useCase(42L)

            assertEquals(42L, repository.lastAlbumCollectionId)
            assertEquals(expected, result)
        }
}
