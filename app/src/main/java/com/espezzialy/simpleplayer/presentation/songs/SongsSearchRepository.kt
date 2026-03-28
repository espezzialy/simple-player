package com.espezzialy.simpleplayer.presentation.songs

import android.content.Context
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.core.coroutines.DispatcherProvider
import com.espezzialy.simpleplayer.domain.usecase.SearchSongsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de busca compartilhado entre a tela Songs e o painel lateral do player (tablet),
 * para manter a mesma query e resultados ao navegar.
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

    private val _effect = Channel<SongsEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var searchJob: Job? = null

    fun onIntent(intent: SongsIntent) {
        when (intent) {
            is SongsIntent.QueryChanged -> onQueryChanged(intent.value)
            SongsIntent.RetrySearch -> triggerSearch()
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
            _state.update { current ->
                current.copy(
                    songs = emptyList(),
                    isLoading = false,
                    errorMessage = null
                )
            }
            return
        }

        triggerSearch(debounce = true)
    }

    private fun triggerSearch(debounce: Boolean = false) {
        searchJob?.cancel()
        searchJob = scope.launch {
            if (debounce) delay(500)

            val query = _state.value.query.trim()
            if (query.isBlank()) return@launch

            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { searchSongsUseCase(query) }
                .onSuccess { songs ->
                    _state.update {
                        it.copy(
                            songs = songs,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure {
                    val message = appContext.getString(R.string.error_search_songs)
                    _state.update {
                        it.copy(
                            songs = emptyList(),
                            isLoading = false,
                            errorMessage = message
                        )
                    }
                    _effect.send(SongsEffect.ShowError(message))
                }
        }
    }
}
