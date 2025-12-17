package com.yveltius.versememorization.entity.versesearch

import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.verses.Verse

sealed class SearchResult {
    data class VerseSearchResult(
        val verse: Verse
    ): SearchResult()

    data class CollectionSearchResult(
        val verseCollection: VerseCollection
    ): SearchResult()
}