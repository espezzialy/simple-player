package com.espezzialy.simpleplayer.presentation.player

import org.junit.Assert.assertEquals
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
    fun formatMmSs_padsSeconds() {
        assertEquals("0:05", PlayerTimeFormatter.formatMmSs(5))
        assertEquals("10:00", PlayerTimeFormatter.formatMmSs(600))
    }
}
