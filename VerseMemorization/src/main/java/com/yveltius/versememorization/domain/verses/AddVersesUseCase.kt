package com.yveltius.versememorization.domain.verses

import com.yveltius.versememorization.data.verses.VerseRepository
import com.yveltius.versememorization.entity.verses.Verse

class AddVersesUseCase internal constructor(
    private val verseRepository: VerseRepository
) {
    suspend fun addVerse(verse: Verse): Result<Unit> {
        return verseRepository.addVerse(verse)
    }
}