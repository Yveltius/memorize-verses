package com.yveltius.versememorization.data.verses

import com.yveltius.versememorization.data.util.JsonFileReader
import com.yveltius.versememorization.data.util.Log
import com.yveltius.versememorization.entity.util.Worker
import com.yveltius.versememorization.entity.util.containsAllIgnoreCase
import com.yveltius.versememorization.entity.util.fromJsonString
import com.yveltius.versememorization.entity.util.toJsonString
import com.yveltius.versememorization.entity.util.toPrettyJsonString
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText

private const val VERSES_FILE_NAME = "verses"

internal class VerseRepositoryImpl(
    log: Log,
    private val jsonFileReader: JsonFileReader
) : VerseRepository, Worker(log) {
    override val logTag: String = "VerseRepositoryImpl"

    override suspend fun getVerse(
        book: String?,
        chapter: Int?,
        verseNumber: Int?,
        partialText: String?,
        tags: List<String>
    ): Result<Verse> {
//        val verses = getVersesFromFile().getOrThrow()

        TODO("Not yet implemented")
    }

    override suspend fun getVerses(
        book: String?,
        chapter: Int?,
        verseNumber: Int?,
        partialText: String?,
        tags: List<String>
    ): Result<List<Verse>> {
        return doWork(
            failureMessage = "Failed to get verses matching the following parameters:\n" +
                    "Book:$book\n" +
                    "Chapter:$chapter\n" +
                    "Verse Number: $verseNumber\n" +
                    "Partial Text:$partialText\n" +
                    "Tags:$tags"
        ) {
            val verses = getVersesFromFile().getOrThrow()

            val filteredVerses = verses
                .filterByBook(book)
                .filterByChapter(chapter)
                .filterByVerseNumber(verseNumber)
                .filterByPartialText(partialText)
                .filterByTags(tags)

            log.debug(
                tag = logTag,
                message = "Successfully filtered verses(\n${filteredVerses.toPrettyJsonString()}\n) given:\n" +
                        "Book:$book\n" +
                        "Chapter:$chapter\n" +
                        "Verse Number: $verseNumber\n" +
                        "Partial Text:$partialText\n" +
                        "Tags:$tags"
            )

            filteredVerses
        }
    }

    override suspend fun addVerse(verse: Verse): Result<Unit> {
        return doWork(
            failureMessage = "Failed to add verse(\n${verse.toPrettyJsonString()}\n)"
        ) {
            val verses = getVersesFromFile().getOrThrow()

            val versesWithAddition = listOf(*verses.toTypedArray(), verse)

            setVersesToFile(versesWithAddition)
        }
    }

    override suspend fun removeVerse(verse: Verse): Result<Unit> {
        return doWork(
            failureMessage = "Failed to remove verse(\n${verse.toPrettyJsonString()}\n)"
        ) {
            val verses = getVersesFromFile().getOrThrow()

            val versesWithRemoval = verses.filterWithoutVerse(verse)

            setVersesToFile(versesWithRemoval)
        }
    }

    private suspend fun getVersesFromFile(): Result<List<Verse>> {
        return doWork(
            failureMessage = "Failed to get verses from File($VERSES_FILE_NAME)."
        ) {
            val versesJsonString =
                jsonFileReader.readFromJsonFile(fileName = VERSES_FILE_NAME).getOrThrow()

            log.debug(
                tag = logTag,
                message = "Successfully retrieved verses($versesJsonString) from File($VERSES_FILE_NAME)."
            )

            versesJsonString.fromJsonString<List<Verse>>()
        }
    }

    private suspend fun setVersesToFile(verses: List<Verse>): Result<Unit> {
        return doWork(
            failureMessage = "Failed to set verses to File($VERSES_FILE_NAME)."
        ) {
            jsonFileReader
                .writeToJsonFile(
                    fileName = VERSES_FILE_NAME,
                    content = verses.toJsonString()
                ).getOrThrow()

            log.debug(
                tag = logTag,
                message = "Successfully saved verses to File($VERSES_FILE_NAME)."
            )
        }
    }

    private fun List<Verse>.filterByBook(book: String?): List<Verse> {
        return this.filter { verse: Verse ->
            book?.let {
                verse.book.lowercase().contains(book.lowercase())
            } ?: true // if book is null, let all through
        }
    }

    private fun List<Verse>.filterByChapter(chapter: Int?): List<Verse> {
        return this.filter { verse: Verse ->
            chapter?.let {
                verse.chapter == chapter
            } ?: true // if chapter is null, let all through
        }
    }

    private fun List<Verse>.filterByVerseNumber(verseNumber: Int?): List<Verse> {
        return this.filter { verse: Verse ->
            verseNumber?.let {
                verse.verseText.any { verseNumberAndText -> verseNumberAndText.verseNumber == verseNumber }
            } ?: true // if chapter is null, let all through
        }
    }

    private fun List<Verse>.filterByTags(tags: List<String>?): List<Verse> {
        return this.filter { verse: Verse ->
            tags?.let {
                verse.tags.containsAllIgnoreCase(tags)
            } ?: true // if tags are null, let all through
        }
    }

    private fun List<Verse>.filterByPartialText(partialText: String?): List<Verse> {
        return this.filter { verse: Verse ->
            partialText?.let {
                verse.verseText.any { verseNumberAndText ->
                    verseNumberAndText.text.contains(
                        partialText,
                        ignoreCase = true
                    )
                }
            } ?: true // if tags are null, let all through
        }
    }

    private fun List<VerseNumberAndText>.matches(other: List<VerseNumberAndText>): Boolean {
        return this.size == other.size
                && this.containsAll(other)
                && other.containsAll(this)
    }

    private fun Verse.matches(other: Verse): Boolean {
        return this.book == other.book &&
                this.chapter == other.chapter &&
                this.tags.containsAllIgnoreCase(other.tags) &&
                this.verseText.matches(other.verseText)
    }

    /**
     * @param filterVerse The [Verse] we do NOT want in the [List]
     * @return [List] of all [Verse]s that do not match the given [Verse]
     */
    private fun List<Verse>.filterWithoutVerse(filterVerse: Verse): List<Verse> {
        return this.filterNot { verse ->
            verse.matches(other = filterVerse)
        }
    }
}