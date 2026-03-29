package com.espezzialy.simpleplayer.presentation.player

import com.espezzialy.simpleplayer.domain.model.Song
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Painel do player (tablet): lista da pesquisa em Songs ou faixas de um álbum. */
sealed interface PlayerSidePanelSource {
    data object SearchResults : PlayerSidePanelSource
    data class AlbumTracks(val albumTitle: String, val songs: List<Song>) : PlayerSidePanelSource
}

/**
 * Define a origem da lista do painel lateral do player.
 * [PlayerSidePanelSource.SearchResults] usa [com.espezzialy.simpleplayer.presentation.songs.SongsSearchRepository].
 */
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
}

data class PlayerSidePanelUiState(
    val songs: List<Song>,
    val panelTitle: String?,
    val isSearchMode: Boolean,
    val isLoading: Boolean,
    val errorMessage: String?,
    val showEmptyQueryHint: Boolean
)
