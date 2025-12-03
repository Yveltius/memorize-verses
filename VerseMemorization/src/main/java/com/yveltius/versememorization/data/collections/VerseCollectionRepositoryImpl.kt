package com.yveltius.versememorization.data.collections

import com.yveltius.versememorization.data.util.JsonFileReader
import com.yveltius.versememorization.data.util.Log
import com.yveltius.versememorization.data.verses.VerseRepository
import com.yveltius.versememorization.entity.collections.InternalVerseCollectionForFile
import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.util.Worker
import com.yveltius.versememorization.entity.util.fromJsonString

private const val COLLECTIONS_FILE_NAME = "collections"

internal class VerseCollectionRepositoryImpl(
    log: Log,
    private val jsonFileReader: JsonFileReader,
    private val verseRepository: VerseRepository
) : VerseCollectionRepository, Worker(log) {
    override val logTag: String = "CollectionRepositoryImpl"

    override suspend fun getCollections(): Result<Set<VerseCollection>> {
        return doWork(
            failureMessage = "Failed to get VerseCollections."
        ) {
            val collections = getCollectionsFromFile().getOrThrow()

            collections
        }
    }

    private suspend fun getCollectionsFromFile(): Result<Set<VerseCollection>> {
        return doWork(
            failureMessage = "Failed to get collections from File($COLLECTIONS_FILE_NAME)."
        ) {
            val internalCollectionsJsonString = jsonFileReader
                .readFromJsonFile(fileName = COLLECTIONS_FILE_NAME)
                .getOrThrow()

            if (internalCollectionsJsonString.isEmpty()) {
                log.debug(tag = logTag, message = "No collections found in File($COLLECTIONS_FILE_NAME).")

                return@doWork emptySet()
            }

            log.debug(
                tag = logTag,
                message = "Successfully retrieved internal collections($internalCollectionsJsonString) from File($COLLECTIONS_FILE_NAME)."
            )

            val internalCollections = internalCollectionsJsonString.fromJsonString<List<InternalVerseCollectionForFile>>()

            val verses = verseRepository.getVerses().getOrThrow()

            log.debug(
                tag = logTag,
                message = "Internal collections have been deserialized and we have retrieved verses from file."
            )

            val collections = internalCollections.map { internalCollection ->
                VerseCollection(
                    name = internalCollection.name,
                    verses = verses.filter { verse ->
                        internalCollection.verseUuids.any { uuid -> uuid == verse.uuid }
                    }.toSet()
                )
            }.toSet()

            log.debug(
                tag = logTag,
                message = "Successfully built VerseCollection(s) from internal collections and verses."
            )

            collections
        }
    }
}