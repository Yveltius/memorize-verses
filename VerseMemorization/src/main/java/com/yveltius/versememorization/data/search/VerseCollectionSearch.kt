package com.yveltius.versememorization.data.search

import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.versesearch.SearchCategory
import com.yveltius.versememorization.entity.versesearch.SearchResult

class VerseCollectionSearch {
    fun getSearchResults(
        query: String,
        category: SearchCategory,
        verseCollections: Set<VerseCollection>
    ): Set<SearchResult.CollectionSearchResult> {
        if (query.isEmpty()) return setOf()

        return when (category) {
            SearchCategory.Collection -> getSearchResultsForCollection(query = query, verseCollections = verseCollections)
            else -> throw Throwable(message = "SearchCategory(${category.name}) is not supported by VerseCollectionSearch.")
        }
    }

    private fun getSearchResultsForCollection(
        query: String,
        verseCollections: Set<VerseCollection>
    ): Set<SearchResult.CollectionSearchResult> {
        return verseCollections
            .filter { (name, _) -> name.contains(query, ignoreCase = true) }
            .map { SearchResult.CollectionSearchResult(verseCollection = it) }
            .toSet()
    }
}