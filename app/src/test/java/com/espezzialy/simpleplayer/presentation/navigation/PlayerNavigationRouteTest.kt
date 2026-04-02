package com.espezzialy.simpleplayer.presentation.navigation

import com.espezzialy.simpleplayer.domain.model.Song
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PlayerNavigationRouteTest {
    @Test
    fun playerRouteFor_encodesSlashAndAmpersandInNames() {
        val song =
            Song(
                trackId = 7L,
                trackName = "A/B",
                artistName = "C&D",
                collectionName = "LP",
                collectionId = 3L,
                artworkUrl100 = null,
            )
        val route = playerRouteFor(song)
        assertTrue(route.contains("A%2FB"))
        assertTrue(route.contains("C%26D"))
        assertTrue(route.contains("/7/"))
    }

    @Test
    fun playerRouteFor_usesPlaceholder_whenCollectionIdNull() {
        val song =
            Song(
                trackId = 1L,
                trackName = "T",
                artistName = "A",
                collectionName = "C",
                collectionId = null,
                artworkUrl100 = null,
            )
        val route = playerRouteFor(song)
        assertTrue(route.endsWith("/${PlayerNavigation.NO_COLLECTION_ID}"))
    }
}
