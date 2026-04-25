package com.faithselect.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithselect.domain.model.AppLanguage
import com.faithselect.domain.model.AppTheme
import com.faithselect.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "",
    val isSubscribed: Boolean = false,
    val isTrialActive: Boolean = false,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val theme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val fontSize: Float = 16f
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getSubscriptionStatus: GetSubscriptionStatusUseCase,
    private val refreshSubscription: RefreshSubscriptionUseCase,
    private val getPreferences: GetPreferencesUseCase,
    private val updatePreferences: UpdatePreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getSubscriptionStatus(),
                getPreferences.language(),
                getPreferences.theme(),
                getPreferences.fontSize()
            ) { sub, lang, theme, fontSize ->
                ProfileUiState(
                    isSubscribed = sub.isSubscribed,
                    isTrialActive = sub.isTrialActive,
                    language = lang,
                    theme = theme,
                    fontSize = fontSize
                )
            }.collect { _uiState.value = it }
        }
    }

    fun setLanguage(language: AppLanguage) = viewModelScope.launch {
        updatePreferences.setLanguage(language)
    }

    fun setTheme(theme: AppTheme) = viewModelScope.launch {
        updatePreferences.setTheme(theme)
    }

    fun setFontSize(size: Float) = viewModelScope.launch {
        updatePreferences.setFontSize(size)
    }

    fun restorePurchases() = viewModelScope.launch {
        refreshSubscription()
    }
}
