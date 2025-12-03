package com.yveltius.versememorization.entity.collections

import com.yveltius.versememorization.entity.verses.Verse

data class VerseCollection(
    val name: String,
    val verses: Set<Verse>
)
