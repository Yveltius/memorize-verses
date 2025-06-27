package com.yveltius.versememorization.data.util

import java.util.logging.Level
import java.util.logging.Logger

internal class VerseMemorizationLog(
    identifier: String = "VerseMemorizationLog"
): Log {
    private val logger: Logger = Logger.getLogger(identifier)

    override fun debug(tag: String, message: String) {
        logger.log(
            Level.INFO,
            "$tag: $message"
        )
    }

    override fun error(tag: String, message: String, throwable: Throwable) {
        logger.log(
            Level.SEVERE,
            "$tag: $message\n${throwable.message}"
        )
    }

    override fun <T> logErrorAndReturnResult(
        tag: String,
        message: String,
        throwable: Throwable
    ): Result<T> {
        error(tag, message, throwable)

        return Result.failure(
            Throwable("$tag: $message\n${throwable.message}")
        )
    }
}