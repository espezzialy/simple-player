package com.espezzialy.simpleplayer.presentation.songs

import com.espezzialy.simpleplayer.domain.model.Song

data class SongsState(
    val query: String = "",
    /** Persistido em DataStore; mostrado quando a pesquisa está vazia. */
    val recentSongs: List<Song> = emptyList(),
    val songs: List<Song> = emptyList(),
    /**
     * Todos os resultados já obtidos para a query atual (até 200).
     * O painel do player usa isto; [songs] é só a janela visível na lista (scroll infinito).
     */
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
    /** Atualiza a pesquisa atual (pull-to-refresh na tela Songs). */
    data object Refresh : SongsIntent
    data object LoadMore : SongsIntent
    data object ClearRecentSongs : SongsIntent
}

sealed interface SongsEffect {
    data class ShowError(val message: String) : SongsEffect
}
