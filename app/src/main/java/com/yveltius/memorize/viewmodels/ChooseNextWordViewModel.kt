package com.yveltius.memorize.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

                    val availableGuesses = currentWords.getAvailableGuesses()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            allWordsStates = words,
                            currentWordsStates = currentWords,
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
            val nextWord = uiState.value.currentWordsStates.first { it.isGuessable && !it.isGuessed }
            val nextWordIndex = uiState.value.currentWordsStates.indexOf(nextWord)
            val currentWordsIndex = uiState.value.allWordsStates.indexOf(uiState.value.currentWordsStates)
            val currentGuessCount = uiState.value.currentGuessCount + 1

            if (word == nextWord.string) {
                updateCurrentWordsForCorrectGuess(
                    currentWordsListIndex = currentWordsIndex,
                    wordBeingUpdateIndex = nextWordIndex
                )
            } else {
                _uiState.update {
                    it.copy(
                        currentGuessCount = currentGuessCount,
                        lastGuessIncorrect = true
                    )
                }
            }
        }
    }

    private fun updateCurrentWordsForCorrectGuess(currentWordsListIndex: Int, wordBeingUpdateIndex: Int) {
        val newCurrentWords = uiState.value.currentWordsStates.mapIndexed { index, state ->
            if (index == wordBeingUpdateIndex) {
                state.copy(
                    isGuessed = true
                )
            } else {
                state
            }
        }

        // updates the current words index with newCurrentWords
        val newWords = uiState.value.allWordsStates.mapIndexed { index, states ->
            if (index == currentWordsListIndex) {
                newCurrentWords
            } else {
                states
            }
        }

        val newAvailableGuesses = newCurrentWords
            .filter { wordGuessState -> !wordGuessState.isGuessed && wordGuessState.isGuessable }
            .distinct()
            .take(8)
            .shuffled()

        val currentGuessIndex = if ((wordBeingUpdateIndex + 1) >= uiState.value.allWordsStates[currentWordsListIndex].size) {
            -1
        } else {
            wordBeingUpdateIndex + 1
        }

        val currentGuessCount = uiState.value.currentGuessCount + 1

        val showNextButton = newCurrentWords.all { it.isGuessed || !it.isGuessable }
                && (currentWordsListIndex < uiState.value.allWordsStates.size - 1)

        val showFinishButton = newCurrentWords.all { it.isGuessed || !it.isGuessable }
                && currentWordsListIndex >= (uiState.value.allWordsStates.size - 1)

        _uiState.update {
            it.copy(
                allWordsStates = newWords,
                currentWordsStates = newCurrentWords,
                currentGuessCount = currentGuessCount,
                availableGuesses = newAvailableGuesses,
                currentGuessIndex = currentGuessIndex,
                lastGuessIncorrect = false,
                showNextButton = showNextButton,
                showFinishButton = showFinishButton
            )
        }
    }

    fun goNext() {
        val nextWords = uiState.value.let {
            it.allWordsStates[it.allWordsStates.indexOf(it.currentWordsStates) + 1]
        }

        val availableGuesses = nextWords.getAvailableGuesses()

        _uiState.update {
            it.copy(
                allWordsStates = it.allWordsStates,
                currentWordsStates = nextWords,
                availableGuesses = availableGuesses,
                guessCounts = it.guessCounts + it.currentGuessCount,
                currentGuessCount = 0,
                showNextButton = false,
                showFinishButton = false
            )
        }
    }

    fun onComplete() {
        //todo need to add something for when the user completes this section
    }

    fun List<WordGuessState>.getAvailableGuesses(): List<WordGuessState> {
        return this
            .filter { wordGuessState -> !wordGuessState.isGuessed && wordGuessState.isGuessable }
            .distinct()
            .take(8)
            .shuffled()
    }

    data class UiState(
        val isLoading: Boolean = false,
        val allWordsStates: List<List<WordGuessState>> = listOf(),
        val guessCounts: List<Int> = listOf(),
        val currentWordsStates: List<WordGuessState> = listOf(),
        val currentGuessCount: Int = 0,
        val availableGuesses: List<WordGuessState> = listOf(),
        val currentGuessIndex: Int = 0,
        val verse: Verse? = null,
        val lastGuessIncorrect: Boolean = false,
        val showNextButton: Boolean = false,
        val showFinishButton: Boolean = false
    ) {
        val currentVerse: String?
            get() {
                return this.verse?.getVerseString(
                    index = this.allWordsStates.indexOf(
                        this.currentWordsStates
                    )
                )
            }
    }

    data class WordGuessState(
        val string: String,
        val isGuessable: Boolean,
        val isGuessed: Boolean = false
    )
}