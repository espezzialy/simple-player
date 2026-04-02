package com.espezzialy.simpleplayer.presentation.player

import com.espezzialy.simpleplayer.presentation.navigation.PlayerNavigation
import com.espezzialy.simpleplayer.presentation.navigation.playerRouteFor
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PlayerUiStateNotificationMappingTest {
    @Test
    fun toSongForNotification_roundTripsThroughPlayerRoute() {
        val state =
            PlayerUiState(
                trackId = 42L,
                trackName = "Track",
                artistName = "Artist",
                collectionId = 7L,
                artworkUrl = "https://example.com/a.jpg",
                trackTimeMillis = 200_000L,
                progress = 0f,
                isPlaying = true,
                currentTimeLabel = "0:00",
                remainingTimeLabel = "3:00",
                repeatEnabled = false,
                totalDurationSeconds = 180,
            )
        val route = playerRouteFor(state.toSongForNotification())
        assertTrue(route.contains("/42/"))
        assertTrue(route.contains("Track"))
        assertTrue(route.contains("7/200000"))
    }

    @Test
    fun toSongForNotification_nullCollection_usesNavigationPlaceholders() {
        val state =
            PlayerUiState(
                trackId = 1L,
                trackName = "T",
                artistName = "A",
                collectionId = null,
                artworkUrl = null,
                trackTimeMillis = null,
                progress = 0f,
                isPlaying = false,
                currentTimeLabel = "",
                remainingTimeLabel = "",
                repeatEnabled = false,
                totalDurationSeconds = 60,
            )
        val route = playerRouteFor(state.toSongForNotification())
        assertTrue(
            route.endsWith(
                "/${PlayerNavigation.NO_COLLECTION_ID}/${PlayerNavigation.NO_TRACK_TIME_MILLIS}",
            ),
        )
    }
}
