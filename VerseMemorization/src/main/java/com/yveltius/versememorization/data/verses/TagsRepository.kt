package com.yveltius.versememorization.data.verses

internal interface TagsRepository {
    suspend fun getAllTags(): Result<List<String>>
}