package com.yveltius.memorize.features.main.viewmodels.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.domain.collections.VerseCollectionsUseCase
import com.yveltius.versememorization.domain.verses.GetVersesUseCase
import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.verses.Verse
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class VerseCollectionEditViewModel : ViewModel() {
    private val verseCollectionsUseCase: VerseCollectionsUseCase by inject(VerseCollectionsUseCase::class.java)
    private val getVersesUseCase: GetVersesUseCase by inject(GetVersesUseCase::class.java)

    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow<UiState>(value = UiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadContent(collectionName: String) {
        viewModelScope.launch {
            val versesDeferred = async { getVersesUseCase.getVerses() }
            val verseCollectionDeferred = async {
                verseCollectionsUseCase.getCollection(collectionName = collectionName)
            }

            val versesResult = versesDeferred.await()
            val verseCollectionResult = verseCollectionDeferred.await()

            if (versesResult.isFailure || verseCollectionResult.isFailure) {
                _uiState.update { UiState.FailedToLoadVerseCollection }
                return@launch
            }

            val verses = versesResult.getOrThrow()
            val verseCollection = verseCollectionResult.getOrThrow()

            if (verses.isEmpty()) {
                _uiState.update { UiState.NoVersesAvailable }
                return@launch
            }

            _uiState.update {
                UiState.Content(
                    verseCollection = verseCollection,
                    versesNotInCollection = verses.filter { verseBeingFiltered ->
                        verseCollection.verses.none { verseBeingFiltered.uuid == it.uuid }
                    }
                )
            }
        }
    }

    fun onRetry(collectionName: String) {
        loadContent(collectionName)
    }

    fun onAddVerseToCollection(collectionName: String, verse: Verse) {
        viewModelScope.launch {
            verseCollectionsUseCase.addVerseToCollection(
                collectionName = collectionName,
                verse = verse
            ).onSuccess {
                loadContent(collectionName)
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
                loadContent(collectionName)
            }.onFailure {
                // snackbar retry?
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()

        object FailedToLoadVerseCollection : UiState()

        object NoVersesAvailable : UiState()

        data class Content(
            val verseCollection: VerseCollection,
            val versesNotInCollection: List<Verse>
        ) : UiState()
    }
}