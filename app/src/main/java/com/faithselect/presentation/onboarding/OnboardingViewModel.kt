package com.faithselect.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithselect.domain.usecase.UpdatePreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val updatePreferences: UpdatePreferencesUseCase
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            updatePreferences.completeOnboarding()
        }
    }
}
