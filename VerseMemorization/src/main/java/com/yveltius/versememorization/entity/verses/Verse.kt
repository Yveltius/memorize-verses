package com.yveltius.versememorization.entity.verses

import kotlinx.serialization.Serializable

@Serializable
data class Verse(
    val book: String,
    val chapter: Int,
    val verseText: List<VerseNumberAndText>,
    val tags: List<String>
) {
    fun getVerseNumberString(): String {
        assert(verseText.isNotEmpty()) {
            "No Verse object should have an empty list of verseText(s)"
        }
        return when {
            verseText.size == 1 -> "$book $chapter:${verseText.first().verseNumber}"
            verseText.size > 1 -> "$book $chapter:${verseText.first().verseNumber}-${verseText.last().verseNumber}"
            else -> {
                throw IllegalArgumentException("verseText list should not be empty.")
            }
        }
    }
}
