package com.yveltius.versememorization.data.verses

import com.yveltius.versememorization.data.util.JsonFileReader
import com.yveltius.versememorization.data.util.Log
import com.yveltius.versememorization.data.util.VerseMemorizationLog
import com.yveltius.versememorization.entity.util.fromJsonString
import com.yveltius.versememorization.entity.util.toJsonString
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject

@RunWith(JUnit4::class)
class VerseRepositoryImplTest {
    @Before
    fun before() {
        startKoin {
            modules(
                module {
                    single<Log> { VerseMemorizationLog() }
                    single<JsonFileReader>(named("notEmpty")) { JsonFileReaderTestImpl() }
                    single<JsonFileReader>(named("Empty")) { EmptyJsonFileReaderTestImpl() }
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

    @After
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
        Assert.assertTrue(
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
        Assert.assertTrue(
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
        Assert.assertTrue(
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
        Assert.assertTrue(
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
        Assert.assertTrue(
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
        Assert.assertTrue(
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
        Assert.assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
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
        Assert.assertTrue(
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
        Assert.assertTrue(
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
        Assert.assertTrue(
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
        Assert.assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
    }

    @Test
    fun `initial read from empty or non-existent file should return empty list`() {
        val verseRepository: VerseRepository by inject(VerseRepository::class.java, named("Empty"))
        runBlocking { verseRepository.getVerses().getOrThrow() }
        Assert.assertTrue(true)
    }
    //endregion

    //region removeVerse
    @Test
    fun `remove verse that perfectly matches, not just those that are very close`() {
        /*
        What I mean by the function name is the scenario where you have two
        [Verse] objects, one containing Romans 12:1-2 and one containing Romans 12:2.
        If I am trying to remove Romans 12:2, I should not delete Romans 12:1-2.
        */
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val versesBeforeRemoval = runBlocking { verseRepository.getVerses().getOrThrow() }

        runBlocking {
            verseRepository.removeVerse(
                verse = Verse(
                    book = "Romans",
                    chapter = 12,
                    verseText = listOf(
                        VerseNumberAndText(
                            verseNumber = 2,
                            text = "And do not be conformed to this world, but be transformed by the renewing of your mind, so that you may approve what the will of God is, that which is good and pleasing and perfect."
                        )
                    ),
                    tags = listOf("Discipleship Verse", "Separate from the World")
                )
            ).getOrThrow()
        }

        val versesAfterRemoval = runBlocking { verseRepository.getVerses().getOrThrow() }

        val expected = versesBeforeRemoval.count() - 1
        val actual = versesAfterRemoval.count()
        Assert.assertTrue(
            "Expected verse count: $expected, actual: $actual",
            actual == expected
        )
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
        Assert.assertTrue(
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
            verseRepository.getVerses(book = "John", chapter = 15, verseNumber = 7).getOrThrow().first()
        }
        Assert.assertTrue(
            "Expected verse count: $expected, actual: $verse",
            verse == expected
        )
    }
    //endregion

    class JsonFileReaderTestImpl : JsonFileReader {
        private var verses = listOf(
            Verse(
                book = "Hebrews",
                chapter = 12,
                verseText = listOf(
                    VerseNumberAndText(
                        verseNumber = 3,
                        text = "For consider Him who has endured such hostility by sinners against Himself, so that you will not grow weary, fainting in heart."
                    )
                ),
                tags = listOf("Be Steadfast", "Discipleship Verse")
            ),
            Verse(
                book = "Romans",
                chapter = 12,
                verseText = listOf(
                    VerseNumberAndText(
                        verseNumber = 2,
                        text = "And do not be conformed to this world, but be transformed by the renewing of your mind, so that you may approve what the will of God is, that which is good and pleasing and perfect."
                    )
                ),
                tags = listOf("Discipleship Verse", "Separate from the World")
            ),
            Verse(
                book = "Romans",
                chapter = 12,
                verseText = listOf(
                    VerseNumberAndText(
                        verseNumber = 1,
                        text = "Therefore I exhort you, brothers, by the mercies of God, to present your bodies as a sacrifice—living, holy, and pleasing to God, which is your spiritual service of worship."
                    ),
                    VerseNumberAndText(
                        verseNumber = 2,
                        text = "And do not be conformed to this world, but be transformed by the renewing of your mind, so that you may approve what the will of God is, that which is good and pleasing and perfect."
                    )
                ),
                tags = listOf("Discipleship Verse", "Obedience to Christ")
            ),
            Verse(
                book = "1 John",
                chapter = 2,
                verseText = listOf(
                    VerseNumberAndText(
                        verseNumber = 15,
                        text = "Do not love the world nor the things in the world. If anyone loves the world, the love of the Father is not in him."
                    ),
                    VerseNumberAndText(
                        verseNumber = 16,
                        text = "For all that is in the world, the lust of the flesh and the lust of the eyes and the boastful pride of life, is not from the Father, but is from the world."
                    )
                ),
                tags = listOf("Discipleship Verse", "Separate from the World")
            ),
            Verse(
                book = "1 John",
                chapter = 3,
                verseText = listOf(
                    VerseNumberAndText(
                        verseNumber = 18,
                        text = "Little children, let us not love with word or with tongue, but in deed and truth."
                    )
                ),
                tags = listOf("Discipleship Verse", "Love")
            ),
            Verse(
                book = "2 Corinthians",
                chapter = 5,
                verseText = listOf(
                    VerseNumberAndText(
                        verseNumber = 17,
                        text = "Therefore if anyone is in Christ, he is a new creation; the old things passed away; behold, new things have come."
                    )
                ),
                tags = listOf("Discipleship Verse", "Obedience to Christ")
            ),
            Verse(
                book = "Deuteronomy",
                chapter = 6,
                verseText = listOf(
                    VerseNumberAndText(
                        verseNumber = 4,
                        text = "Hear, O Israel! Yahweh is our God, Yahweh is one!"
                    ),
                    VerseNumberAndText(
                        verseNumber = 5,
                        text = "You shall love Yahweh your God with all your heart and with all your soul and with all your might."
                    ),
                    VerseNumberAndText(
                        verseNumber = 6,
                        text = "These words, which I am commanding you today, shall be on your heart."
                    ),
                    VerseNumberAndText(
                        verseNumber = 7,
                        text = "You shall teach them diligently to your sons and shall speak of them when you sit in your house and when you walk by the way and when you lie down and when you rise up."
                    ),
                    VerseNumberAndText(
                        verseNumber = 8,
                        text = "You shall bind them as a sign on your hand, and they shall be as phylacteries between your eyes."
                    ),
                    VerseNumberAndText(
                        verseNumber = 9,
                        text = "You shall write them on the doorposts of your house and on your gates."
                    ),
                ),
                tags = listOf("Discipleship Verse", "Obedience to Christ")
            ),
            Verse(
                book = "Joshua",
                chapter = 1,
                verseText = listOf(
                    VerseNumberAndText(
                        verseNumber = 8,
                        text = "This book of the law shall not depart from your mouth, but you shall meditate on it day and night, so that you may be careful to do according to all that is written in it; for then you will make your way successful, and then you will be prosperous."
                    ),
                    VerseNumberAndText(
                        verseNumber = 9,
                        text = "Have I not commanded you? Be strong and courageous! Do not be in dread or be dismayed, for Yahweh your God is with you wherever you go."
                    )
                ),
                tags = listOf("Discipleship Verse", "The Word")
            )
        )

        override suspend fun readFromJsonFile(fileName: String): Result<String> {
            return Result.success(verses.toJsonString())
        }

        override suspend fun writeToJsonFile(fileName: String, content: String): Result<Unit> {
            verses = content.fromJsonString()

            return Result.success(Unit)
        }
    }

    class EmptyJsonFileReaderTestImpl : JsonFileReader {
        override suspend fun readFromJsonFile(fileName: String): Result<String> {
            return Result.success("")
        }

        override suspend fun writeToJsonFile(fileName: String, content: String): Result<Unit> {
            TODO("Not yet implemented")
        }

    }
}