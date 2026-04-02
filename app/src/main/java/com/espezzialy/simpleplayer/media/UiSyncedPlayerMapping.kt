package com.espezzialy.simpleplayer.media

import androidx.media3.common.C
import androidx.media3.common.Player
import com.espezzialy.simpleplayer.presentation.player.PlayerUiState

internal fun durationMsFromUiState(state: PlayerUiState?): Long {
    val s = state ?: return C.TIME_UNSET
    val ms = s.totalDurationSeconds * 1000L
    return if (ms > 0L) ms else C.TIME_UNSET
}

internal fun currentPositionMs(
    state: PlayerUiState?,
    durationMs: Long,
): Long {
    val s = state ?: return 0L
    if (durationMs == C.TIME_UNSET || durationMs <= 0L) return 0L
    return (s.progress * durationMs).toLong().coerceIn(0L, durationMs)
}

internal fun playbackStateFromUiState(state: PlayerUiState?): Int {
    val s = state ?: return Player.STATE_IDLE
    return if (s.trackId > 0L) Player.STATE_READY else Player.STATE_IDLE
}
