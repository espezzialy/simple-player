package com.espezzialy.simpleplayer.presentation.player

import com.espezzialy.simpleplayer.domain.model.Song

data class PlayerState(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    /** `null` when there is no associated album (lookup). */
    val collectionId: Long?,
    val artworkUrl: String?,
    /** 0f..1f — mock until real playback exists. */
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
    data class SongSelectedFromPlaylist(val song: Song) : PlayerIntent
}
