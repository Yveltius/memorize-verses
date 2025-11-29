package com.yveltius.memorize.features.verselist.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.domain.verses.GetVersesUseCase
import com.yveltius.versememorization.domain.verses.RemoveVersesUseCase
import com.yveltius.versememorization.entity.verses.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class VersesListViewModel : ViewModel() {
    private val getVersesUseCase: GetVersesUseCase by KoinJavaComponent.inject(GetVersesUseCase::class.java)
    private val removeVersesUseCase: RemoveVersesUseCase by KoinJavaComponent.inject(
        RemoveVersesUseCase::class.java
    )

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(value = UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        getVerses()
    }

    fun getVerses() {
        viewModelScope.launch {
            getVersesUseCase.getVerses()
                .onSuccess { verses ->
                    _uiState.update {
                        it.copy(verses = verses)
                    }
                }
                .onFailure {
                    // todo display fetch error
                }
        }
    }

    fun removeVerse(verse: Verse) {
        viewModelScope.launch {
            removeVersesUseCase.removeVerse(verse)
                .onSuccess {
                    getVerses()
                }
                .onFailure {
                    // todo display removal error
                }
        }
    }

    data class UiState(
        val verses: List<Verse> = listOf()
    )
}