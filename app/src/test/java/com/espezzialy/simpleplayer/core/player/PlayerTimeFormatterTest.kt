package com.espezzialy.simpleplayer.core.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PlayerTimeFormatterTest {
    @Test
    fun labelsForProgress_matchesMockTrackPosition() {
        val total = 260
        val progress = 86f / total
        val (current, remaining) = PlayerTimeFormatter.labelsForProgress(progress, total)
        assertEquals("1:26", current)
        assertEquals("-2:54", remaining)
    }

    @Test
    fun labelsForProgress_atStartAndEnd() {
        val total = 60
        val start = PlayerTimeFormatter.labelsForProgress(0f, total)
        assertEquals("0:00", start.first)
        assertEquals("-1:00", start.second)

        val end = PlayerTimeFormatter.labelsForProgress(1f, total)
        assertEquals("1:00", end.first)
        assertEquals("-0:00", end.second)
    }

    @Test
    fun labelsForProgress_coercesProgressAboveOne() {
        val total = 100
        val (current, remaining) = PlayerTimeFormatter.labelsForProgress(1.5f, total)
        assertEquals("1:40", current)
        assertEquals("-0:00", remaining)
    }

    @Test
    fun formatMmSs_padsSeconds() {
        assertEquals("0:05", PlayerTimeFormatter.formatMmSs(5))
        assertEquals("10:00", PlayerTimeFormatter.formatMmSs(600))
    }

    @Test
    fun formatMmSs_rejectsNegative() {
        assertThrows(IllegalArgumentException::class.java) {
            PlayerTimeFormatter.formatMmSs(-1)
        }
    }

    @Test
    fun labelsForProgress_rejectsNegativeTotal() {
        assertThrows(IllegalArgumentException::class.java) {
            PlayerTimeFormatter.labelsForProgress(0.5f, -1)
        }
    }
}
