package com.yveltius.versememorization.entity.verses

import kotlinx.serialization.Serializable

@Serializable
data class VerseNumberAndText(
    val verseNumber: Int,
    val text: String
)
