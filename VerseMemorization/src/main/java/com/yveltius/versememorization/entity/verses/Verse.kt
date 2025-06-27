package com.yveltius.versememorization.entity.verses

import kotlinx.serialization.Serializable

@Serializable
data class Verse(
    val book: String,
    val chapter: Int,
    val verseText: List<VerseNumberAndText>,
    val tags: List<String>
)
