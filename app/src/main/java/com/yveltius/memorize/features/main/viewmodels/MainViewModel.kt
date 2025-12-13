package com.yveltius.memorize.features.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.data.search.VerseCollectionSearch
import com.yveltius.versememorization.data.search.VerseSearch
import com.yveltius.versememorization.domain.collections.VerseCollectionsUseCase
import com.yveltius.versememorization.domain.verses.GetVersesUseCase
import com.yveltius.versememorization.domain.verses.RemoveVersesUseCase
import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.versesearch.SearchCategory
import com.yveltius.versememorization.entity.versesearch.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel() {
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
                .onFailure { throwable -> }
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
                }.onFailure { throwable -> }
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
        val verseCollectionSearch = VerseCollectionSearch()
        val map = buildMap(capacity = SearchCategory.entries.size) {
            SearchCategory.collectionEntries.forEach { category ->
                this.put(
                    category,
                    verseCollectionSearch.getSearchResults(
                        query,
                        category,
                        verseCollections = uiState.value.collections.toSet()
                    ).toList()
                )
            }

            SearchCategory.verseEntries.forEach { category ->
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
        val searchResults: Map<SearchCategory, List<SearchResult>> = SearchCategory.entries.associateWith { emptyList() }
    )
}