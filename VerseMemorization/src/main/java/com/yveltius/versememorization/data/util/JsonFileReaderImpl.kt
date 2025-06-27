package com.yveltius.versememorization.data.util

import android.content.Context
import com.yveltius.versememorization.entity.util.Worker
import java.io.File

internal class JsonFileReaderImpl(
    log: Log,
    private val context: Context
) : JsonFileReader, Worker(log) {
    override val logTag: String = "JsonFileReaderImpl"

    override suspend fun readFromJsonFile(fileName: String): Result<String> {
        return doWork(
            failureMessage = "Failed to read JSON from File($fileName)."
        ) {
            val file = File(context.filesDir, fileName)

            if (!file.exists()) {
                file.createNewFile()
                log.debug(
                    tag = logTag,
                    message = "File($fileName) didn't exist so it was created."
                )
            }

            val fileContents = file.readText()

            log.debug(
                tag = logTag,
                message = "Successfully read content($fileContents) from File($fileName)."
            )

            fileContents
        }
    }

    override suspend fun writeToJsonFile(fileName: String, content: String): Result<Unit> {
        return doWork(
            failureMessage = "Failed to write content($content) to File($fileName)."
        ) {
            val file = File(context.filesDir, fileName)

            if (!file.exists()) {
                file.createNewFile()
                log.debug(
                    tag = logTag,
                    message = "File($fileName) didn't exist so it was created."
                )
            }

            file.writeText(content)

            log.debug(
                tag = logTag,
                message = "Successfully wrote content($content) to File($fileName)."
            )
        }
    }
}