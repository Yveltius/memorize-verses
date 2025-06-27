package com.yveltius.versememorization.entity.util

import com.yveltius.versememorization.data.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal abstract class Worker(
    protected val log: Log
) {
    abstract val logTag: String
    suspend fun <T> doWork(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        failureMessage: String,
        block: suspend (coroutineScope: CoroutineScope) -> T
    ): Result<T> {
        return withContext(dispatcher) {
            try {
                val result = block(this)

                Result.success(result)
            } catch (throwable: Throwable) {
                log.logErrorAndReturnResult(
                    tag = logTag,
                    message = failureMessage,
                    throwable = throwable
                )
            }
        }
    }
}