package com.yveltius.versememorization.data.verses

import com.yveltius.versememorization.entity.verses.Verse
import java.util.UUID

internal interface VerseRepository {
    /**
     * @param book The book of the Bible containing the verse. If the [book] is
     * null, it will be ignored.
     * @param chapter The chapter the verse is in. If the [chapter] is null, it will be ignored.
     * @param verseNumber The verse number. If the [verseNumber] is null, it will be ignored.
     * @param partialText Any text can be checked for and return any verses matching the
     * prior qualifiers and this. If [partialText] is null, it will be ignored.
     * @param tags List of string tags defined by the user. Effectively ignored if empty.
     *
     * @return The [Verse] most closely resembling the given parameters.
     */
    suspend fun getVerse(
        book: String? = null,
        chapter: Int? = null,
        verseNumber: Int? = null,
        partialText: String? = null,
        tags: List<String> = listOf(),
        uuid: UUID? = null
    ): Result<Verse>

    /**
     * @param book If null, it will be ignored.
     * @param chapter If null, it will be ignored.
     * @param verseNumber If null, it will be ignored.
     * @param partialText If null, it will be ignored.
     * @param tags List of string tags defined by the user. Effectively ignored if empty.
     *
     * @return A [List] of [Verse]s matching the parameters.
     */
    suspend fun getVerses(
        book: String? = null,
        chapter: Int? = null,
        verseNumber: Int? = null,
        partialText: String? = null,
        tags: List<String> = listOf()
    ): Result<Set<Verse>>

    @Throws(Throwable::class)
    suspend fun addVerse(verse: Verse): Result<Unit>

    suspend fun removeVerse(verse: Verse): Result<Unit>

    /**
     * @param updatedVerse The verse in its edited state to be saved
     */
    suspend fun updateVerse(updatedVerse: Verse): Result<Unit>
}