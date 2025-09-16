package com.yveltius.versememorization.data.choosenextword

class ChooseNextWord {
    fun parseText(text: String): List<String> {
        val parsedText = text
            .split(' ')
            .filter { it.isNotEmpty() }
            .map { word -> if (word.first().isLowerCase() || !(word.first().isLetter())) word.trim('\'', '\"') else word }
            .map { word -> word.trim('.', '!', '?', ',', '(', ')', '\"', '[', ']', ';', ':', '…') }
        return parsedText
    }

    fun parseTextKeepingPunctuation(text: String): List<String> {
        assert(text.isNotEmpty())

        val parsedText = text
            .split(' ')
            .map { word ->
                val specialCharRegexString = "[.?!,();:…\"\\[\\]]"
                val specialCharArray = charArrayOf('.', '?', '!', ',', '\"', '(', ')', '…', '[', ']', ';', ':', '\'')

                if (word.contains(Regex(pattern = specialCharRegexString)) || word.firstOrNull() == '\'') {
                    val specialCharCount = word.count { isSpecialChar(specialCharArray, char = it) }

                    val indexOfFirstSpecialCharacter = word.indexOfAny(chars = specialCharArray)

                    val specialChars = CharArray(size = specialCharCount) { index -> word[indexOfFirstSpecialCharacter + index] }.filter { it.isLetterOrDigit().not() }

                    val specialCharsList = specialChars.map { char -> char.toString() }.toMutableList()
                    if (indexOfFirstSpecialCharacter == 0) {
                        specialCharsList.add(word.trim(*specialCharArray))
                        return@map specialCharsList
                    } else {
                        specialCharsList.add(0, word.trim(*specialCharArray))
                        return@map specialCharsList
                    }
                } else {
                    return@map listOf(word)
                }
            }.flatten()

        return parsedText.filter { it.isNotEmpty() }
    }

    private fun isSpecialChar(specialCharArray: CharArray, char: Char): Boolean {
        return specialCharArray.any { it == char }
    }
}