package com.faithselect.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithselect.domain.model.AppTheme
import com.faithselect.domain.usecase.GetPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = false,
    val isSubscribed: Boolean = true,
    val isOnboardingComplete: Boolean = true,
    val appTheme: AppTheme = AppTheme.SYSTEM_DEFAULT
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPreferences: GetPreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getPreferences.theme(),
                getPreferences.isOnboardingComplete()
            ) { theme, onboarding ->
                MainUiState(
                    isLoading = false,
                    isSubscribed = true,
                    isOnboardingComplete = onboarding,
                    appTheme = theme
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}