package com.espezzialy.simpleplayer.presentation.songs

import com.espezzialy.simpleplayer.domain.model.Song

data class SongsState(
    val query: String = "",
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface SongsIntent {
    data class QueryChanged(val value: String) : SongsIntent
    data object RetrySearch : SongsIntent
}

sealed interface SongsEffect {
    data class ShowError(val message: String) : SongsEffect
}
