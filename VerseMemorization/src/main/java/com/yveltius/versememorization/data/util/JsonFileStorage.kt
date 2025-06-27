package com.yveltius.versememorization.data.util

import com.yveltius.versememorization.entity.util.Worker

internal class JsonFileStorage(
    log: Log,
    private val jsonFileReader: JsonFileReader
): Storage, Worker(log) {
    override val logTag: String = "JsonFileStorage"
    override suspend fun getString(key: String, defaultValue: String?): Result<String> {
        return Result.success("")
    }

    override suspend fun setString(key: String, value: String): Result<Unit> {
        return doWork(
            failureMessage = "Failed to set string($value) for key($key)."
        ) {
            jsonFileReader
        }
    }
}