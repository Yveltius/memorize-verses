package com.yveltius.versememorization.data.verses

import com.yveltius.versememorization.data.util.Log
import com.yveltius.versememorization.entity.util.Worker

internal class TagsRepositoryImpl(
    log: Log,
    private val verseRepository: VerseRepository
) : TagsRepository, Worker(log) {
    override val logTag: String = "TagsRepositoryImpl"

    override suspend fun getAllTags(): Result<List<String>> {
        return doWork(
            failureMessage = "Failed to get all tags."
        ) {
            val tags = verseRepository
                .getVerses()
                .getOrThrow()
                .flatMap { it.tags }
                .distinctBy { it }

            log.debug(
                tag = logTag,
                message = "Successfully retrieved all tags($tags)."
            )

            tags
        }
    }
}