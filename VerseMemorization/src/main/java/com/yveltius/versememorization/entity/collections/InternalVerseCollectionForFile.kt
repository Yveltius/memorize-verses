package com.yveltius.versememorization.entity.collections

import com.yveltius.versememorization.entity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class InternalVerseCollectionForFile(
    val name: String,
    val verseUuids: Set<@Serializable(with = UUIDSerializer::class) UUID>
)
