package com.faithselect.presentation.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithselect.domain.model.AudioItem
import com.faithselect.domain.usecase.GetAudioItemsUseCase
import com.faithselect.domain.usecase.ToggleAudioFavoriteUseCase
import com.faithselect.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── Audio List ViewModel ─────────────────────────────────────────────────────

data class AudioUiState(
    val audioItems: List<AudioItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val getAudioItems: GetAudioItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AudioUiState())
    val uiState: StateFlow<AudioUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAudioItems.all().collect { items ->
                _uiState.value = AudioUiState(audioItems = items, isLoading = false)
            }
        }
    }
}

// ─── Audio Player ViewModel ───────────────────────────────────────────────────

data class AudioPlayerUiState(
    val audioItem: AudioItem? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isDownloaded: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val toggleFavorite: ToggleAudioFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AudioPlayerUiState())
    val uiState: StateFlow<AudioPlayerUiState> = _uiState.asStateFlow()

    fun loadAudio(audioId: String) {
        viewModelScope.launch {
            // Audio items come from Firestore via getAllAudioItems
            contentRepository.getAllAudioItems()
                .map { items -> items.find { it.id == audioId } }
                .collect { item ->
                    _uiState.update { it.copy(audioItem = item, isLoading = false) }
                    // Auto-play when loaded
                    if (item != null) startPlayback(item.audioUrl)
                }
        }
    }

    private fun startPlayback(audioUrl: String) {
        // In a real app, bind to AudioPlaybackService here
        // For now, update UI state
        _uiState.update { it.copy(isPlaying = true) }
    }

    fun togglePlayPause() {
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }
        // Call service.pause() / service.resume()
    }

    fun skipBack() {
        val newPos = (_uiState.value.currentPositionMs - 10_000L).coerceAtLeast(0L)
        _uiState.update { it.copy(currentPositionMs = newPos) }
        // Call service.seekTo(newPos)
    }

    fun skipForward() {
        val newPos = (_uiState.value.currentPositionMs + 10_000L)
            .coerceAtMost(_uiState.value.durationMs)
        _uiState.update { it.copy(currentPositionMs = newPos) }
        // Call service.seekTo(newPos)
    }

    fun seekTo(fraction: Float) {
        val newPos = (fraction * _uiState.value.durationMs).toLong()
        _uiState.update { it.copy(currentPositionMs = newPos) }
        // Call service.seekTo(newPos)
    }

    fun downloadAudio() {
        viewModelScope.launch {
            // Trigger WorkManager download job
            _uiState.update { it.copy(isDownloaded = true) }
        }
    }
}
