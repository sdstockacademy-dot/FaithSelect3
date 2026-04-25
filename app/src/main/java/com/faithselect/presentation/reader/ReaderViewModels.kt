package com.faithselect.presentation.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithselect.domain.model.AppLanguage
import com.faithselect.domain.model.Verse
import com.faithselect.domain.repository.ContentRepository
import com.faithselect.domain.usecase.GetPreferencesUseCase
import com.faithselect.domain.usecase.GetVersesUseCase
import com.faithselect.domain.usecase.ToggleVerseFavoriteUseCase
import com.faithselect.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── Verse List ViewModel ─────────────────────────────────────────────────────

data class VerseListUiState(
    val verses: List<Verse> = emptyList(),
    val language: AppLanguage = AppLanguage.ENGLISH,
    val isLoading: Boolean = true
)

@HiltViewModel
class VerseListViewModel @Inject constructor(
    private val getVerses: GetVersesUseCase,
    private val getPreferences: GetPreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerseListUiState())
    val uiState: StateFlow<VerseListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getPreferences.language().collect { language ->
                _uiState.update { it.copy(language = language) }
            }
        }
    }

    fun loadVerses(chapterId: String) {
        viewModelScope.launch {
            getVerses(chapterId).collect { verses ->
                _uiState.update { it.copy(verses = verses, isLoading = false) }
            }
        }
    }
}

// ─── Verse Reader ViewModel ───────────────────────────────────────────────────

data class VerseReaderUiState(
    val verse: Verse? = null,
    val isFavorited: Boolean = false,
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    val fontSize: Float = 16f,
    val isAudioPlaying: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class VerseReaderViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val favoritesRepository: FavoritesRepository,
    private val toggleFavorite: ToggleVerseFavoriteUseCase,
    private val getPreferences: GetPreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerseReaderUiState())
    val uiState: StateFlow<VerseReaderUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getPreferences.language().collect { language ->
                _uiState.update { it.copy(selectedLanguage = language) }
            }
        }
        viewModelScope.launch {
            getPreferences.fontSize().collect { size ->
                _uiState.update { it.copy(fontSize = size) }
            }
        }
    }

    fun loadVerse(verseId: String) {
        viewModelScope.launch {
            val verse = contentRepository.getVerseById(verseId)
            val isFav = verse?.let { favoritesRepository.isVerseFavorited(it.id) } ?: false
            _uiState.update { it.copy(verse = verse, isFavorited = isFav, isLoading = false) }
        }
    }

    fun toggleFavorite() {
        val verse = _uiState.value.verse ?: return
        viewModelScope.launch {
            toggleFavorite(verse, _uiState.value.isFavorited)
            _uiState.update { it.copy(isFavorited = !it.isFavorited) }
        }
    }

    fun selectLanguage(language: AppLanguage) {
        _uiState.update { it.copy(selectedLanguage = language) }
    }

    fun increaseFontSize() {
        val newSize = (_uiState.value.fontSize + 2f).coerceAtMost(28f)
        _uiState.update { it.copy(fontSize = newSize) }
    }

    fun decreaseFontSize() {
        val newSize = (_uiState.value.fontSize - 2f).coerceAtLeast(12f)
        _uiState.update { it.copy(fontSize = newSize) }
    }

    fun toggleAudio(audioUrl: String) {
        _uiState.update { it.copy(isAudioPlaying = !it.isAudioPlaying) }
        // AudioPlaybackService handles actual playback — just toggle state here
    }
}
