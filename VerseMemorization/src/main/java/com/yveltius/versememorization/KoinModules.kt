package com.yveltius.versememorization

import com.yveltius.versememorization.data.util.JsonFileReader
import com.yveltius.versememorization.data.util.JsonFileReaderImpl
import com.yveltius.versememorization.data.util.Log
import com.yveltius.versememorization.data.util.VerseMemorizationLog
import com.yveltius.versememorization.data.verses.VerseRepository
import com.yveltius.versememorization.data.verses.VerseRepositoryImpl
import com.yveltius.versememorization.domain.verses.AddVersesUseCase
import com.yveltius.versememorization.domain.verses.GetVersesUseCase
import com.yveltius.versememorization.domain.verses.RemoveVersesUseCase
import org.koin.dsl.module

object KoinModules {
    private val repositoryModule = module {
        factory<VerseRepository> {
            VerseRepositoryImpl(
                log = get(),
                jsonFileReader = get()
            )
        }
    }

    private val useCaseModule = module {
        factory { GetVersesUseCase(verseRepository = get()) }

        factory { AddVersesUseCase(verseRepository = get()) }

        factory { RemoveVersesUseCase(verseRepository = get()) }
    }

    private val miscModule = module {
        factory<Log> {
            VerseMemorizationLog()
        }

        factory<JsonFileReader> {
            JsonFileReaderImpl(
                log = get(),
                context = get()
            )
        }
    }

    val modules = arrayOf(repositoryModule, useCaseModule, miscModule)
}