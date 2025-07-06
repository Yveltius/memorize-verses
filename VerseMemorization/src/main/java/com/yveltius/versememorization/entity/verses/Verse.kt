package com.yveltius.versememorization.entity.verses

import com.yveltius.versememorization.entity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Verse(
    val book: String,
    val chapter: Int,
    val verseText: List<VerseNumberAndText>,
    val tags: List<String>,
    @Serializable(with = UUIDSerializer::class) val uuid: UUID = UUID.randomUUID()
) {
    fun getVerseString(): String {
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
