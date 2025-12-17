package com.yveltius.versememorization.data.verses

import com.yveltius.versememorization.data.util.JsonFileReader
import com.yveltius.versememorization.data.util.Log
import com.yveltius.versememorization.data.util.VerseMemorizationLog
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class VerseRepositoryImplTest {
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
                }
            )
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    //region getVerses
    @Test
    fun `get verses given book`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val filteredVerses = runBlocking { verseRepository.getVerses(book = "1 John").getOrThrow() }

        val expected = 2
        val actual = filteredVerses.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `get verses given book ignore case`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val filteredVerses =
            runBlocking { verseRepository.getVerses(book = "hEbReWs").getOrThrow() }

        val expected = 1
        val actual = filteredVerses.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `get verses given chapter`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val filteredVerses = runBlocking { verseRepository.getVerses(chapter = 12).getOrThrow() }

        val expected = 3
        val actual = filteredVerses.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `get verses given verse number`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val filteredVerses = runBlocking { verseRepository.getVerses(verseNumber = 8).getOrThrow() }

        val expected = 2
        val actual = filteredVerses.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `get verses given 1 tag`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val filteredVerses = runBlocking {
            verseRepository.getVerses(
                tags = listOf(
                    "Discipleship Verse"
                )
            ).getOrThrow()
        }

        val expected = 8
        val actual = filteredVerses.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `get verses given 2 tag`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val filteredVerses = runBlocking {
            verseRepository.getVerses(
                tags = listOf(
                    "Discipleship Verse",
                    "Separate from the World"
                )
            ).getOrThrow()
        }

        val expected = 2
        val actual = filteredVerses.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `get verses given 2 tag ignore case`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val filteredVerses = runBlocking {
            verseRepository.getVerses(
                tags = listOf(
                    "discipleship verse",
                    "separate FrOm ThE world"
                )
            ).getOrThrow()
        }

        val expected = 2
        val actual = filteredVerses.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `get verse given uuid`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)
        val newVerseUUID = UUID.randomUUID()

        val verse = Verse(
            book = "Amos",
            chapter = 5,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 9,
                    text = "It is He who flashes forth with devastation upon the strong So that devastation comes upon the fortification."
                )
            ),
            tags = listOf(),
            uuid = newVerseUUID
        )

        runBlocking { verseRepository.addVerse(verse).getOrThrow() }

        val actual = runBlocking { verseRepository.getVerse(uuid = newVerseUUID).getOrThrow() }
        assertTrue(
            "Expected verse count: $verse, actual: $actual",
            actual == verse
        )
    }

    @Test
    fun `correct book but wrong chapter should return 0 verses`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val filteredVerses = runBlocking {
            verseRepository.getVerses(
                book = "Hebrews",
                chapter = 8
            ).getOrThrow()
        }

        val expected = 0
        val actual = filteredVerses.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `multiple valid inputs leads to 1 correct verse`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val filteredVerses = runBlocking {
            verseRepository.getVerses(
                book = "Romans",
                chapter = 12,
                verseNumber = 1
            ).getOrThrow()
        }

        val expected = 1
        val actual = filteredVerses.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `partial text match`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val filteredVerses = runBlocking {
            verseRepository.getVerses(
                partialText = "Yahweh"
            ).getOrThrow()
        }

        val expected = 2
        val actual = filteredVerses.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `partial text match ignore case`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val filteredVerses = runBlocking {
            verseRepository.getVerses(
                partialText = "YaHWeH"
            ).getOrThrow()
        }

        val expected = 2
        val actual = filteredVerses.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `initial read from empty or non-existent file should return empty list`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java, named("Empty"))
        val verses = runBlocking { verseRepository.getVerses().getOrThrow() }
        val expected = 0
        val actual = verses.size
        assertTrue(
            message = "Expected: $expected, Actual: $actual",
            expected == actual
        )
    }

    @Test
    fun `get verse using a null uuid returns closest match`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val verse = runBlocking {
            verseRepository.getVerse(
                book = null,
                chapter = 12,
                verseNumber = 3,
                partialText = null,
                tags = listOf(),
                uuid = null
            ).getOrNull()
        }

        val expected = Verse(
            book = "Hebrews",
            chapter = 12,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 3,
                    text = "For consider Him who has endured such hostility by sinners against Himself, so that you will not grow weary, fainting in heart."
                )
            ),
            tags = listOf("Be Steadfast", "Discipleship Verse")
        )

        assertTrue(message = "Expected: $expected\nActual: $verse") {
            // cant use uuid because we create a new verse locally
            verse?.book == expected.book
                    && verse.chapter == expected.chapter
                    && verse.verseText.containsAll(expected.verseText)
        }
    }
    //endregion

    //region removeVerse
    @Test
    fun `remove verse that matches UUID`() {
        /*
        What I mean by the function name is the scenario where you have two
        [Verse] objects, one containing Romans 12:1-2 and one containing Romans 12:2.
        If I am trying to remove Romans 12:2, I should not delete Romans 12:1-2.
        */
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val versesBeforeRemoval = runBlocking { verseRepository.getVerses().getOrThrow() }
        val verseToRemove = versesBeforeRemoval.random()

        runBlocking {
            verseRepository.removeVerse(
                verse = verseToRemove
            ).getOrThrow()
        }

        val versesAfterRemoval = runBlocking { verseRepository.getVerses().getOrThrow() }

        val expected = versesBeforeRemoval.count() - 1
        val actual = versesAfterRemoval.count()
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `throws if there is not a matching verse to remove`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val versesBeforeRemoval = runBlocking { verseRepository.getVerses().getOrThrow() }
        val verseToRemove = versesBeforeRemoval.random()

        assertFailsWith(Throwable::class) {
            runBlocking {
                verseRepository.removeVerse(
                    verse = verseToRemove.copy(uuid = UUID.randomUUID())
                ).getOrThrow()
            }
        }
    }

    @Test
    fun `throws if more than one verse is removed`() {
        //should only happen in a scenario where somehow the exact same verse was saved twice.
        //this state should not occur and other tests should address this case in addVerse
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val versesBeforeRemoval = runBlocking { verseRepository.getVerses().getOrThrow() }
        val verseBeingTestedAgainst = runBlocking { versesBeforeRemoval.random() }

        //force add second matching verse
        runBlocking {
            verseRepository.addVerse(verse = verseBeingTestedAgainst).getOrThrow()
        }

        assertDoesNotThrow(message = "This should not throw because we are using Sets.") {
            runBlocking {
                verseRepository.removeVerse(verse = verseBeingTestedAgainst).getOrThrow()
            }
        }
    }
    //endregion

    //region addVerse
    @Test
    fun `added verse causes verse count to increment by 1`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val versesBeforeAddition = runBlocking { verseRepository.getVerses().getOrThrow() }

        runBlocking {
            verseRepository.addVerse(
                verse = Verse(
                    book = "John",
                    chapter = 15,
                    verseText = listOf(
                        VerseNumberAndText(
                            verseNumber = 7,
                            text = "If you remain in Me, and My words remain in you, ask whatever you want and it will be done for you."
                        )
                    ),
                    tags = listOf("Discipleship Verses", "Prayer")
                )
            )
        }

        val versesAfterAddition = runBlocking { verseRepository.getVerses().getOrThrow() }

        val expected = versesBeforeAddition.size + 1
        val actual = versesAfterAddition.size
        assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `added verse shows up in repo`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val verse = Verse(
            book = "John",
            chapter = 15,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 7,
                    text = "If you remain in Me, and My words remain in you, ask whatever you want and it will be done for you."
                )
            ),
            tags = listOf("Discipleship Verses", "Prayer")
        )

        runBlocking {
            verseRepository.addVerse(
                verse = verse
            )
        }

        val expected = runBlocking {
            verseRepository.getVerses(book = "John", chapter = 15, verseNumber = 7).getOrThrow()
                .first()
        }
        assertTrue(
            "Expected verse count: $expected, actual: $verse",
            verse == expected
        )
    }

    @Test
    fun `exception is thrown when attempting to add a verse with no text`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val verse = Verse(
            book = "John",
            chapter = 15,
            verseText = listOf(),
            tags = listOf("Discipleship Verses", "Prayer")
        )

        assertThrows<Throwable> {
            runBlocking {
                verseRepository.addVerse(verse).getOrThrow()
            }
        }
    }

    @Test
    fun `exception is thrown when attempting to add verse with empty book`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val verse = Verse(
            book = "",
            chapter = 15,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 7,
                    text = "If you remain in Me, and My words remain in you, ask whatever you want and it will be done for you."
                )
            ),
            tags = listOf("Discipleship Verses", "Prayer")
        )

        assertThrows<Throwable> {
            runBlocking {
                verseRepository.addVerse(verse).getOrThrow()
            }
        }
    }

    @Test
    fun `exception is thrown when attempting to add verse with chapter number less than or equal to zero`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val verse = Verse(
            book = "John",
            chapter = -15,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 7,
                    text = "If you remain in Me, and My words remain in you, ask whatever you want and it will be done for you."
                )
            ),
            tags = listOf("Discipleship Verses", "Prayer")
        )

        assertThrows<Throwable> {
            runBlocking {
                verseRepository.addVerse(verse).getOrThrow()
            }
        }
    }

    @Test
    fun `exception is thrown when attempting to add verse with empty text attribute for any verseText item`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val verse = Verse(
            book = "John",
            chapter = 15,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 7,
                    text = ""
                )
            ),
            tags = listOf("Discipleship Verses", "Prayer")
        )

        assertThrows<Throwable> {
            runBlocking {
                verseRepository.addVerse(verse).getOrThrow()
            }
        }
    }

    @Test
    fun `exception is thrown when attempting to add verse with verseNumber attribute less than or equal to zero for any verseText item`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val verse = Verse(
            book = "John",
            chapter = 15,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = -7,
                    text = "If you remain in Me, and My words remain in you, ask whatever you want and it will be done for you."
                )
            ),
            tags = listOf("Discipleship Verses", "Prayer")
        )

        assertThrows<Throwable> {
            runBlocking {
                verseRepository.addVerse(verse).getOrThrow()
            }
        }
    }
    //endregion

    //region updateVerse
    @Test
    fun `updated verse has correct values`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)
        val verseToEdit = runBlocking { verseRepository.getVerses().getOrThrow().first() }

        val editedVerse = verseToEdit.copy(
            book = "Changed",
            chapter = 2,
            verseText = verseToEdit.verseText + VerseNumberAndText(
                verseNumber = 300,
                text = "added text for verse"
            )
        )

        runBlocking {
            verseRepository.updateVerse(updatedVerse = editedVerse).getOrThrow()
        }

        val retrievedEditedVerseFromRepo =
            runBlocking { verseRepository.getVerse(uuid = editedVerse.uuid) }

        assertTrue(
            "Expected: $editedVerse, Actual: $retrievedEditedVerseFromRepo",
            editedVerse == retrievedEditedVerseFromRepo.getOrThrow()
        )
    }

    @Test
    fun `save verse if matching verse is not found`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)
        //get the verse
        val verseToEdit = runBlocking { verseRepository.getVerses().getOrThrow().first() }

        //edit the verse
        val editedVerse = verseToEdit.copy(
            book = "Changed",
            chapter = 2,
            verseText = verseToEdit.verseText + VerseNumberAndText(
                verseNumber = 300,
                text = "added text for verse"
            ),
            uuid = UUID.randomUUID()
        )

        //delete the original from the store
        runBlocking {
            verseRepository.removeVerse(verse = verseToEdit).getOrThrow()
        }

        //attempt to update the verse
        runBlocking {
            verseRepository.updateVerse(updatedVerse = editedVerse).getOrThrow()
        }

        val retrievedEditedVerseFromRepo =
            runBlocking { verseRepository.getVerse(uuid = editedVerse.uuid) }

        assertTrue(
            "Expected: $editedVerse, Actual: $retrievedEditedVerseFromRepo",
            editedVerse == retrievedEditedVerseFromRepo.getOrThrow()
        )
    }
    //endregion
}