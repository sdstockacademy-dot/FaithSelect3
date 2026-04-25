package com.faithselect.presentation.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithselect.domain.usecase.GetSubscriptionStatusUseCase
import com.faithselect.domain.usecase.RefreshSubscriptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaywallUiState(
    val isLoading: Boolean = false,
    val isSubscribed: Boolean = false,
    val priceString: String = "₹99/month",
    val errorMessage: String? = null
)

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val getSubscriptionStatus: GetSubscriptionStatusUseCase,
    private val refreshSubscription: RefreshSubscriptionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSubscriptionStatus().collect { status ->
                _uiState.update { it.copy(isSubscribed = status.isSubscribed) }
            }
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            refreshSubscription()
        }
    }
}