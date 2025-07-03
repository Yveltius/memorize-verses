package com.yveltius.memorize.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.domain.verses.AddVersesUseCase
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class AddVerseViewModel : ViewModel() {
    private val addVersesUseCase: AddVersesUseCase by inject(AddVersesUseCase::class.java)

    private val _uiState = MutableStateFlow(value = UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun addVerse() {
        viewModelScope.launch {
            buildVerse().getOrNull()?.let { verse ->
                _uiState.update {
                    it.copy(isSaving = true)
                }

                addVersesUseCase.addVerse(verse = verse)
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                book = "",
                                chapter = "",
                                verseNumberAndTextList = listOf(AddVerseNumberAndText()),
                                recentlySavedVerse = verse,
                                encounteredSaveError = false
                            )
                        }
                    }.onFailure {
                        _uiState.update {
                            it.copy(isSaving = false)
                        }
                    }
            } ?: _uiState.update {
                it.copy(isSaving = false, encounteredSaveError = true)
            }
        }
    }

    fun onBookChanged(book: String) {
        _uiState.update {
            it.copy(book = book)
        }
    }

    fun onChapterChanged(chapter: String) {
        _uiState.update {
            it.copy(chapter = chapter)
        }
    }

    fun onVerseNumberChanged(index: Int, verseNumber: String) {
        val newVerseNumberAndText = _uiState.value.verseNumberAndTextList[index].copy(
            verseNumber = verseNumber
        )
        val newList = _uiState.value.verseNumberAndTextList.toMutableList()
        newList[index] = newVerseNumberAndText
        _uiState.update {
            it.copy(verseNumberAndTextList = newList.toList())
        }
    }

    fun onVerseTextChanged(index: Int, verseText: String) {
        val newVerseNumberAndText =
            _uiState.value.verseNumberAndTextList[index].copy(verseText = verseText)

        val newList = _uiState.value.verseNumberAndTextList.toMutableList()
        newList[index] = newVerseNumberAndText
        _uiState.update {
            it.copy(verseNumberAndTextList = newList.toList())
        }
    }

    fun onAddVerseNumberAndText() {
        _uiState.update {
            it.copy(verseNumberAndTextList = it.verseNumberAndTextList + AddVerseNumberAndText())
        }
    }

    fun onDeleteVerseNumberAndText(index: Int) {
        _uiState.update {
            it.copy(
                verseNumberAndTextList = it
                    .verseNumberAndTextList
                    .filterIndexed { filterIndex, _ -> filterIndex != index }
            )
        }
    }

    fun onDeleteLastVerseNumberAndText() {
        _uiState.update {
            it.copy(
                verseNumberAndTextList = it
                    .verseNumberAndTextList
                    .dropLast(1)
            )
        }
    }

    data class UiState(
        val isSaving: Boolean = false,
        val book: String = "",
        val chapter: String = "",
        val verseNumberAndTextList: List<AddVerseNumberAndText> = listOf(AddVerseNumberAndText()),
        val recentlySavedVerse: Verse? = null,
        val encounteredSaveError: Boolean = false
    )

    private fun buildVerse(): Result<Verse> {
        val chapterErrorFound = uiState.value.chapter.toIntOrNull() == null
        val foundErrorInVerseNumbers = uiState
            .value
            .verseNumberAndTextList
            .any { it.verseNumber.toIntOrNull() == null }

        if (chapterErrorFound || foundErrorInVerseNumbers)
            return Result.failure(Throwable("Failed to convert chapter or verse number to int. Check your inputs."))

        return Result.success(
            Verse(
                book = uiState.value.book,
                chapter = uiState.value.chapter.toInt(),
                verseText = uiState.value.verseNumberAndTextList.map { it.transform() },
                tags = listOf()
            )
        )
    }

    fun resetEncounteredSaveError() {
        _uiState.update {
            it.copy(encounteredSaveError = false)
        }
    }

    data class AddVerseNumberAndText(
        var verseNumber: String = "",
        var verseText: String = ""
    ) {
        fun transform(): VerseNumberAndText {
            return VerseNumberAndText(verseNumber.toIntOrNull() ?: 0, verseText)
        }
    }
}