package com.yveltius.versememorization.data.collections

import com.yveltius.versememorization.entity.collections.VerseCollection

internal interface VerseCollectionRepository {
    suspend fun getCollections(): Result<Set<VerseCollection>>
}