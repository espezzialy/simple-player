package com.espezzialy.simpleplayer.presentation.player

import com.espezzialy.simpleplayer.domain.model.Song

data class PlayerUiState(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val collectionId: Long?,
    val artworkUrl: String?,
    val progress: Float,
    val isPlaying: Boolean,
    val currentTimeLabel: String,
    val remainingTimeLabel: String,
    val repeatEnabled: Boolean
)

data class PlayerSidePanelUiState(
    val songs: List<Song>,
    val panelTitle: String?,
    val isSearchMode: Boolean,
    val isLoading: Boolean,
    val errorMessage: String?,
    val showEmptyQueryHint: Boolean
)
