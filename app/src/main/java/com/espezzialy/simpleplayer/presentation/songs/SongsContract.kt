package com.espezzialy.simpleplayer.presentation.songs

import com.espezzialy.simpleplayer.domain.model.Song

data class SongsState(
    val query: String = "",
    val songs: List<Song> = emptyList(),
    /**
     * Todos os resultados já obtidos para a query atual (até 200).
     * O painel do player usa isto; [songs] é só a janela visível na lista (scroll infinito).
     */
    val fullResults: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = false,
    val errorMessage: String? = null
)

sealed interface SongsIntent {
    data class QueryChanged(val value: String) : SongsIntent
    data object RetrySearch : SongsIntent
    data object LoadMore : SongsIntent
}

sealed interface SongsEffect {
    data class ShowError(val message: String) : SongsEffect
}
