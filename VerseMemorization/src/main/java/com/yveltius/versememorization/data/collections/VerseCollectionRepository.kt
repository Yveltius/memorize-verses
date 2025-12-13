package com.yveltius.versememorization.data.collections

import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.verses.Verse

internal interface VerseCollectionRepository {
    suspend fun getAllCollections(): Result<Set<VerseCollection>>

    suspend fun getCollection(collectionName: String): Result<VerseCollection>

    suspend fun getCollectionsForVerse(verse: Verse): Result<Set<VerseCollection>>

    suspend fun addCollection(newCollection: VerseCollection): Result<Unit>

    suspend fun addCollection(newCollectionName: String): Result<Unit>

    suspend fun deleteCollection(collection: VerseCollection): Result<Unit>

    suspend fun addVerseToCollection(collectionName: String, verse: Verse): Result<Unit>

    suspend fun removeVerseFromCollection(collectionName: String, verse: Verse): Result<Unit>
}