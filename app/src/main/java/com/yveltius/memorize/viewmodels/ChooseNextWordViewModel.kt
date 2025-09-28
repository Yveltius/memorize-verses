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
                    val words = verse.getWordsAndPunctuation().map { singleList ->
                        singleList.map {
                            WordGuessState(
                                string = it,
                                isGuessable = !it.matches(regex = "[.?!,();:…\"\\[\\]]".toRegex()),
                                isGuessed = false
                            )
                        }
                    }

                    //todo probably need an assert for list size of at least one
                    val currentWords = words.firstOrNull() ?: listOf()

                    val availableGuesses = currentWords
                        .filter { wordGuessState -> !wordGuessState.isGuessed && wordGuessState.isGuessable }
                        .distinct()
                        .shuffled()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            words = words,
                            currentWords = currentWords,
                            availableGuesses = availableGuesses,
                            verse = verse,
                        )
                    }
                }.onFailure {
                    // not sure right now
                }
        }
    }

    fun onGuess(word: String) {
        viewModelScope.launch {
            val nextWord = uiState.value.currentWords.first { it.isGuessable && !it.isGuessed }
            val nextWordIndex = uiState.value.currentWords.indexOf(nextWord)
            val currentWordsIndex = uiState.value.words.indexOf(uiState.value.currentWords)
            if (word == nextWord.string) {
                updateCurrentWordsForCorrectGuess(
                    currentWordsListIndex = currentWordsIndex,
                    wordBeingUpdateIndex = nextWordIndex
                )
            } else {
                _uiState.update {
                    it.copy(
                        error = true
                    )
                }
            }
        }
    }

    private fun updateCurrentWordsForCorrectGuess(currentWordsListIndex: Int, wordBeingUpdateIndex: Int) {
        val newCurrentWords = uiState.value.currentWords.mapIndexed { index, state ->
            if (index == wordBeingUpdateIndex) {
                state.copy(
                    isGuessed = true
                )
            } else {
                state
            }
        }

        val newWords = uiState.value.words.mapIndexed { index, states ->
            if (index == currentWordsListIndex) {
                newCurrentWords
            } else {
                states
            }
        }

        val newAvailableGuesses = newCurrentWords.filter { wordGuessState -> !wordGuessState.isGuessed && wordGuessState.isGuessable }.distinct().shuffled()

        _uiState.update {
            it.copy(
                words = newWords,
                currentWords = newCurrentWords,
                availableGuesses = newAvailableGuesses,
                error = false
            )
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = false)
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val words: List<List<WordGuessState>> = listOf(),
        val currentWords: List<WordGuessState> = listOf(),
        val availableGuesses: List<WordGuessState> = listOf(),
        val verse: Verse? = null,
        val error: Boolean = false
    )

    data class WordGuessState(
        val string: String,
        val isGuessable: Boolean,
        val isGuessed: Boolean = false
    )
}