package com.espezzialy.simpleplayer.presentation.songs

import android.content.Context
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.core.coroutines.DispatcherProvider
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.domain.usecase.SearchSongsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de busca compartilhado entre a tela Songs e o painel lateral do player (tablet),
 * para manter a mesma query e lista ao navegar. A paginação por scroll existe só na tela Songs.
 *
 * **Rede:** primeiro pedido só com [SearchSongsUseCase.PAGE_SIZE] (25) para resposta rápida.
 * A API iTunes ignora `offset`, por isso no **primeiro** “carregar mais” pedimos até
 * [SearchSongsUseCase.MAX_ITUNES_TOTAL] (o resultado inclui o mesmo prefixo de 25 + o resto).
 * Depois disso, mais páginas de 25 são só em memória.
 *
 * Efeitos usam [MutableSharedFlow] (e não Channel) para vários coletores (Songs + Player) sem
 * competição por um único consumidor.
 */
@Singleton
class SongsSearchRepository @Inject constructor(
    private val searchSongsUseCase: SearchSongsUseCase,
    private val dispatcherProvider: DispatcherProvider,
    @ApplicationContext private val appContext: Context
) {

    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)

    private val _state = MutableStateFlow(SongsState())
    val state: StateFlow<SongsState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SongsEffect>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<SongsEffect> = _effect.asSharedFlow()

    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null

    /** Lista canónica após o último pedido relevante; até 200 quando o catálogo completo foi pedido. */
    private var allSongs: List<Song> = emptyList()

    /** `false` até o primeiro [loadMore] com pedido grande (ou não ser necessário). */
    private var fullCatalogLoaded: Boolean = false

    fun onIntent(intent: SongsIntent) {
        when (intent) {
            is SongsIntent.QueryChanged -> onQueryChanged(intent.value)
            SongsIntent.RetrySearch -> triggerSearch(debounce = false, reset = true, isPullToRefresh = false)
            SongsIntent.Refresh -> refresh()
            SongsIntent.LoadMore -> loadMore()
        }
    }

    private fun onQueryChanged(value: String) {
        _state.update {
            it.copy(
                query = value,
                errorMessage = null
            )
        }

        if (value.isBlank()) {
            searchJob?.cancel()
            loadMoreJob?.cancel()
            resetSearchData()
            _state.update { current ->
                current.copy(
                    songs = emptyList(),
                    fullResults = emptyList(),
                    isLoading = false,
                    isRefreshing = false,
                    isLoadingMore = false,
                    hasMore = false,
                    errorMessage = null
                )
            }
            return
        }

        triggerSearch(debounce = true, reset = true, isPullToRefresh = false)
    }

    private fun refresh() {
        val q = _state.value.query.trim()
        if (q.isBlank()) return
        val pullStyle = _state.value.songs.isNotEmpty()
        triggerSearch(debounce = false, reset = true, isPullToRefresh = pullStyle)
    }

    private fun resetSearchData() {
        allSongs = emptyList()
        fullCatalogLoaded = false
    }

    private fun triggerSearch(
        debounce: Boolean = false,
        reset: Boolean = true,
        isPullToRefresh: Boolean = false
    ) {
        searchJob?.cancel()
        loadMoreJob?.cancel()
        searchJob = scope.launch {
            if (debounce) delay(QUERY_DEBOUNCE_MS)

            val query = _state.value.query.trim()
            if (query.isBlank()) return@launch

            if (reset) {
                if (isPullToRefresh) {
                    fullCatalogLoaded = false
                } else {
                    resetSearchData()
                }
            }

            _state.update {
                if (isPullToRefresh) {
                    it.copy(
                        isRefreshing = true,
                        errorMessage = null
                    )
                } else {
                    it.copy(
                        isLoading = true,
                        isRefreshing = false,
                        isLoadingMore = false,
                        errorMessage = null,
                        hasMore = false
                    )
                }
            }

            try {
                val page = searchSongsUseCase(
                    query = query,
                    limit = SearchSongsUseCase.PAGE_SIZE,
                    offset = 0
                )
                allSongs = page.songs.distinctBy { it.trackId }
                val first = allSongs.take(SearchSongsUseCase.PAGE_SIZE)
                val mayHaveMore = allSongs.size >= SearchSongsUseCase.PAGE_SIZE
                _state.update {
                    it.copy(
                        songs = first,
                        fullResults = allSongs,
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        hasMore = mayHaveMore,
                        errorMessage = null
                    )
                }
            } catch (e: CancellationException) {
                _state.update { it.copy(isRefreshing = false, isLoading = false) }
                throw e
            } catch (_: Exception) {
                emitSearchFailure(isPullToRefresh = isPullToRefresh)
            }
        }
    }

    private fun emitSearchFailure(isPullToRefresh: Boolean = false) {
        val message = appContext.getString(R.string.error_search_songs)
        if (isPullToRefresh) {
            _state.update {
                it.copy(
                    isRefreshing = false,
                    errorMessage = message
                )
            }
        } else {
            resetSearchData()
            _state.update {
                it.copy(
                    songs = emptyList(),
                    fullResults = emptyList(),
                    isLoading = false,
                    isRefreshing = false,
                    isLoadingMore = false,
                    hasMore = false,
                    errorMessage = message
                )
            }
        }
        _effect.tryEmit(SongsEffect.ShowError(message))
    }

    private fun loadMore() {
        val s = _state.value
        if (!s.hasMore || s.isLoadingMore || s.isLoading || s.isRefreshing || s.query.isBlank()) return

        val query = s.query.trim()

        if (!fullCatalogLoaded) {
            loadMoreJob?.cancel()
            loadMoreJob = scope.launch {
                try {
                    _state.update { it.copy(isLoadingMore = true, errorMessage = null) }
                    val page = searchSongsUseCase(
                        query = query,
                        limit = SearchSongsUseCase.MAX_ITUNES_TOTAL,
                        offset = 0
                    )
                    allSongs = page.songs.distinctBy { it.trackId }
                    fullCatalogLoaded = true
                    val previousVisible = _state.value.songs.size
                    val newSize = minOf(
                        previousVisible + SearchSongsUseCase.PAGE_SIZE,
                        allSongs.size
                    )
                    publishVisibleWindow(visibleCount = newSize)
                } catch (e: CancellationException) {
                    _state.update { it.copy(isLoadingMore = false) }
                    throw e
                } catch (_: Exception) {
                    val message = appContext.getString(R.string.error_search_songs)
                    _state.update { it.copy(isLoadingMore = false) }
                    _effect.tryEmit(SongsEffect.ShowError(message))
                }
            }
            return
        }

        val current = _state.value
        val newSize = minOf(
            current.songs.size + SearchSongsUseCase.PAGE_SIZE,
            allSongs.size
        )
        publishVisibleWindow(visibleCount = newSize)
    }

    /** Atualiza [SongsState.songs] com os primeiros [visibleCount] itens de [allSongs]. */
    private fun publishVisibleWindow(visibleCount: Int) {
        val n = visibleCount.coerceIn(0, allSongs.size)
        _state.update { current ->
            current.copy(
                songs = allSongs.take(n),
                fullResults = allSongs,
                isLoadingMore = false,
                hasMore = n < allSongs.size
            )
        }
    }

    private companion object {
        const val QUERY_DEBOUNCE_MS = 500L
    }
}
