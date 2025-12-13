package com.yveltius.versememorization.domain.collections

import com.yveltius.versememorization.data.collections.VerseCollectionRepository
import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.verses.Verse

class VerseCollectionsUseCase internal constructor(
    private val verseCollectionRepository: VerseCollectionRepository
){
    suspend fun getAllCollections(): Result<Set<VerseCollection>> {
        return verseCollectionRepository.getAllCollections()
    }

    suspend fun getCollection(collectionName: String): Result<VerseCollection> {
        return verseCollectionRepository.getCollection(collectionName)
    }

    suspend fun getCollectionsForVerse(verse: Verse): Result<Set<VerseCollection>> {
        return verseCollectionRepository.getCollectionsForVerse(verse)
    }

    suspend fun addCollection(newCollection: VerseCollection): Result<Unit> {
        return verseCollectionRepository.addCollection(newCollection)
    }

    suspend fun addCollection(newCollectionName: String): Result<Unit> {
        return verseCollectionRepository.addCollection(newCollectionName)
    }

    suspend fun deleteCollection(collection: VerseCollection): Result<Unit> {
        return verseCollectionRepository.deleteCollection(collection)
    }

    suspend fun addVerseToCollection(collectionName: String, verse: Verse): Result<Unit> {
        return verseCollectionRepository.addVerseToCollection(collectionName, verse)
    }

    suspend fun removeVerseFromCollection(collectionName: String, verse: Verse): Result<Unit> {
        return verseCollectionRepository.removeVerseFromCollection(collectionName, verse)
    }
}