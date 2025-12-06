package com.yveltius.memorize.features.verselist.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.data.versesearch.VerseSearch
import com.yveltius.versememorization.domain.collections.VerseCollectionsUseCase
import com.yveltius.versememorization.entity.versesearch.VerseSearchCategory
import com.yveltius.versememorization.entity.versesearch.VerseSearchResult
import com.yveltius.versememorization.domain.verses.GetVersesUseCase
import com.yveltius.versememorization.domain.verses.RemoveVersesUseCase
import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.verses.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class VersesListViewModel : ViewModel() {
    private val getVersesUseCase: GetVersesUseCase by inject(GetVersesUseCase::class.java)
    private val removeVersesUseCase: RemoveVersesUseCase by inject(
        RemoveVersesUseCase::class.java
    )
    private val verseCollectionsUseCase: VerseCollectionsUseCase by inject(VerseCollectionsUseCase::class.java)

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(value = UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun getVerses() {
        viewModelScope.launch {
            getVersesUseCase.getVerses()
                .onSuccess { verses ->
                    _uiState.update {
                        it.copy(verses = verses.toList())
                    }
                }
                .onFailure {
                    // todo display fetch error
                }
        }
    }

    fun getCollections() {
        viewModelScope.launch {
            verseCollectionsUseCase.getAllCollections()
                .onSuccess { collections ->
                    _uiState.update {
                        it.copy(collections = collections.toList())
                    }
                }
                .onFailure { throwable ->  }
        }
    }

    fun removeVerse(verse: Verse) {
        viewModelScope.launch {
            removeVersesUseCase.removeVerse(verse)
                .onSuccess { getVerses() }
                .onFailure {
                    // todo display removal error
                }
        }
    }

    fun onAddCollection(newCollectionName: String) {
        viewModelScope.launch {
            verseCollectionsUseCase.addCollection(newCollectionName)
                .onSuccess {
                    getCollections()
                }.onFailure { throwable ->  }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update {
            it.copy(query = newQuery)
        }

        viewModelScope.launch { generateSearchResults(newQuery) }
    }

    private fun generateSearchResults(query: String) {
        val verseSearch = VerseSearch()
        val map = buildMap(capacity = VerseSearchCategory.entries.size) {
            VerseSearchCategory.entries.forEach { category ->
                this.put(
                    category,
                    verseSearch.getSearchResults(query, category, verses = uiState.value.verses)
                )
            }
        }

        _uiState.update {
            it.copy(
                searchResults = map
            )
        }
    }

    data class UiState(
        val verses: List<Verse> = emptyList(),
        val collections: List<VerseCollection> = emptyList(),
        val query: String = "",
        val searchResults: Map<VerseSearchCategory, List<VerseSearchResult>> = VerseSearchCategory.entries.associateWith { emptyList() }
    )
}