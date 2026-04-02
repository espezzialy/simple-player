package com.espezzialy.simpleplayer.media

import org.junit.Assert.assertNull
import org.junit.Test

class PlayerNotificationTransportTest {
    @Test
    fun clear_nullsAllCallbacks() {
        val t = PlayerNotificationTransport()
        var skips = 0
        t.onSkipPrevious = { skips++ }
        t.onSkipNext = { skips++ }
        t.onPlayFromNotification = { skips++ }
        t.onPauseFromNotification = { skips++ }
        t.onSeekFromNotification = { skips++ }

        t.clear()

        assertNull(t.onSkipPrevious)
        assertNull(t.onSkipNext)
        assertNull(t.onPlayFromNotification)
        assertNull(t.onPauseFromNotification)
        assertNull(t.onSeekFromNotification)
    }
}
