package com.espezzialy.simpleplayer.presentation.songs

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songsSearchRepository: SongsSearchRepository
) : ViewModel() {

    val state: StateFlow<SongsState> = songsSearchRepository.state

    val effect = songsSearchRepository.effect

    fun onIntent(intent: SongsIntent) {
        songsSearchRepository.onIntent(intent)
    }
}
