package com.yveltius.versememorization.entity.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val json = Json { ignoreUnknownKeys = true }
val prettyJson = Json { prettyPrint = true }

/**
 * Will serialize a Serializable object to a String
 */
inline fun <reified T> T.toJsonString(): String {
    return try {
        json.encodeToString(this)
    } catch (throwable: Throwable) {
        throw Throwable("${T::class.simpleName} must be a Serializable class.\n${throwable.message}")
    }
}

/**
 * Will deserialize a String to a Serializable object
 */
inline fun <reified T> String.fromJsonString(): T {
    return try {
        json.decodeFromString(this)
    } catch (throwable: Throwable) {
        throw Throwable("${T::class.simpleName} must be a Serializable class.\n${throwable.message}")
    }
}

/**
 * Will serialize a Serializable object to a pretty JSON String
 */
inline fun <reified T> T.toPrettyJsonString(): String {
    return try {
        prettyJson.encodeToString(this)
    } catch (throwable: Throwable) {
        throw Throwable("{T::class.simpleName} must be a Serializable class.\n${throwable.message}")
    }
}