package com.faithselect.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithselect.domain.model.AudioItem
import com.faithselect.domain.model.Verse
import com.faithselect.domain.repository.FavoritesRepository
import com.faithselect.domain.usecase.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val favoriteVerses: List<Verse> = emptyList(),
    val favoriteAudio: List<AudioItem> = emptyList()
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavorites: GetFavoritesUseCase,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getFavorites.verses(),
                getFavorites.audio()
            ) { verses, audio ->
                FavoritesUiState(favoriteVerses = verses, favoriteAudio = audio)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun removeFavoriteVerse(verseId: String) {
        viewModelScope.launch {
            favoritesRepository.removeVerseFromFavorites(verseId)
        }
    }

    fun removeFavoriteAudio(audioId: String) {
        viewModelScope.launch {
            favoritesRepository.removeAudioFromFavorites(audioId)
        }
    }
}
