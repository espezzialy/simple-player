package com.espezzialy.simpleplayer.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val totalSeconds = MOCK_TOTAL_SECONDS
    private val initialProgress = MOCK_INITIAL_ELAPSED_SECONDS.toFloat() / MOCK_TOTAL_SECONDS

    private val _state = MutableStateFlow(
        buildState(
            trackName = savedStateHandle.get<String>(PlayerNavigation.ARG_TRACK_NAME).orEmpty(),
            artistName = savedStateHandle.get<String>(PlayerNavigation.ARG_ARTIST_NAME).orEmpty(),
            collectionId = savedStateHandle.get<Long>(PlayerNavigation.ARG_COLLECTION_ID)
                ?.takeUnless { it == PlayerNavigation.NO_COLLECTION_ID },
            artworkUrl = savedStateHandle.get<String>(PlayerNavigation.ARG_ARTWORK_URL)
                ?.takeIf { it.isNotBlank() },
            progress = initialProgress,
            isPlaying = false,
            repeatEnabled = false
        )
    )
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    fun onIntent(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.ProgressChanged -> updateProgress(intent.value)
            PlayerIntent.PlayPauseClicked -> togglePlayPause()
            PlayerIntent.SkipPreviousClicked,
            PlayerIntent.SkipNextClicked -> {
                // No play queue — keep mock behavior.
            }
            PlayerIntent.RepeatClicked -> {
                _state.update { it.copy(repeatEnabled = !it.repeatEnabled) }
            }
        }
    }

    private fun togglePlayPause() {
        _state.update { it.copy(isPlaying = !it.isPlaying) }
    }

    private fun updateProgress(value: Float) {
        val p = value.coerceIn(0f, 1f)
        val (current, remaining) = PlayerTimeFormatter.labelsForProgress(p, totalSeconds)
        _state.update {
            it.copy(
                progress = p,
                currentTimeLabel = current,
                remainingTimeLabel = remaining
            )
        }
    }

    private fun buildState(
        trackName: String,
        artistName: String,
        collectionId: Long?,
        artworkUrl: String?,
        progress: Float,
        isPlaying: Boolean,
        repeatEnabled: Boolean
    ): PlayerState {
        val (current, remaining) = PlayerTimeFormatter.labelsForProgress(progress, totalSeconds)
        return PlayerState(
            trackName = trackName,
            artistName = artistName,
            collectionId = collectionId,
            artworkUrl = artworkUrl,
            progress = progress,
            isPlaying = isPlaying,
            currentTimeLabel = current,
            remainingTimeLabel = remaining,
            repeatEnabled = repeatEnabled
        )
    }

    private companion object {
        /** Mock total duration (~4:20). */
        const val MOCK_TOTAL_SECONDS = 260
        /** Mock initial position (~1:26). */
        const val MOCK_INITIAL_ELAPSED_SECONDS = 86
    }
}
