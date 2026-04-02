package com.espezzialy.simpleplayer.data.songs

import android.content.Context
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.core.coroutines.DispatcherProvider
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.domain.model.SongsEffect
import com.espezzialy.simpleplayer.domain.model.SongsIntent
import com.espezzialy.simpleplayer.domain.model.SongsUiState
import com.espezzialy.simpleplayer.domain.usecase.SearchSongsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Inject
import javax.inject.Singleton

/**
 * iTunes Search ignores `offset`; the first "load more" refetches with a large limit, then paging is in-memory.
 * [MutableSharedFlow] lets Songs and Player both collect [effect] without a single consumer.
 */
@Singleton
class SongsSearchRepository
    @Inject
    constructor(
        private val searchSongsUseCase: SearchSongsUseCase,
        private val dispatcherProvider: DispatcherProvider,
        @ApplicationContext private val appContext: Context,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)

        private val _state = MutableStateFlow(SongsUiState())
        val state: StateFlow<SongsUiState> = _state.asStateFlow()

        private val _effect =
            MutableSharedFlow<SongsEffect>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )
        val effect: SharedFlow<SongsEffect> = _effect.asSharedFlow()

        private var searchJob: Job? = null
        private var loadMoreJob: Job? = null

        private var allSongs: List<Song> = emptyList()
        private var fullCatalogLoaded: Boolean = false

        fun onIntent(intent: SongsIntent) {
            when (intent) {
                is SongsIntent.QueryChanged -> onQueryChanged(intent.value)
                SongsIntent.RetrySearch -> triggerSearch(debounce = false, reset = true, isPullToRefresh = false)
                SongsIntent.Refresh -> refresh()
                SongsIntent.LoadMore -> loadMore()
                SongsIntent.ClearRecentSongs -> Unit
            }
        }

        private fun onQueryChanged(value: String) {
            _state.update {
                it.copy(
                    query = value,
                    errorMessage = null,
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
                        errorMessage = null,
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
            isPullToRefresh: Boolean = false,
        ) {
            searchJob?.cancel()
            loadMoreJob?.cancel()
            searchJob =
                scope.launch {
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
                                errorMessage = null,
                            )
                        } else {
                            it.copy(
                                isLoading = true,
                                isRefreshing = false,
                                isLoadingMore = false,
                                errorMessage = null,
                                hasMore = false,
                            )
                        }
                    }

                    try {
                        val page =
                            searchSongsUseCase(
                                query = query,
                                limit = SearchSongsUseCase.PAGE_SIZE,
                                offset = 0,
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
                                errorMessage = null,
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
                        errorMessage = message,
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
                        errorMessage = message,
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
                loadMoreJob =
                    scope.launch {
                        try {
                            _state.update { it.copy(isLoadingMore = true, errorMessage = null) }
                            val page =
                                searchSongsUseCase(
                                    query = query,
                                    limit = SearchSongsUseCase.MAX_ITUNES_TOTAL,
                                    offset = 0,
                                )
                            allSongs = page.songs.distinctBy { it.trackId }
                            fullCatalogLoaded = true
                            val previousVisible = _state.value.songs.size
                            val newSize =
                                minOf(
                                    previousVisible + SearchSongsUseCase.PAGE_SIZE,
                                    allSongs.size,
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
            val newSize =
                minOf(
                    current.songs.size + SearchSongsUseCase.PAGE_SIZE,
                    allSongs.size,
                )
            publishVisibleWindow(visibleCount = newSize)
        }

        private fun publishVisibleWindow(visibleCount: Int) {
            val n = visibleCount.coerceIn(0, allSongs.size)
            _state.update { current ->
                current.copy(
                    songs = allSongs.take(n),
                    fullResults = allSongs,
                    isLoadingMore = false,
                    hasMore = n < allSongs.size,
                )
            }
        }

        private companion object {
            const val QUERY_DEBOUNCE_MS = 500L
        }
    }
