package com.yveltius.memorize.features.main.viewmodels.verses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.domain.verses.GetVersesUseCase
import com.yveltius.versememorization.entity.verses.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

class VerseDetailsViewModel: ViewModel() {
    private val getVersesUseCase: GetVersesUseCase by inject(GetVersesUseCase::class.java)

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(value = UiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getVerse(verseUUID: UUID) {
        _uiState.update { UiState.Loading }

        viewModelScope.launch {
            getVersesUseCase.getVerse(uuid = verseUUID)
                .onSuccess { verse ->
                    _uiState.update {
                        UiState.Content(verse)
                    }
                }.onFailure {
                    _uiState.update { UiState.FailedToLoadVerse }
                }
        }
    }

    fun onRetry(verseUUID: UUID) {
        getVerse(verseUUID)
    }

    sealed class UiState {
        object Loading: UiState()
        object FailedToLoadVerse: UiState()
        data class Content(val verse: Verse): UiState()
    }
}