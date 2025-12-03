package com.yveltius.versememorization.data.collections

import com.yveltius.versememorization.data.util.JsonFileReader
import com.yveltius.versememorization.data.util.Log
import com.yveltius.versememorization.data.util.VerseMemorizationLog
import com.yveltius.versememorization.data.verses.EmptyVerseJsonFileReaderTestImpl
import com.yveltius.versememorization.data.verses.VerseJsonFileReaderTestImpl
import com.yveltius.versememorization.data.verses.VerseRepository
import com.yveltius.versememorization.data.verses.VerseRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CollectionRepositoryImplTest {
    @BeforeTest
    fun before() {
        startKoin {
            modules(
                module {
                    single<Log> { VerseMemorizationLog() }
                    single<JsonFileReader>(named("notEmpty")) { VerseCollectionJsonFileReaderTestImpl() }
                    single<JsonFileReader>(named("Empty")) { EmptyCollectionJsonFileReaderTestImpl() }
                    single<JsonFileReader>(named("notEmptyVerses")) { VerseJsonFileReaderTestImpl() }
                    single<JsonFileReader>(named("EmptyVerses")) { EmptyVerseJsonFileReaderTestImpl() }
                    single<VerseCollectionRepository> {
                        VerseCollectionRepositoryImpl(
                            log = get(),
                            jsonFileReader = get(named("notEmpty")),
                            verseRepository = get()
                        )
                    }
                    single<VerseCollectionRepository>(named("Empty")) {
                        VerseCollectionRepositoryImpl(
                            log = get(),
                            jsonFileReader = get(named("Empty")),
                            verseRepository = get(named("EmptyVerses"))
                        )
                    }
                    single<VerseRepository> {
                        VerseRepositoryImpl(
                            log = get(),
                            jsonFileReader = get(named("notEmptyVerses"))
                        )
                    }
                    single<VerseRepository>(named("EmptyVerses")) {
                        VerseRepositoryImpl(
                            log = get(),
                            jsonFileReader = get(named("EmptyVerses"))
                        )
                    }
                }
            )
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun `no collections returned from empty repo`() {
        val emptyVerseCollectionRepository: VerseCollectionRepository by inject(
            VerseCollectionRepository::class.java,
            named("Empty")
        )

        val collections =
            runBlocking { emptyVerseCollectionRepository.getCollections().getOrNull() }
        assertTrue(message = "Collection should NOT be null and should be empty. Size: ${collections?.size}") {
            collections != null && collections.isEmpty()
        }
    }

    @Test
    fun `get collections returns correct collection list size`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)

        val actual = runBlocking { collectionRepository.getCollections() }.getOrNull()?.size

        val expected = 3

        assertTrue(message = "Expected: $expected\nActual: $actual") {
            actual == expected
        }
    }
}