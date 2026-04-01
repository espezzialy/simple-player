package com.espezzialy.simpleplayer.presentation.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.espezzialy.simpleplayer.data.local.RecentSongsRepository
import com.espezzialy.simpleplayer.data.session.PlayerSidePanelSession
import com.espezzialy.simpleplayer.data.songs.SongsSearchRepository
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.domain.model.SongsEffect
import com.espezzialy.simpleplayer.domain.model.SongsIntent
import com.espezzialy.simpleplayer.domain.model.SongsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songsSearchRepository: SongsSearchRepository,
    private val recentSongsRepository: RecentSongsRepository,
    private val playerSidePanelSession: PlayerSidePanelSession
) : ViewModel() {

    val state: StateFlow<SongsUiState> = combine(
        songsSearchRepository.state,
        recentSongsRepository.recentSongs
    ) { search, recent ->
        search.copy(recentSongs = recent)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SongsUiState()
    )

    val effect: SharedFlow<SongsEffect> = songsSearchRepository.effect

    fun onIntent(intent: SongsIntent) {
        when (intent) {
            SongsIntent.ClearRecentSongs -> viewModelScope.launch {
                recentSongsRepository.clear()
            }
            else -> songsSearchRepository.onIntent(intent)
        }
    }

    fun onSongSelectedForPlayer(song: Song, fromRecentSection: Boolean, onReady: () -> Unit) {
        viewModelScope.launch {
            val queueAfterAdd = recentSongsRepository.add(song)
            if (fromRecentSection) {
                playerSidePanelSession.setRecentSongs(queueAfterAdd)
            } else {
                preparePlayerFromSearch()
            }
            onReady()
        }
    }

    fun preparePlayerFromSearch() {
        playerSidePanelSession.setSearchResults()
    }
}
