package com.espezzialy.simpleplayer.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.presentation.songs.SongsEffect
import com.espezzialy.simpleplayer.presentation.songs.SongsIntent
import com.espezzialy.simpleplayer.presentation.songs.SongsSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val songsSearchRepository: SongsSearchRepository,
    private val sidePanelSession: PlayerSidePanelSession
) : ViewModel() {

    val sidePanelUiState: StateFlow<PlayerSidePanelUiState> = combine(
        sidePanelSession.source,
        songsSearchRepository.state
    ) { source, search ->
        when (source) {
            is PlayerSidePanelSource.AlbumTracks -> PlayerSidePanelUiState(
                songs = source.songs,
                panelTitle = source.albumTitle,
                isSearchMode = false,
                isLoading = false,
                errorMessage = null,
                showEmptyQueryHint = false
            )
            PlayerSidePanelSource.SearchResults -> PlayerSidePanelUiState(
                songs = search.songs,
                panelTitle = null,
                isSearchMode = true,
                isLoading = search.isLoading,
                errorMessage = search.errorMessage,
                showEmptyQueryHint = search.query.isBlank()
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PlayerSidePanelUiState(
            songs = emptyList(),
            panelTitle = null,
            isSearchMode = true,
            isLoading = false,
            errorMessage = null,
            showEmptyQueryHint = true
        )
    )

    val songsSearchEffect: Flow<SongsEffect> = songsSearchRepository.effect

    private val totalSeconds = MOCK_TOTAL_SECONDS
    private val initialProgress = MOCK_INITIAL_ELAPSED_SECONDS.toFloat() / MOCK_TOTAL_SECONDS

    private val _state = MutableStateFlow(
        buildState(
            trackId = savedStateHandle.get<Long>(PlayerNavigation.ARG_TRACK_ID) ?: 0L,
            trackName = savedStateHandle.get<String>(PlayerNavigation.ARG_TRACK_NAME).orEmpty(),
            artistName = savedStateHandle.get<String>(PlayerNavigation.ARG_ARTIST_NAME).orEmpty(),
            collectionId = savedStateHandle.get<Long>(PlayerNavigation.ARG_COLLECTION_ID)
                ?.takeUnless { it == PlayerNavigation.NO_COLLECTION_ID },
            artworkUrl = savedStateHandle.get<String>(PlayerNavigation.ARG_ARTWORK_URL)
                ?.takeIf { it.isNotBlank() },
            progress = initialProgress,
            isPlaying = true,
            repeatEnabled = false
        )
    )
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    fun onSongsSearchIntent(intent: SongsIntent) {
        songsSearchRepository.onIntent(intent)
    }

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
            is PlayerIntent.SongSelectedFromPlaylist -> selectSong(intent.song)
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

    private fun selectSong(song: Song) {
        val progress = MOCK_INITIAL_ELAPSED_SECONDS.toFloat() / MOCK_TOTAL_SECONDS
        val (current, remaining) = PlayerTimeFormatter.labelsForProgress(progress, totalSeconds)
        _state.update {
            it.copy(
                trackId = song.trackId,
                trackName = song.trackName,
                artistName = song.artistName,
                collectionId = song.collectionId,
                artworkUrl = song.artworkUrl100,
                progress = progress,
                currentTimeLabel = current,
                remainingTimeLabel = remaining,
                isPlaying = true
            )
        }
    }

    private fun buildState(
        trackId: Long,
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
            trackId = trackId,
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
