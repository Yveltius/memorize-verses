package com.yveltius.versememorization.data.verses

import com.yveltius.versememorization.data.util.JsonFileReader
import com.yveltius.versememorization.entity.util.fromJsonString
import com.yveltius.versememorization.entity.util.toJsonString
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import java.util.UUID

class VerseJsonFileReaderTestImpl : JsonFileReader {
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
            tags = listOf("Be Steadfast", "Discipleship Verse"),
            uuid = UUID.fromString("8da5284b-d7b5-457d-b37c-d81c1aa1df37")
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
            tags = listOf("Discipleship Verse", "Separate from the World"),
            uuid = UUID.fromString("f7c9d471-a785-45db-9357-1d660999a2ad")
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
            tags = listOf("Discipleship Verse", "Obedience to Christ"),
            uuid = UUID.fromString("5719e5ca-28bb-4761-b4b9-fe624181194f")
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
            tags = listOf("Discipleship Verse", "Separate from the World"),
            uuid = UUID.fromString("4972009f-0061-4ae2-abe9-db223beab612")
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
            tags = listOf("Discipleship Verse", "Love"),
            uuid = UUID.fromString("b29d3ff7-62b6-4d87-9ae5-ebb8d71a7815")
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
            tags = listOf("Discipleship Verse", "Obedience to Christ"),
            uuid = UUID.fromString("14364d7c-d881-43e1-97f8-7598373315d6")
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
            tags = listOf("Discipleship Verse", "Obedience to Christ"),
            uuid = UUID.fromString("69002cfe-1f5c-4614-b912-4c6913e60162")
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
            tags = listOf("Discipleship Verse", "The Word"),
            uuid = UUID.fromString("4bf7418b-e76f-4808-8b06-fa57ab307474")
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