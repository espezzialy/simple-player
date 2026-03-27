package com.espezzialy.simpleplayer.presentation.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.espezzialy.simpleplayer.domain.usecase.SearchSongsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SongsViewModel(
    private val searchSongsUseCase: SearchSongsUseCase
) : ViewModel() {

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
        searchJob = viewModelScope.launch {
            if (debounce) kotlinx.coroutines.delay(500)

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
                    val message = "Nao foi possivel buscar musicas. Tente novamente."
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

    class Factory(
        private val searchSongsUseCase: SearchSongsUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SongsViewModel::class.java)) {
                return SongsViewModel(searchSongsUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
