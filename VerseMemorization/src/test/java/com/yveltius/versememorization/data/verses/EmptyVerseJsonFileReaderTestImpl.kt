package com.yveltius.versememorization.data.verses

import com.yveltius.versememorization.data.util.JsonFileReader

class EmptyVerseJsonFileReaderTestImpl : JsonFileReader {
    override suspend fun readFromJsonFile(fileName: String): Result<String> {
        return Result.success("")
    }

    override suspend fun writeToJsonFile(fileName: String, content: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}