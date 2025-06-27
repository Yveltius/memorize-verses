package com.yveltius.versememorization.domain.verses

import com.yveltius.versememorization.data.verses.VerseRepository
import com.yveltius.versememorization.entity.verses.Verse

class RemoveVersesUseCase internal constructor(
    private val verseRepository: VerseRepository
) {
    suspend fun removeVerse(verse: Verse): Result<Unit> {
        return verseRepository.removeVerse(verse)
    }
}