package com.yveltius.versememorization.data.util

internal interface Log {
    fun debug(
        tag: String,
        message: String
    )

    fun error(
        tag: String,
        message: String,
        throwable: Throwable
    )

    fun <T> logErrorAndReturnResult(
        tag: String,
        message: String,
        throwable: Throwable
    ): Result<T>
}