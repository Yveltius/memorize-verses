package com.yveltius.versememorization.data.search

import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.versesearch.SearchResult
import com.yveltius.versememorization.entity.versesearch.SearchCategory

class VerseSearch {
    fun getSearchResults(
        query: String,
        category: SearchCategory,
        verses: List<Verse>
    ): List<SearchResult.VerseSearchResult> {
        if (query.isEmpty()) return listOf()

        return when (category) {
            SearchCategory.Book -> getSearchResultsForBook(query, verses)
            SearchCategory.Text -> getSearchResultsForText(query, verses)
            SearchCategory.Tag -> getSearchResultsForTag(query, verses)
            else -> throw Throwable("SearchCategory(${category.name}) is not a supported type for VerseSearch.")
        }
    }

    private fun getSearchResultsForTag(
        query: String,
        verses: List<Verse>
    ): List<SearchResult.VerseSearchResult> {
        return verses
            .filter { verse -> verse.tags.any { tag -> tag.contains(query, ignoreCase = true) } }
            .map { SearchResult.VerseSearchResult(it) }
    }

    private fun getSearchResultsForText(
        query: String,
        verses: List<Verse>
    ): List<SearchResult.VerseSearchResult> {
        return verses
            .filter { verse -> verse.verseText.any { it.text.contains(query, ignoreCase = true) } }
            .map { SearchResult.VerseSearchResult(it) }
    }

    private fun getSearchResultsForBook(
        query: String,
        verses: List<Verse>
    ): List<SearchResult.VerseSearchResult> {
        return verses
            .filter { verse -> verse.book.contains(query, ignoreCase = true) }
            .map { SearchResult.VerseSearchResult(verse = it) }
    }
}