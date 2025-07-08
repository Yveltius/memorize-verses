package com.yveltius.versememorization.domain.verses

import com.yveltius.versememorization.data.verses.VerseRepository
import com.yveltius.versememorization.entity.verses.Verse

class UpdateVerseUseCase internal constructor(
    private val verseRepository: VerseRepository
){
    suspend fun updateVerse(updatedVerse: Verse): Result<Unit> {
        return verseRepository.updateVerse(updatedVerse = updatedVerse)
    }
}