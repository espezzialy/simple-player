package com.espezzialy.simpleplayer.presentation.songs

import androidx.lifecycle.ViewModel
import com.espezzialy.simpleplayer.presentation.player.PlayerSidePanelSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songsSearchRepository: SongsSearchRepository,
    private val playerSidePanelSession: PlayerSidePanelSession
) : ViewModel() {

    val state: StateFlow<SongsState> = songsSearchRepository.state

    val effect: SharedFlow<SongsEffect> = songsSearchRepository.effect

    fun onIntent(intent: SongsIntent) {
        songsSearchRepository.onIntent(intent)
    }

    /** Ao abrir o player a partir da pesquisa, o painel (tablet) usa a lista da pesquisa. */
    fun preparePlayerFromSearch() {
        playerSidePanelSession.setSearchResults()
    }
}
