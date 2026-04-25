package com.faithselect.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithselect.domain.model.Chapter
import com.faithselect.domain.model.Religion
import com.faithselect.domain.model.Scripture
import com.faithselect.domain.usecase.GetChaptersUseCase
import com.faithselect.domain.usecase.GetReligionsUseCase
import com.faithselect.domain.usecase.GetScripturesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── Library ViewModel (Religion list) ───────────────────────────────────────

data class LibraryUiState(
    val religions: List<Religion> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getReligions: GetReligionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getReligions().collect { religions ->
                _uiState.value = LibraryUiState(religions = religions, isLoading = false)
            }
        }
    }
}

// ─── Scripture ViewModel ──────────────────────────────────────────────────────

data class ScriptureUiState(
    val scriptures: List<Scripture> = emptyList(),
    val religionName: String = "Scriptures",
    val isLoading: Boolean = true
)

@HiltViewModel
class ScriptureViewModel @Inject constructor(
    private val getScriptures: GetScripturesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScriptureUiState())
    val uiState: StateFlow<ScriptureUiState> = _uiState.asStateFlow()

    fun loadScriptures(religionId: String) {
        viewModelScope.launch {
            getScriptures(religionId).collect { scriptures ->
                _uiState.value = ScriptureUiState(scriptures = scriptures, isLoading = false)
            }
        }
    }
}

// ─── Chapter ViewModel ────────────────────────────────────────────────────────

data class ChapterUiState(
    val chapters: List<Chapter> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val getChapters: GetChaptersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChapterUiState())
    val uiState: StateFlow<ChapterUiState> = _uiState.asStateFlow()

    fun loadChapters(scriptureId: String) {
        viewModelScope.launch {
            getChapters(scriptureId).collect { chapters ->
                _uiState.value = ChapterUiState(chapters = chapters, isLoading = false)
            }
        }
    }
}
