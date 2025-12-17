package com.yveltius.memorize.features.main.viewmodels.verses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.domain.verses.GetVersesUseCase
import com.yveltius.versememorization.domain.verses.RemoveVersesUseCase
import com.yveltius.versememorization.entity.verses.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

class VerseDetailsViewModel: ViewModel() {
    private val getVersesUseCase: GetVersesUseCase by inject(GetVersesUseCase::class.java)
    private val removeVersesUseCase: RemoveVersesUseCase by inject(RemoveVersesUseCase::class.java)

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

    fun onRetryLoadVerse(verseUUID: UUID) {
        getVerse(verseUUID)
    }

    fun deleteVerse(verse: Verse) {
        _uiState.update { UiState.DeletingVerse }
        viewModelScope.launch {
            removeVersesUseCase.removeVerse(verse)
                .onSuccess {
                    _uiState.update { UiState.DeletedVerse }
                }.onFailure {
                    _uiState.update { UiState.Content(verse = verse, failedToDeleteVerse = true)}
                }
        }
    }

    // this can only be called from a Content UiState
    fun resetFailedToDeleteVerse(verse: Verse) {
        _uiState.update {
            UiState.Content(verse = verse)
        }
    }

    sealed class UiState {
        object Loading: UiState()

        object FailedToLoadVerse: UiState()

        object DeletingVerse: UiState()

        object DeletedVerse: UiState()

        data class Content(
            val verse: Verse,
            val failedToDeleteVerse: Boolean = false
        ): UiState()
    }
}