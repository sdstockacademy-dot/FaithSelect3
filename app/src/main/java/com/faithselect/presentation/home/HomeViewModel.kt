package com.faithselect.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithselect.data.krishna.KrishnaAIService
import com.faithselect.domain.model.DailyContent
import com.faithselect.domain.model.Religion
import com.faithselect.domain.usecase.GetPreferencesUseCase
import com.faithselect.domain.usecase.GetReligionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val greeting: String = "🙏 Welcome",
    val dailyContent: com.faithselect.data.krishna.DailyGuidance? = null,
    val religions: List<Religion> = emptyList(),
    val featuredAudioTitles: List<String> = listOf(
        "Hanuman Chalisa",
        "Shri Ramcharitmanas",
        "Bhagwat Katha",
        "Sunderkand Path"
    ),
    val language: String = "en",
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getReligions: GetReligionsUseCase,
    private val krishnaAIService: KrishnaAIService,
    private val getPreferences: GetPreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(greeting = getGreeting()))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // Load religions safely - catch any Firestore errors
        viewModelScope.launch {
            try {
                getReligions().catch { e ->
                    // Firestore error - just show empty list, don't crash
                    emit(emptyList())
                }.collect { religions ->
                    _uiState.update { it.copy(religions = religions) }
                }
            } catch (e: Exception) {
                // Silent fail - app continues working
            }
        }

        // Load daily guidance - works offline, no crash possible
        viewModelScope.launch {
            try {
                val daily = krishnaAIService.getDailyGuidance()
                _uiState.update { it.copy(dailyContent = daily) }
            } catch (e: Exception) {
                // Silent fail
            }
        }

        // Load language preference
        viewModelScope.launch {
            try {
                getPreferences.language().collect { language ->
                    _uiState.update { it.copy(language = language.code) }
                }
            } catch (e: Exception) {
                // Silent fail
            }
        }
    }

    private fun getGreeting(): String {
        return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 4..11  -> "🌅 Good Morning"
            in 12..16 -> "☀️ Good Afternoon"
            in 17..20 -> "🌇 Good Evening"
            else      -> "🌙 Good Night"
        }
    }
}