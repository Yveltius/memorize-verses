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
import java.util.UUID

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
        tags: List<String>,
        uuid: UUID?
    ): Result<Verse> {
        return doWork(
            failureMessage = "Failed to get a verse matching the following parameters:\n" +
                    "Book: $book\n" +
                    "Chapter: $chapter\n" +
                    "Verse Number: $verseNumber\n" +
                    "Partial Text: $partialText\n" +
                    "Tags: $tags\n" +
                    "UUID: $uuid"
        ) {
            val verses = getVerses(book, chapter, verseNumber, partialText, tags).getOrThrow()

            val verse = verses.filterByUUID(uuid = uuid).first()

            log.debug(
                tag = logTag,
                message = "Successfully found a verse matching the given parameters: ${verse.getVerseString()}"
            )

            verse
        }
    }

    override suspend fun getVerses(
        book: String?,
        chapter: Int?,
        verseNumber: Int?,
        partialText: String?,
        tags: List<String>
    ): Result<Set<Verse>> {
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
                .asSequence()
                .filterByBook(book)
                .filterByChapter(chapter)
                .filterByVerseNumber(verseNumber)
                .filterByPartialText(partialText)
                .filterByTags(tags)
                .toSet()

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
            assert(verse.verseText.isNotEmpty()) {
                "verseText list should not be empty when trying to add a verse."
            }

            assert(verse.book.isNotEmpty()) {
                "book should not be empty when trying to add a verse."
            }

            assert(verse.chapter > 0) {
                "chapter should be greater than zero."
            }

            assert(verse.verseText.all { verseNumberAndText -> verseNumberAndText.verseNumber > 0 }) {
                "All VerseTextAndNumber objects should have verseNumbers greater than zero."
            }

            assert(verse.verseText.all { verseNumberAndText -> verseNumberAndText.text.isNotEmpty() }) {
                "All VerseNumberAndText objects should have text that isn\'t empty."
            }

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

            if (verses.size == versesWithRemoval.size) throw Throwable("No matching verse was found to remove.")

            if (verses.size > versesWithRemoval.size + 1) throw Throwable("More than one matching verse was found and deleted.")

            setVersesToFile(verses = versesWithRemoval)
        }
    }

    override suspend fun updateVerse(updatedVerse: Verse): Result<Unit> {
        return doWork(
            failureMessage = "Failed to update verse($updatedVerse)."
        ) {
            val verses = getVersesFromFile().getOrThrow()

            if (verses.filterByUUID(uuid = updatedVerse.uuid).firstOrNull() != null) {
                val versesWithChange = verses.map { verse ->
                    if (verse.uuid == updatedVerse.uuid) {
                        updatedVerse
                    } else {
                        verse
                    }
                }

                setVersesToFile(verses = versesWithChange)
            } else {
                // verse was not found, just save it
                addVerse(updatedVerse).getOrThrow()
            }
        }
    }

    private suspend fun getVersesFromFile(): Result<Set<Verse>> {
        return doWork(
            failureMessage = "Failed to get verses from File($VERSES_FILE_NAME)."
        ) {
            val versesJsonString =
                jsonFileReader.readFromJsonFile(fileName = VERSES_FILE_NAME).getOrThrow()

            log.debug(
                tag = logTag,
                message = "Successfully retrieved verses($versesJsonString) from File($VERSES_FILE_NAME)."
            )

            if (versesJsonString.isNotEmpty()) {
                versesJsonString.fromJsonString<Set<Verse>>()
            } else {
                emptySet()
            }
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

    // region Extension functions
    private fun Sequence<Verse>.filterByBook(book: String?): Sequence<Verse> {
        return this.filter { verse: Verse ->
            book?.let {
                verse.book.lowercase().contains(book.lowercase())
            } ?: true // if book is null, let all through
        }
    }

    private fun Sequence<Verse>.filterByChapter(chapter: Int?): Sequence<Verse> {
        return this.filter { verse: Verse ->
            chapter?.let {
                verse.chapter == chapter
            } ?: true // if chapter is null, let all through
        }
    }

    private fun Sequence<Verse>.filterByVerseNumber(verseNumber: Int?): Sequence<Verse> {
        return this.filter { verse: Verse ->
            verseNumber?.let {
                verse.verseText.any { verseNumberAndText -> verseNumberAndText.verseNumber == verseNumber }
            } ?: true // if chapter is null, let all through
        }
    }

    private fun Sequence<Verse>.filterByTags(tags: List<String>?): Sequence<Verse> {
        return this.filter { verse: Verse ->
            tags?.let {
                verse.tags.containsAllIgnoreCase(tags)
            } ?: true // if tags are null, let all through
        }
    }

    private fun Sequence<Verse>.filterByPartialText(partialText: String?): Sequence<Verse> {
        return this.filter { verse: Verse ->
            partialText?.let {
                verse.verseText.any { verseNumberAndText ->
                    verseNumberAndText.text.contains(
                        partialText,
                        ignoreCase = true
                    )
                }
            } ?: true // if partialText is null, let all through
        }
    }

    private fun Set<Verse>.filterByUUID(uuid: UUID?): List<Verse> {
        return this.filter { verse ->
            uuid?.let {
                verse.uuid == uuid
            } ?: true // let all through if uuid is null
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
                this.verseText.matches(other.verseText) &&
                this.uuid == other.uuid
    }

    /**
     * @param filterVerse The [Verse] we do NOT want in the [Set]
     * @return [Set] of all [Verse]s that do not match the given [Verse]
     */
    private fun Set<Verse>.filterWithoutVerse(filterVerse: Verse): List<Verse> {
        return this.filterNot { verse ->
            verse.matches(other = filterVerse)
        }
    }
    // endregion
}