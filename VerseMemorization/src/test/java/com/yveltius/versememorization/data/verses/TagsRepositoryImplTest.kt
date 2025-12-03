package com.yveltius.versememorization.data.verses

import com.yveltius.versememorization.data.util.JsonFileReader
import com.yveltius.versememorization.data.util.Log
import com.yveltius.versememorization.data.util.VerseMemorizationLog
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

class TagsRepositoryImplTest {
    @BeforeTest
    fun before() {
        startKoin {
            modules(
                module {
                    single<Log> { VerseMemorizationLog() }
                    single<JsonFileReader>(named("notEmpty")) { VerseJsonFileReaderTestImpl() }
                    single<JsonFileReader>(named("Empty")) { EmptyVerseJsonFileReaderTestImpl() }
                    single<VerseRepository> {
                        VerseRepositoryImpl(
                            log = get(),
                            jsonFileReader = get(named("notEmpty"))
                        )
                    }
                    single<VerseRepository>(named("Empty")) {
                        VerseRepositoryImpl(
                            log = get(),
                            jsonFileReader = get(named("Empty"))
                        )
                    }
                    single<TagsRepository>(named("Empty")) {
                        TagsRepositoryImpl(
                            log = get(),
                            verseRepository = get(named("Empty"))
                        )
                    }
                    single<TagsRepository> {
                        TagsRepositoryImpl(
                            log = get(),
                            verseRepository = get()
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
    fun `empty verse repo returns an empty tag list`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java, named("Empty"))
        val tagsRepository: TagsRepository by inject(TagsRepository::class.java, named("Empty"))

        val verses = runBlocking { verseRepository.getVerses().getOrThrow() }

        assertTrue {
            verses.isEmpty()
        }

        val expected = 0

        val actual = runBlocking { tagsRepository.getAllTags().getOrThrow().size }

            assertTrue(
                message = "Expected: $expected, actual: $actual"
            ) {
                expected == actual
            }
    }

    @Test
    fun `no tag appears more than once`() {
        val tagsRepository: TagsRepository by inject(TagsRepository::class.java)

        val tags = runBlocking { tagsRepository.getAllTags().getOrThrow() }

        // check for Discipleship Verse, Obedience to Christ, Separate from the World
        val discipleshipVerseCount = tags.count { tag ->
            tag == "Discipleship Verse"
        }
        val obedienceToChristCount = tags.count { tag ->
            tag == "Obedience to Christ"
        }
        val separateFromTheWorldCount = tags.count { tag ->
            tag == "Separate from the World"
        }

        val expected = 1

        assertTrue(
            message = "Expected: $expected, Actual: $discipleshipVerseCount"
        ) {
            expected == discipleshipVerseCount
        }

        assertTrue(
            message = "Expected: $expected, Actual: $obedienceToChristCount"
        ) {
            expected == obedienceToChristCount
        }

        assertTrue(
            message = "Expected: $expected, Actual: $separateFromTheWorldCount"
        ) {
            expected == separateFromTheWorldCount
        }
    }
}