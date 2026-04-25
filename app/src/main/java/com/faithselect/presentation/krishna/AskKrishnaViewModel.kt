package com.faithselect.presentation.krishna

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithselect.data.krishna.FreemiumService
import com.faithselect.data.krishna.KrishnaAIService
import com.faithselect.data.krishna.KrishnaResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isUser: Boolean,
    val krishnaResponse: KrishnaResponse? = null
)

data class AskKrishnaUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val questionsUsed: Int = 0,
    val questionsRemaining: Int = FreemiumService.FREE_QUESTION_LIMIT,
    val isPremium: Boolean = false,
    val showPaywall: Boolean = false
)

@HiltViewModel
class AskKrishnaViewModel @Inject constructor(
    private val krishnaAIService: KrishnaAIService,
    private val freemiumService: FreemiumService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AskKrishnaUiState())
    val uiState: StateFlow<AskKrishnaUiState> = _uiState.asStateFlow()

    init {
        // Observe live premium + usage changes
        viewModelScope.launch {
            combine(
                freemiumService.isPremiumFlow,
                freemiumService.questionCountFlow
            ) { isPremium, count ->
                val remaining = if (isPremium) 999
                    else (FreemiumService.FREE_QUESTION_LIMIT - count).coerceAtLeast(0)
                _uiState.update { state ->
                    state.copy(
                        isPremium = isPremium,
                        questionsUsed = count,
                        questionsRemaining = remaining
                    )
                }
            }.collect()
        }
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            // Check limit
            if (!freemiumService.canAskQuestion()) {
                _uiState.update { it.copy(showPaywall = true) }
                return@launch
            }

            // Add user message
            val userMsg = ChatMessage(text = trimmed, isUser = true)
            _uiState.update { state ->
                state.copy(messages = state.messages + userMsg, isLoading = true)
            }

            // Increment count
            freemiumService.incrementQuestionCount()

            // Get response — instant, offline, free
            val response = krishnaAIService.getResponse(trimmed)

            // Add Krishna response
            val krishnaMsg = ChatMessage(
                text = response.gitaInsight,
                isUser = false,
                krishnaResponse = response
            )
            _uiState.update { state ->
                state.copy(
                    messages = state.messages + krishnaMsg,
                    isLoading = false
                )
            }
        }
    }

    fun dismissPaywall() {
        _uiState.update { it.copy(showPaywall = false) }
    }

    fun clearChat() {
        _uiState.update { it.copy(messages = emptyList()) }
    }
}
