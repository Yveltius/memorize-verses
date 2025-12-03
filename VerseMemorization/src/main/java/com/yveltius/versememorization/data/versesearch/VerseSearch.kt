package com.yveltius.versememorization.data.versesearch

import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.versesearch.VerseSearchCategory
import com.yveltius.versememorization.entity.versesearch.VerseSearchResult

class VerseSearch {
    fun getSearchResults(
        query: String,
        category: VerseSearchCategory,
        verses: List<Verse>
    ): List<VerseSearchResult> {
        if (query.isEmpty()) return listOf()

        return when (category) {
            VerseSearchCategory.Book -> getSearchResultsForBook(query, verses)
            VerseSearchCategory.Text -> getSearchResultsForText(query, verses)
            VerseSearchCategory.Tag -> getSearchResultsForTag(query, verses)
        }
    }

    private fun getSearchResultsForTag(
        query: String,
        verses: List<Verse>
    ): List<VerseSearchResult> {
        return verses
            .filter { verse -> verse.tags.any { tag -> tag.contains(query, ignoreCase = true) } }
            .map { VerseSearchResult(it) }
    }

    private fun getSearchResultsForText(
        query: String,
        verses: List<Verse>
    ): List<VerseSearchResult> {
        return verses
            .filter { verse -> verse.verseText.any { it.text.contains(query, ignoreCase = true) } }
            .map { VerseSearchResult(it) }
    }

    private fun getSearchResultsForBook(
        query: String,
        verses: List<Verse>
    ): List<VerseSearchResult> {
        return verses
            .filter { verse -> verse.book.contains(query, ignoreCase = true) }
            .map { VerseSearchResult(verse = it) }
    }
}