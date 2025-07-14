package com.yveltius.versememorization.entity.verses

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
    fun `verse with 2 verses should return composite string`() {
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
    fun `verse with many verses should return composite string`() {
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
}