package com.yveltius.versememorization.data.collections

import com.yveltius.versememorization.data.util.JsonFileReader
import com.yveltius.versememorization.data.util.Log
import com.yveltius.versememorization.data.verses.VerseRepository
import com.yveltius.versememorization.entity.collections.InternalVerseCollectionForFile
import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.util.Worker
import com.yveltius.versememorization.entity.util.fromJsonString
import com.yveltius.versememorization.entity.util.toJsonString
import com.yveltius.versememorization.entity.verses.Verse

private const val COLLECTIONS_FILE_NAME = "collections"

internal class VerseCollectionRepositoryImpl(
    log: Log,
    private val jsonFileReader: JsonFileReader,
    private val verseRepository: VerseRepository
) : VerseCollectionRepository, Worker(log) {
    override val logTag: String = "CollectionRepositoryImpl"

    override suspend fun getAllCollections(): Result<Set<VerseCollection>> {
        return doWork(
            failureMessage = "Failed to get VerseCollections."
        ) {
            val collections = getCollectionsFromFile().getOrThrow()

            collections
        }
    }

    override suspend fun getCollection(collectionName: String): Result<VerseCollection> {
        return doWork(
            failureMessage = "Failed to get a VerseCollection for [$collectionName]."
        ) {
            assert(collectionName.isNotEmpty())

            val allCollections = getAllCollections().getOrThrow()

            val foundCollection =
                allCollections.first { it.name.equals(collectionName, ignoreCase = true) }

            log.debug(
                tag = logTag,
                message = "Found VerseCollection matching name($collectionName)."
            )

            foundCollection
        }
    }

    override suspend fun getCollectionsForVerse(verse: Verse): Result<Set<VerseCollection>> {
        return doWork(
            failureMessage = "Failed to get collections containing ${verse.getVerseString()}."
        ) {
            val allCollections = getAllCollections().getOrThrow()

            val filteredCollections = allCollections.filter { collection ->
                collection.verses.any { it.uuid == verse.uuid }
            }.toSet()

            log.debug(
                tag = "VerseCollectionRepositoryImpl",
                message = "Successfully found ${filteredCollections.size} collection(s) containing ${verse.getVerseString()}."
            )

            filteredCollections
        }
    }

    override suspend fun addCollection(newCollection: VerseCollection): Result<Unit> {
        return doWork(
            failureMessage = "Failed to add VerseCollection($newCollection) to collections."
        ) {
            assert(newCollection.name.isNotEmpty()) {
                "Verse collection name cannot be empty."
            }

            val allCollections = getAllCollections().getOrThrow()

            assert(!allCollections.any { (name, _) ->
                newCollection.name.equals(name, ignoreCase = true)
            }) {
                "You cannot add a VerseCollection that has the same name as another."
            }

            writeCollectionsToFile(collections = allCollections + newCollection).getOrThrow()
        }
    }

    override suspend fun addCollection(newCollectionName: String): Result<Unit> {
        val newCollection = VerseCollection(name = newCollectionName, verses = setOf())

        return addCollection(newCollection)
    }

    override suspend fun deleteCollection(collection: VerseCollection): Result<Unit> {
        return doWork(
            failureMessage = "Failed to delete VerseCollection($collection)."
        ) {
            val allCollections = getAllCollections().getOrThrow()

            writeCollectionsToFile(
                collections = allCollections
                    .filter { verseCollection -> collection.name != verseCollection.name }
                    .toSet()
            ).getOrThrow()

            log.debug(
                tag = logTag,
                message = "Successfully deleted VerseCollection(${collection.name}."
            )
        }
    }

    override suspend fun addVerseToCollection(collectionName: String, verse: Verse): Result<Unit> {
        return doWork(
            failureMessage = "Failed to add Verse($verse) to VerseCollection($collectionName)."
        ) {
            val allCollections = getAllCollections().getOrThrow()

            val collectionToUpdate =
                allCollections.find { it.name.equals(collectionName, ignoreCase = true) }!!
            val updatedCollection = VerseCollection(
                name = collectionToUpdate.name,
                verses = collectionToUpdate.verses + verse
            )

            val allCollectionsWithUpdatedCollection = allCollections.map {
                if (it.name.equals(collectionToUpdate.name, ignoreCase = true)) {
                    updatedCollection
                } else {
                    it
                }
            }.toSet()

            writeCollectionsToFile(collections = allCollectionsWithUpdatedCollection).getOrThrow()

            log.debug(
                tag = logTag,
                message = "Successfully updated VerseCollection($collectionName) with Verse(${verse.getVerseString()})."
            )
        }
    }

    override suspend fun removeVerseFromCollection(
        collectionName: String,
        verse: Verse
    ): Result<Unit> {
        return doWork(
            failureMessage = "Failed to remove Verse(${verse.getVerseString()} from VerseCollection($collectionName)."
        ) {
            assert(collectionName.isNotEmpty())

            val allCollections = getAllCollections().getOrThrow()

            val verseCollection = allCollections
                    .first { it.name.equals(collectionName, ignoreCase = true) }

            val newVerseCollection = VerseCollection(
                name = verseCollection.name,
                verses = verseCollection.verses.filter { it.uuid != verse.uuid }.toSet()
            )

            val newCollections = allCollections.map { collection ->
                if (collection.name.equals(collectionName, ignoreCase = true)) {
                    newVerseCollection
                } else {
                    collection
                }
            }.toSet()

            writeCollectionsToFile(collections = newCollections).getOrThrow()

            log.debug(
                tag = logTag,
                message = "Successfully removed Verse(${verse.getVerseString()} from VerseCollection($collectionName)."
            )
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
                log.debug(
                    tag = logTag,
                    message = "No collections found in File($COLLECTIONS_FILE_NAME)."
                )

                return@doWork emptySet()
            }

            log.debug(
                tag = logTag,
                message = "Successfully retrieved internal collections($internalCollectionsJsonString) from File($COLLECTIONS_FILE_NAME)."
            )

            val internalCollections =
                internalCollectionsJsonString.fromJsonString<List<InternalVerseCollectionForFile>>()

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

    private suspend fun writeCollectionsToFile(collections: Set<VerseCollection>): Result<Unit> {
        return doWork(
            failureMessage = "Failed to save VerseCollections($collections) to File($COLLECTIONS_FILE_NAME)."
        ) {
            val internalCollections = collections.transformToInternal()

            jsonFileReader.writeToJsonFile(
                fileName = COLLECTIONS_FILE_NAME,
                content = internalCollections.toJsonString()
            ).getOrThrow()

            log.debug(
                tag = logTag,
                message = "Successfully wrote VerseCollections($collections) out to File($COLLECTIONS_FILE_NAME)."
            )
        }
    }

    private fun Set<VerseCollection>.transformToInternal(): Set<InternalVerseCollectionForFile> {
        return this.map {
            InternalVerseCollectionForFile(
                name = it.name,
                verseUuids = it.verses.map { verse -> verse.uuid }.toSet()
            )
        }.toSet()
    }
}