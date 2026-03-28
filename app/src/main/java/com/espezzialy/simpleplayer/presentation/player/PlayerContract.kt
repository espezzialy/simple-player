package com.espezzialy.simpleplayer.presentation.player

data class PlayerState(
    val trackName: String,
    val artistName: String,
    val artworkUrl: String?,
    /** 0f..1f — mock até existir reprodução real. */
    val progress: Float,
    val isPlaying: Boolean,
    val currentTimeLabel: String,
    val remainingTimeLabel: String,
    val repeatEnabled: Boolean
)

sealed interface PlayerIntent {
    data class ProgressChanged(val value: Float) : PlayerIntent
    data object PlayPauseClicked : PlayerIntent
    data object SkipPreviousClicked : PlayerIntent
    data object SkipNextClicked : PlayerIntent
    data object RepeatClicked : PlayerIntent
}
