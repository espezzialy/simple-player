package com.espezzialy.simpleplayer.presentation.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.espezzialy.simpleplayer.domain.usecase.GetAlbumDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlbumDetailUseCase: GetAlbumDetailUseCase
) : ViewModel() {

    private val collectionId: Long =
        checkNotNull(savedStateHandle.get<Long>(COLLECTION_ID_ARG)) {
            "collectionId obrigatorio na rota"
        }

    private val _state = MutableStateFlow(AlbumDetailState())
    val state: StateFlow<AlbumDetailState> = _state.asStateFlow()

    init {
        load()
    }

    fun onIntent(intent: AlbumDetailIntent) {
        when (intent) {
            AlbumDetailIntent.Retry -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { getAlbumDetailUseCase(collectionId) }
                .onSuccess { album ->
                    _state.update {
                        it.copy(album = album, isLoading = false, errorMessage = null)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            album = null,
                            isLoading = false,
                            errorMessage = e.message ?: "Não foi possível carregar o álbum."
                        )
                    }
                }
        }
    }

    private companion object {
        const val COLLECTION_ID_ARG = "collectionId"
    }
}
