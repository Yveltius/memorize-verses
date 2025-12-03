package com.yveltius.versememorization.data.collections

import com.yveltius.versememorization.data.util.JsonFileReader
import com.yveltius.versememorization.entity.collections.InternalVerseCollectionForFile
import com.yveltius.versememorization.entity.util.fromJsonString
import com.yveltius.versememorization.entity.util.toJsonString
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText

class VerseCollectionJsonFileReaderTestImpl: JsonFileReader {
    // region Test Verses
    private val john1: Verse = Verse(
        book = "John",
        chapter = 1,
        verseText = listOf(
            VerseNumberAndText(
                verseNumber = 26,
                text = "John answered them, saying, “I baptize with water, but among you stands One whom you do not know."
            )
        ),
        tags = listOf()
    )

    private val hebrews12: Verse = Verse(
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

    private val romans12: Verse = Verse(
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

    private val romans12Extended: Verse = Verse(
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

    private val firstJohn2: Verse = Verse(
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
    )

    private val firstJohn3: Verse = Verse(
        book = "1 John",
        chapter = 3,
        verseText = listOf(
            VerseNumberAndText(
                verseNumber = 18,
                text = "Little children, let us not love with word or with tongue, but in deed and truth."
            )
        ),
        tags = listOf("Discipleship Verse", "Love")
    )

    private val secondCorinthians5: Verse = Verse(
        book = "2 Corinthians",
        chapter = 5,
        verseText = listOf(
            VerseNumberAndText(
                verseNumber = 17,
                text = "Therefore if anyone is in Christ, he is a new creation; the old things passed away; behold, new things have come."
            )
        ),
        tags = listOf("Discipleship Verse", "Obedience to Christ")
    )

    private val deuteronomy6: Verse = Verse(
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
    )

    private val joshua1: Verse = Verse(
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
    // endregion

    private var collections: List<InternalVerseCollectionForFile> = listOf(
        InternalVerseCollectionForFile(
            name = "Obedience to Christ",
            verseUuids = setOf(
                romans12.uuid, romans12Extended.uuid
            )
        ),
        InternalVerseCollectionForFile(
            name = "The Word",
            verseUuids = setOf(joshua1.uuid)
        ),
        InternalVerseCollectionForFile(
            name = "Be Steadfast",
            verseUuids = setOf(hebrews12.uuid)
        )
    )

    override suspend fun readFromJsonFile(fileName: String): Result<String> {
        return Result.success(collections.toJsonString())
    }

    override suspend fun writeToJsonFile(fileName: String, content: String): Result<Unit> {
        collections = content.fromJsonString()

        return Result.success(Unit)
    }


}