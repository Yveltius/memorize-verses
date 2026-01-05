package com.yveltius.versememorization.data.collections

import com.yveltius.versememorization.data.util.JsonFileReader

class EmptyCollectionJsonFileReaderTestImpl: JsonFileReader {
    override suspend fun readFromJsonFile(fileName: String): Result<String> {
        return Result.success("")
    }

    override suspend fun writeToJsonFile(fileName: String, content: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}