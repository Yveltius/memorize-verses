package com.yveltius.versememorization.data.util

internal interface JsonFileReader {
    suspend fun readFromJsonFile(fileName: String): Result<String>

    suspend fun writeToJsonFile(fileName: String, content: String): Result<Unit>
}