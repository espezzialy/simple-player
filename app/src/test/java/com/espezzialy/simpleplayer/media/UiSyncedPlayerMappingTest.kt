package com.espezzialy.simpleplayer.media

import androidx.media3.common.C
import androidx.media3.common.Player
import com.espezzialy.simpleplayer.presentation.player.PlayerUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class UiSyncedPlayerMappingTest {
    @Test
    fun durationMsFromUiState_null_returnsTimeUnset() {
        assertEquals(C.TIME_UNSET, durationMsFromUiState(null))
    }

    @Test
    fun durationMsFromUiState_zeroSeconds_returnsTimeUnset() {
        assertEquals(C.TIME_UNSET, durationMsFromUiState(sampleState(totalDurationSeconds = 0)))
    }

    @Test
    fun durationMsFromUiState_positive_returnsMillis() {
        assertEquals(180_000L, durationMsFromUiState(sampleState(totalDurationSeconds = 180)))
    }

    @Test
    fun currentPositionMs_halfProgress() {
        val d = 60_000L
        val s = sampleState(progress = 0.5f, totalDurationSeconds = 60)
        assertEquals(30_000L, currentPositionMs(s, d))
    }

    @Test
    fun currentPositionMs_clampsToDuration() {
        val d = 10_000L
        val s = sampleState(progress = 2f, totalDurationSeconds = 10)
        assertEquals(10_000L, currentPositionMs(s, d))
    }

    @Test
    fun currentPositionMs_invalidDuration_returnsZero() {
        assertEquals(0L, currentPositionMs(sampleState(), C.TIME_UNSET))
    }

    @Test
    fun playbackStateFromUiState_null_idle() {
        assertEquals(Player.STATE_IDLE, playbackStateFromUiState(null))
    }

    @Test
    fun playbackStateFromUiState_nonPositiveTrackId_idle() {
        assertEquals(Player.STATE_IDLE, playbackStateFromUiState(sampleState(trackId = 0L)))
    }

    @Test
    fun playbackStateFromUiState_positiveTrackId_ready() {
        assertEquals(Player.STATE_READY, playbackStateFromUiState(sampleState(trackId = 1L)))
    }

    private fun sampleState(
        trackId: Long = 1L,
        totalDurationSeconds: Int = 60,
        progress: Float = 0f,
    ): PlayerUiState =
        PlayerUiState(
            trackId = trackId,
            trackName = "T",
            artistName = "A",
            collectionId = null,
            artworkUrl = null,
            trackTimeMillis = null,
            progress = progress,
            isPlaying = false,
            currentTimeLabel = "0:00",
            remainingTimeLabel = "1:00",
            repeatEnabled = false,
            totalDurationSeconds = totalDurationSeconds,
        )
}
