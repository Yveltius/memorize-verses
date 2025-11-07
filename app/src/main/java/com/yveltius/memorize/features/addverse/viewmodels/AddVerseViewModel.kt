package com.yveltius.memorize.features.addverse.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.domain.verses.AddVersesUseCase
import com.yveltius.versememorization.domain.verses.GetAllTagsUseCase
import com.yveltius.versememorization.domain.verses.GetVersesUseCase
import com.yveltius.versememorization.domain.verses.UpdateVerseUseCase
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import java.util.UUID

class AddVerseViewModel : ViewModel() {
    private val getVersesUseCase: GetVersesUseCase by KoinJavaComponent.inject(GetVersesUseCase::class.java)
    private val addVersesUseCase: AddVersesUseCase by KoinJavaComponent.inject(AddVersesUseCase::class.java)
    private val updateVerseUseCase: UpdateVerseUseCase by KoinJavaComponent.inject(
        UpdateVerseUseCase::class.java
    )
    private val getAllTagsUseCase: GetAllTagsUseCase by KoinJavaComponent.inject(GetAllTagsUseCase::class.java)

    private val _uiState = MutableStateFlow(value = UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun getAllTags() {
        viewModelScope.launch {
            getAllTagsUseCase.getAllTags()
                .onSuccess { tags ->
                   _uiState.update {
                       it.copy(
                           allTags = tags
                       )
                   }
                }
        }
    }

    fun getVerseBeingEdited(uuid: UUID) {
        viewModelScope.launch {
            getVersesUseCase.getVerse(uuid = uuid)
                .onSuccess { verse ->
                    _uiState.update {
                        it.copy(
                            book = verse.book,
                            chapter = verse.chapter.toString(),
                            verseNumberAndTextList = verse.verseText.toVerseNumberAndTextList(),
                            verseBeingEdited = verse,
                            tags = verse.tags
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            failedToLoadVerseForEdit = true
                        )
                    }
                }
        }
    }

    fun updateVerse() {
        viewModelScope.launch {
            buildVerse().getOrNull()?.let { verse ->
                _uiState.update {
                    it.copy(isSaving = true)
                }

                updateVerseUseCase.updateVerse(updatedVerse = verse)
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                recentlySavedVerse = verse,
                                encounteredSaveError = false
                            )
                        }
                    }.onFailure {
                        _uiState.update {
                            it.copy(isSaving = false, encounteredSaveError = true)
                        }
                    }
            } ?: _uiState.update {
                it.copy(isSaving = false, encounteredSaveError = true)
            }
        }
    }

    fun addVerse() {
        viewModelScope.launch {
            when (uiState.value.verseBeingEdited) {
                null -> {
                    // no verse is being edited so this is a plain add
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
                                        tags = listOf(),
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

                else -> {
                    updateVerse()
                }
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

    fun onAddTag(tag: String) {
        if (!uiState.value.tags.any { it.equals(tag, ignoreCase = false) }) {
            _uiState.update {
                it.copy(tags = it.tags + tag)
            }
        }
    }

    fun onRemoveTag(tagToRemove: String) {
        _uiState.update {
            it.copy(tags = it.tags.filter { tag -> tag != tagToRemove })
        }
    }

    fun getSnapshotOfVerse(): Verse? {
        val snapshotVerse = buildVerse().getOrNull()

        return snapshotVerse
    }

    data class UiState(
        val isSaving: Boolean = false,
        val verseBeingEdited: Verse? = null,
        val book: String = "",
        val chapter: String = "",
        val verseNumberAndTextList: List<AddVerseNumberAndText> = listOf(AddVerseNumberAndText()),
        val tags: List<String> = listOf(),
        val allTags: List<String> = listOf(),
        val recentlySavedVerse: Verse? = null,
        val encounteredSaveError: Boolean = false,
        val failedToLoadVerseForEdit: Boolean = false
    )

    private fun buildVerse(): Result<Verse> {
        val chapterErrorFound = uiState.value.chapter.toIntOrNull() == null
        val foundErrorInVerseNumbers = uiState
            .value
            .verseNumberAndTextList
            .any { it.verseNumber.toIntOrNull() == null }
        val foundErrorInVerseText = uiState
            .value
            .verseNumberAndTextList
            .any { it.verseText.isEmpty() }
        val bookErrorFound = uiState.value.book.isEmpty()

        if (chapterErrorFound || foundErrorInVerseNumbers || foundErrorInVerseText || bookErrorFound)
            return Result.failure(Throwable("Failed to convert chapter or verse number to int. Check your inputs."))

        return Result.success(
            Verse(
                book = uiState.value.book,
                chapter = uiState.value.chapter.toInt(),
                verseText = uiState.value.verseNumberAndTextList.map { it.transform() },
                tags = uiState.value.tags,
                uuid = uiState.value.verseBeingEdited?.uuid ?: UUID.randomUUID()
            )
        )
    }

    fun resetEncounteredSaveError() {
        _uiState.update {
            it.copy(encounteredSaveError = false)
        }
    }

    fun resetFailedToLoadVerseForEdit() {
        _uiState.update {
            it.copy(failedToLoadVerseForEdit = false)
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

    private fun List<VerseNumberAndText>.toVerseNumberAndTextList(): List<AddVerseNumberAndText> {
        return this.map { (verseNumber, text) ->
            AddVerseNumberAndText(
                verseNumber = verseNumber.toString(),
                verseText = text
            )
        }
    }
}