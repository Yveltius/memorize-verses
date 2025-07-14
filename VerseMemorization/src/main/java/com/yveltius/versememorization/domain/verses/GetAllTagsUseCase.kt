package com.yveltius.versememorization.domain.verses

import com.yveltius.versememorization.data.verses.TagsRepository

class GetAllTagsUseCase internal constructor(
    private val tagsRepository: TagsRepository
) {
    suspend fun getAllTags(): Result<List<String>> = tagsRepository.getAllTags()
}