package com.yveltius.versememorization.entity.versesearch

// sections that search will be applied to
enum class SearchCategory {
    Collection,
    Book,
    Text,
    Tag;

    companion object {
        val verseEntries: List<SearchCategory> = listOf(Book, Text, Tag)
        val collectionEntries: List<SearchCategory> = listOf(Collection)
    }
}