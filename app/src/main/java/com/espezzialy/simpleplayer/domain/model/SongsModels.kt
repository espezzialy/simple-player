package com.espezzialy.simpleplayer.domain.model

data class SongsUiState(
    val query: String = "",
    val recentSongs: List<Song> = emptyList(),
    val songs: List<Song> = emptyList(),
    val fullResults: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = false,
    val errorMessage: String? = null
)

sealed interface SongsIntent {
    data class QueryChanged(val value: String) : SongsIntent
    data object RetrySearch : SongsIntent
    data object Refresh : SongsIntent
    data object LoadMore : SongsIntent
    data object ClearRecentSongs : SongsIntent
}

sealed interface SongsEffect {
    data class ShowError(val message: String) : SongsEffect
}
