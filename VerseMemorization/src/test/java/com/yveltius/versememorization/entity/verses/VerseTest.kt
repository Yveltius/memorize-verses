package com.yveltius.versememorization.entity.verses

import com.yveltius.versememorization.data.choosenextword.ChooseNextWord
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.assertThrows
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.Test


class VerseTest {
    @Test
    fun `verse with 1 verse should return single element string`() {
        val verse = Verse(
            book = "John",
            chapter = 15,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 7,
                    text = "This is the verse text."
                )
            ),
            tags = listOf()
        )

        val expected = "John 15:7"
        val actual = verse.getVerseString()

        assertTrue("Expected: $expected, Actual: $actual", actual == expected)
    }

    @Test
    fun `verse with 2 verses should return composite string for getVerseString`() {
        val verse = Verse(
            book = "John",
            chapter = 15,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 7,
                    text = "This is the verse text."
                ),
                VerseNumberAndText(verseNumber = 8, text = "Further verse text.")
            ),
            tags = listOf()
        )

        val expected = "John 15:7-8"
        val actual = verse.getVerseString()

        assertTrue("Expected: $expected, Actual: $actual", actual == expected)
    }

    @Test
    fun `verse with many verses should return composite string for getVerseString`() {
        val verse = Verse(
            book = "John",
            chapter = 15,
            verseText = (1..100).map { VerseNumberAndText(verseNumber = it, text = it.toString()) },
            tags = listOf()
        )

        val expected = "John 15:1-100"
        val actual = verse.getVerseString()

        assertTrue("Expected: $expected, Actual: $actual", actual == expected)
    }

    @Test
    fun `verse should return a list of lists of words`() {
        val verse = Verse(
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
        )

        val expected = listOf(
            listOf("Therefore", "I", "exhort", "you", "brothers", "by", "the", "mercies", "of", "God", "to", "present", "your", "bodies", "as", "a", "sacrifice—living", "holy", "and", "pleasing", "to", "God", "which", "is", "your", "spiritual", "service", "of", "worship"),
            listOf("And", "do", "not", "be", "conformed", "to", "this", "world", "but", "be", "transformed", "by", "the", "renewing", "of", "your", "mind", "so", "that", "you", "may", "approve", "what", "the", "will", "of", "God", "is", "that", "which", "is", "good", "and", "pleasing", "and", "perfect")
        )

        val actual = verse.getWords()

        assertTrue(
            "Expected: $expected,\nActual: $actual",
            expected.containsAll(actual)
        )
    }

    @Test
    fun `verse hasMultiple returns true when there are multiple verses`() {
        val verse = Verse(
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
        )

        val expected = true
        val actual = verse.hasMultipleVerses

        assertTrue(
            "Expected: $expected, Actual: $actual",
            expected == actual
        )
    }

    @Test
    fun `verse hasMultiple returns false when there are not multiple verses`() {
        val verse = Verse(
            book = "Romans",
            chapter = 12,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 1,
                    text = "Therefore I exhort you, brothers, by the mercies of God, to present your bodies as a sacrifice—living, holy, and pleasing to God, which is your spiritual service of worship."
                ),
            ),
            tags = listOf("Discipleship Verse", "Obedience to Christ")
        )

        val expected = false
        val actual = verse.hasMultipleVerses

        assertTrue(
            "Expected: $expected, Actual: $actual",
            expected == actual
        )
    }

    @Test
    fun `getVerseString with given index returns correct string`() {
        val verse = Verse(
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
        )

        val expected = "Romans 12:2"
        val actual = verse.getVerseString(index = 1)

        assertTrue(
            "Expected: $expected, Actual: $actual",
            expected == actual
        )
    }

    @Test
    fun `getVerseString throws when given an index that is too small`() {
        val verse = Verse(
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
        )

        assertThrows(Throwable::class.java) {
            verse.getVerseString(index = -100)
        }
    }

    @Test
    fun `getVerseString throws when given an index that is too big`() {
        val verse = Verse(
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
        )

        assertThrows(Throwable::class.java) {
            verse.getVerseString(index = 100)
        }
    }
}