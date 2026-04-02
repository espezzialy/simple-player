package com.espezzialy.simpleplayer.presentation.album

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.data.session.PlayerSidePanelSession
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.domain.usecase.GetAlbumDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val getAlbumDetailUseCase: GetAlbumDetailUseCase,
        private val playerSidePanelSession: PlayerSidePanelSession,
        @ApplicationContext private val appContext: Context,
    ) : ViewModel() {
        private val collectionId: Long =
            checkNotNull(savedStateHandle.get<Long>(COLLECTION_ID_ARG)) {
                "collectionId is required in route"
            }

        private val _state = MutableStateFlow(AlbumDetailUiState())
        val state: StateFlow<AlbumDetailUiState> = _state.asStateFlow()

        init {
            load()
        }

        fun onIntent(intent: AlbumDetailIntent) {
            when (intent) {
                AlbumDetailIntent.Retry -> load()
            }
        }

        fun preparePlayerFromAlbum(album: AlbumDetail) {
            val songs =
                album.tracks.map { track ->
                    Song(
                        trackId = track.trackId,
                        trackName = track.trackName,
                        artistName = track.artistName,
                        collectionName = album.title,
                        collectionId = album.collectionId,
                        artworkUrl100 = track.artworkUrl100,
                        trackTimeMillis = track.trackTimeMillis,
                    )
                }
            playerSidePanelSession.setAlbumTracks(album.title, songs)
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
                                errorMessage = e.message ?: appContext.getString(R.string.error_load_album),
                            )
                        }
                    }
            }
        }

        private companion object {
            const val COLLECTION_ID_ARG = "collectionId"
        }
    }
