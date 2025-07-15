package com.yveltius.versememorization.data.choosenextword

import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText

class ChooseNextWord {
    fun parseText(text: String): List<String> {
        val parsedText = text
            .split(' ')
            .map { word -> if (word.first().isLowerCase() || !(word.first().isLetter())) word.trim('\'', '\"') else word }
            .map { word -> word.trim('.', '!', '?', ',', '(', ')', '\"', '[', ']', ';', ':', '…') }
        return parsedText
    }
}