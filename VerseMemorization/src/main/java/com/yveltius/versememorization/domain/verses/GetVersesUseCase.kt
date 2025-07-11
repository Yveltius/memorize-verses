package com.yveltius.versememorization.domain.verses

import com.yveltius.versememorization.data.verses.VerseRepository
import com.yveltius.versememorization.entity.verses.Verse
import java.util.UUID

class GetVersesUseCase internal constructor(
    private val verseRepository: VerseRepository
) {
    suspend fun getVerse(
        book: String? = null,
        chapter: Int? = null,
        verseNumber: Int? = null,
        partialText: String? = null,
        tags: List<String> = listOf(),
        uuid: UUID? = null
    ): Result<Verse> = verseRepository.getVerse(book, chapter, verseNumber, partialText, tags, uuid)

    suspend fun getVerses(
        book: String? = null,
        chapter: Int? = null,
        verseNumber: Int? = null,
        partialText: String? = null,
        tags: List<String> = listOf()
    ): Result<List<Verse>> =
        verseRepository.getVerses(book, chapter, verseNumber, partialText, tags)
}