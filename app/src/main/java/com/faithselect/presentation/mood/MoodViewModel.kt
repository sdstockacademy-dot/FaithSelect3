package com.faithselect.presentation.mood

import androidx.lifecycle.ViewModel
import com.faithselect.data.krishna.KrishnaAIService
import com.faithselect.data.krishna.KrishnaResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MoodUiState(
    val selectedMood: MoodOption? = null,
    val krishnaResponse: KrishnaResponse? = null
)

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val krishnaAIService: KrishnaAIService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoodUiState())
    val uiState: StateFlow<MoodUiState> = _uiState.asStateFlow()

    fun selectMood(mood: MoodOption) {
        // Get response for this mood using keyword from enum
        val response = krishnaAIService.getResponse(mood.keyword)
        _uiState.update { it.copy(selectedMood = mood, krishnaResponse = response) }
    }

    fun clearMood() {
        _uiState.update { it.copy(selectedMood = null, krishnaResponse = null) }
    }
}
