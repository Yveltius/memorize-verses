package com.yveltius.memorize.features.main.viewmodels.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.domain.collections.VerseCollectionsUseCase
import com.yveltius.versememorization.domain.verses.GetVersesUseCase
import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.verses.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class VerseCollectionEditViewModel: ViewModel() {
    private val verseCollectionsUseCase: VerseCollectionsUseCase by inject(VerseCollectionsUseCase::class.java)
    private val getVersesUseCase: GetVersesUseCase by inject(GetVersesUseCase::class.java)

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow<UiState>(value = UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private lateinit var allVerses: List<Verse>

    init {
        getVerses()
    }

    private fun getVerses() {
        viewModelScope.launch {
            getVersesUseCase.getVerses()
                .onSuccess { verses -> allVerses = verses.toList() }
                .onFailure { /* Screen won't really work without this... */  }
        }
    }

    fun getVerseCollection(collectionName: String) {
        viewModelScope.launch {
            verseCollectionsUseCase.getCollection(collectionName = collectionName)
                .onSuccess { verseCollection ->
                    _uiState.update {
                        UiState.Content(
                            verseCollection,
                            versesNotInCollection = allVerses.filter { verseBeingFiltered ->
                                    verseCollection.verses.none { verseBeingFiltered.uuid == it.uuid }
                            }
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        UiState.FailedToLoadVerseCollection
                    }
                }
        }
    }

    fun onAddVerseToCollection(collectionName: String, verse: Verse) {
        viewModelScope.launch {
            verseCollectionsUseCase.addVerseToCollection(
                collectionName = collectionName,
                verse = verse
            ).onSuccess {
                getVerseCollection(collectionName)
            }.onFailure {
                // snackbar retry?
            }
        }
    }

    fun onRemoveVerseFromCollection(collectionName: String, verse: Verse) {
        viewModelScope.launch {
            verseCollectionsUseCase.removeVerseFromCollection(
                collectionName = collectionName,
                verse = verse
            ).onSuccess {
                getVerseCollection(collectionName)
            }.onFailure {
                // snackbar retry?
            }
        }
    }

    sealed class UiState {
        object Loading: UiState()
        object FailedToLoadVerseCollection: UiState()
        data class Content(
            val verseCollection: VerseCollection,
            val versesNotInCollection: List<Verse>
        ): UiState()
    }
}