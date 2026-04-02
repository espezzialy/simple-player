package com.espezzialy.simpleplayer.core.player

import com.espezzialy.simpleplayer.domain.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlayerQueueNavigationTest {
    private val a = Song(1L, "A", "ar", "al", 10L, null)
    private val b = Song(2L, "B", "ar", "al", 10L, null)
    private val c = Song(3L, "C", "ar", "al", 10L, null)

    @Test
    fun emptyQueue_returnsNull() {
        assertNull(
            computeSkipTargetIndex(
                queue = emptyList(),
                currentTrackId = 1L,
                delta = 1,
                repeatPlaylist = false,
            ),
        )
    }

    @Test
    fun currentTrackNotInQueue_returnsNull() {
        assertNull(
            computeSkipTargetIndex(
                queue = listOf(a, b),
                currentTrackId = 99L,
                delta = 1,
                repeatPlaylist = false,
            ),
        )
    }

    @Test
    fun next_fromMiddle_goesForward() {
        assertEquals(
            1,
            computeSkipTargetIndex(
                queue = listOf(a, b, c),
                currentTrackId = a.trackId,
                delta = 1,
                repeatPlaylist = false,
            ),
        )
    }

    @Test
    fun next_fromLast_withoutRepeat_returnsNull() {
        assertNull(
            computeSkipTargetIndex(
                queue = listOf(a, b, c),
                currentTrackId = c.trackId,
                delta = 1,
                repeatPlaylist = false,
            ),
        )
    }

    @Test
    fun next_fromLast_withRepeat_wrapsToFirst() {
        assertEquals(
            0,
            computeSkipTargetIndex(
                queue = listOf(a, b, c),
                currentTrackId = c.trackId,
                delta = 1,
                repeatPlaylist = true,
            ),
        )
    }

    @Test
    fun previous_fromFirst_withoutRepeat_returnsNull() {
        assertNull(
            computeSkipTargetIndex(
                queue = listOf(a, b, c),
                currentTrackId = a.trackId,
                delta = -1,
                repeatPlaylist = false,
            ),
        )
    }

    @Test
    fun previous_fromMiddle_goesBack() {
        assertEquals(
            0,
            computeSkipTargetIndex(
                queue = listOf(a, b, c),
                currentTrackId = b.trackId,
                delta = -1,
                repeatPlaylist = false,
            ),
        )
    }

    @Test
    fun singleTrack_repeatNext_wrapsToSameIndex() {
        assertEquals(
            0,
            computeSkipTargetIndex(
                queue = listOf(a),
                currentTrackId = a.trackId,
                delta = 1,
                repeatPlaylist = true,
            ),
        )
    }

    @Test
    fun repeatDisabled_doesNotWrapOnPreviousFromFirst() {
        assertNull(
            computeSkipTargetIndex(
                queue = listOf(a),
                currentTrackId = a.trackId,
                delta = -1,
                repeatPlaylist = true,
            ),
        )
    }
}
