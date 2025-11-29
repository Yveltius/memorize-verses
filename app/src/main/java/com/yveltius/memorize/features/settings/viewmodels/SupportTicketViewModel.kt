package com.yveltius.memorize.features.settings.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SupportTicketViewModel: ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(value = UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onSubjectChanged(newSubject: String) {
        _uiState.update {
            it.copy(subject = newSubject)
        }
    }

    fun onBodyChanged(newBody: String) {
        _uiState.update {
            it.copy(body = newBody)
        }
    }

    fun onNoActivityFound() {
        _uiState.update {
            it.copy(
                hasNoActivityError = true
            )
        }
    }

    fun onUnknownError() {
        _uiState.update {
            it.copy(
                hasUnknownError = true
            )
        }
    }

    fun clearErrors() {
        _uiState.update {
            it.copy(
                hasUnknownError = false,
                hasNoActivityError = false
            )
        }
    }

    data class UiState(
        val subject: String = "",
        val body: String = "",
        val hasNoActivityError: Boolean = false,
        val hasUnknownError: Boolean = false
    )
}