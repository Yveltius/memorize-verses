package com.yveltius.memorize.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.data.choosenextword.ChooseNextWord
import com.yveltius.versememorization.domain.verses.GetVersesUseCase
import com.yveltius.versememorization.entity.verses.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

class ChooseNextWordViewModel: ViewModel() {
    private val getVersesUseCase: GetVersesUseCase by inject(GetVersesUseCase::class.java)
    private val chooseNextWord = ChooseNextWord()

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(value = UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun getVerse(verseUUIDString: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            getVersesUseCase.getVerse(uuid = UUID.fromString(verseUUIDString))
                .onSuccess { verse ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            verse = verse,
                        )
                    }
                }.onFailure {
                    // not sure right now
                }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val words: List<String> = listOf(),
        val unusedWord: List<String> = listOf(),
        val verse: Verse? = null
    )
}