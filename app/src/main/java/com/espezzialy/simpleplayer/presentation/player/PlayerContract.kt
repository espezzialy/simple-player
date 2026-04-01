package com.espezzialy.simpleplayer.presentation.player

import com.espezzialy.simpleplayer.domain.model.Song

sealed interface PlayerIntent {
    data class ProgressChanged(val value: Float) : PlayerIntent
    data object PlayPauseClicked : PlayerIntent
    data object SkipPreviousClicked : PlayerIntent
    data object SkipNextClicked : PlayerIntent
    data object RepeatClicked : PlayerIntent
    data class SongSelectedFromPlaylist(val song: Song) : PlayerIntent
}
