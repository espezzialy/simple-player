package com.espezzialy.simpleplayer.presentation.player

import com.espezzialy.simpleplayer.domain.model.Song
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface PlayerSidePanelSource {
    data object SearchResults : PlayerSidePanelSource
    data class AlbumTracks(val albumTitle: String, val songs: List<Song>) : PlayerSidePanelSource
    data class RecentSongs(val songs: List<Song>) : PlayerSidePanelSource
}

@Singleton
class PlayerSidePanelSession @Inject constructor() {

    private val _source = MutableStateFlow<PlayerSidePanelSource>(PlayerSidePanelSource.SearchResults)
    val source: StateFlow<PlayerSidePanelSource> = _source.asStateFlow()

    fun setSearchResults() {
        _source.value = PlayerSidePanelSource.SearchResults
    }

    fun setAlbumTracks(albumTitle: String, songs: List<Song>) {
        _source.value = PlayerSidePanelSource.AlbumTracks(albumTitle, songs)
    }

    fun setRecentSongs(songs: List<Song>) {
        _source.value = PlayerSidePanelSource.RecentSongs(songs)
    }
}

data class PlayerSidePanelUiState(
    val songs: List<Song>,
    val panelTitle: String?,
    val isSearchMode: Boolean,
    val isLoading: Boolean,
    val errorMessage: String?,
    val showEmptyQueryHint: Boolean
)
