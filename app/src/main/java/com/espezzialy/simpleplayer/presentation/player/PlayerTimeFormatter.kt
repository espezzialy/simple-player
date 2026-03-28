package com.espezzialy.simpleplayer.presentation.player

import kotlin.math.roundToInt

/**
 * Time formatting for the mock slider (no [android.text.format.DateUtils] for easier JVM tests).
 */
object PlayerTimeFormatter {

    fun labelsForProgress(progress: Float, totalSeconds: Int): Pair<String, String> {
        require(totalSeconds >= 0)
        val p = progress.coerceIn(0f, 1f)
        val current = (p * totalSeconds).roundToInt().coerceIn(0, totalSeconds)
        val remaining = (totalSeconds - current).coerceAtLeast(0)
        return formatMmSs(current) to "-${formatMmSs(remaining)}"
    }

    fun formatMmSs(totalSeconds: Int): String {
        require(totalSeconds >= 0)
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return "$m:${s.toString().padStart(2, '0')}"
    }
}
