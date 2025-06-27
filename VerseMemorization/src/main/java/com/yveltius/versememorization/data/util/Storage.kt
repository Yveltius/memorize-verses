package com.yveltius.versememorization.data.util

internal interface Storage {
    suspend fun getString(key: String, defaultValue: String? = null): Result<String>
    suspend fun setString(key: String, value: String): Result<Unit>
}