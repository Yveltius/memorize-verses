package com.yveltius.memorize.features.main.viewmodels.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.domain.collections.VerseCollectionsUseCase
import com.yveltius.versememorization.entity.collections.VerseCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class VerseCollectionDetailsViewModel : ViewModel() {
    private val verseCollectionsUseCase: VerseCollectionsUseCase by inject(VerseCollectionsUseCase::class.java)

    private val _uiState = MutableStateFlow<UiState>(value = UiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getCollection(collectionName: String) {
        viewModelScope.launch {
            verseCollectionsUseCase.getCollection(collectionName)
                .onSuccess { collection ->
                    _uiState.update {
                        UiState.Content(verseCollection = collection)
                    }
                }.onFailure {
                    _uiState.update {
                        UiState.FailedToLoadVerseCollection
                    }
                }
        }
    }

    fun onDeleteCollection(verseCollection: VerseCollection) {
        _uiState.update { UiState.Loading }

        viewModelScope.launch {
            verseCollectionsUseCase.deleteCollection(verseCollection)
                .onSuccess {
                    _uiState.update { UiState.CollectionDeleted }
                }.onFailure {
                    _uiState.update {
                        UiState.Content(
                            verseCollection = verseCollection,
                            failedToDeleteVerseCollection = true
                        )
                    }
                }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        object FailedToLoadVerseCollection : UiState()
        data class Content(
            val verseCollection: VerseCollection,
            val failedToDeleteVerseCollection: Boolean = false
        ) : UiState()

        object CollectionDeleted : UiState()
    }
}