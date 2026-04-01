package com.espezzialy.simpleplayer.presentation.player

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.core.player.PlayerTimeFormatter
import com.espezzialy.simpleplayer.data.session.PlayerSidePanelSession
import com.espezzialy.simpleplayer.data.session.PlayerSidePanelSource
import com.espezzialy.simpleplayer.data.songs.SongsSearchRepository
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.domain.model.SongsEffect
import com.espezzialy.simpleplayer.domain.model.SongsIntent
import com.espezzialy.simpleplayer.presentation.navigation.PlayerNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val songsSearchRepository: SongsSearchRepository,
    private val sidePanelSession: PlayerSidePanelSession,
    @ApplicationContext private val appContext: Context
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
            is PlayerSidePanelSource.RecentSongs -> PlayerSidePanelUiState(
                songs = source.songs,
                panelTitle = appContext.getString(R.string.songs_recent_title),
                isSearchMode = false,
                isLoading = false,
                errorMessage = null,
                showEmptyQueryHint = false
            )
            PlayerSidePanelSource.SearchResults -> PlayerSidePanelUiState(
                songs = search.fullResults.ifEmpty { search.songs },
                panelTitle = null,
                isSearchMode = true,
                isLoading = search.isLoading && search.songs.isEmpty(),
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

    val songsSearchEffect: SharedFlow<SongsEffect> = songsSearchRepository.effect

    private val totalSeconds = MOCK_TOTAL_SECONDS

    private val _state = MutableStateFlow(
        buildState(
            trackId = savedStateHandle.get<Long>(PlayerNavigation.ARG_TRACK_ID) ?: 0L,
            trackName = savedStateHandle.get<String>(PlayerNavigation.ARG_TRACK_NAME).orEmpty(),
            artistName = savedStateHandle.get<String>(PlayerNavigation.ARG_ARTIST_NAME).orEmpty(),
            collectionId = savedStateHandle.get<Long>(PlayerNavigation.ARG_COLLECTION_ID)
                ?.takeUnless { it == PlayerNavigation.NO_COLLECTION_ID },
            artworkUrl = savedStateHandle.get<String>(PlayerNavigation.ARG_ARTWORK_URL)
                ?.takeIf { it.isNotBlank() },
            progress = 0f,
            isPlaying = true,
            repeatEnabled = false
        )
    )

    val state: StateFlow<PlayerUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(MOCK_PROGRESS_TICK_MS)
                val s = _state.value
                if (!s.isPlaying || s.progress >= 1f) continue
                val delta = MOCK_PROGRESS_TICK_MS.toFloat() / 1000f / totalSeconds
                val newP = (s.progress + delta).coerceAtMost(1f)
                if (newP >= 1f) {
                    onPlaybackReachedEnd()
                } else {
                    applyProgress(newP)
                }
            }
        }
    }

    fun onSongsSearchIntent(intent: SongsIntent) {
        songsSearchRepository.onIntent(intent)
    }

    fun onIntent(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.ProgressChanged -> onUserProgressChanged(intent.value)
            PlayerIntent.PlayPauseClicked -> togglePlayPause()
            PlayerIntent.SkipPreviousClicked -> skipInQueue(delta = -1)
            PlayerIntent.SkipNextClicked -> skipInQueue(delta = 1)
            PlayerIntent.RepeatClicked -> {
                _state.update { it.copy(repeatEnabled = !it.repeatEnabled) }
            }
            is PlayerIntent.SongSelectedFromPlaylist -> selectSong(intent.song)
        }
    }

    private fun togglePlayPause() {
        _state.update { it.copy(isPlaying = !it.isPlaying) }
    }

    private fun applyProgress(p: Float) {
        val clamped = p.coerceIn(0f, 1f)
        val (current, remaining) = PlayerTimeFormatter.labelsForProgress(clamped, totalSeconds)
        _state.update {
            it.copy(
                progress = clamped,
                currentTimeLabel = current,
                remainingTimeLabel = remaining
            )
        }
    }

    private fun onUserProgressChanged(value: Float) {
        val p = value.coerceIn(0f, 1f)
        applyProgress(p)
        if (p >= 1f && _state.value.isPlaying) {
            onPlaybackReachedEnd()
        }
    }

    private fun onPlaybackReachedEnd() {
        val beforeId = _state.value.trackId
        skipInQueue(1)
        if (_state.value.trackId == beforeId) {
            applyProgress(1f)
            _state.update { it.copy(isPlaying = false) }
        }
    }

    private fun skipInQueue(delta: Int) {
        val queue = sidePanelUiState.value.songs
        if (queue.isEmpty()) return
        val idx = queue.indexOfFirst { it.trackId == _state.value.trackId }
        if (idx < 0) return
        val repeat = _state.value.repeatEnabled
        val newIdx = when {
            delta > 0 && idx == queue.lastIndex && repeat -> 0
            else -> idx + delta
        }
        if (newIdx !in queue.indices) return
        selectSong(queue[newIdx])
    }

    private fun selectSong(song: Song) {
        val (current, remaining) = PlayerTimeFormatter.labelsForProgress(0f, totalSeconds)
        _state.update {
            it.copy(
                trackId = song.trackId,
                trackName = song.trackName,
                artistName = song.artistName,
                collectionId = song.collectionId,
                artworkUrl = song.artworkUrl100,
                progress = 0f,
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
    ): PlayerUiState {
        val (current, remaining) = PlayerTimeFormatter.labelsForProgress(progress, totalSeconds)
        return PlayerUiState(
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
        const val MOCK_TOTAL_SECONDS = 260
        const val MOCK_PROGRESS_TICK_MS = 250L
    }
}
