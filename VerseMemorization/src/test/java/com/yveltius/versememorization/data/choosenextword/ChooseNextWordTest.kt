package com.yveltius.versememorization.data.choosenextword

import kotlin.test.Test
import kotlin.test.assertTrue

class ChooseNextWordTest {
    @Test
    fun `simple sentence ending in period just has words returned`() {
        doTestWithGivenText(
            givenText = "This is my simple sentence.",
            expected = listOf("This", "is", "my", "simple", "sentence")
        )
    }

    @Test
    fun `simple sentence ending in question mark just has words returned`() {
        doTestWithGivenText(
            givenText = "This is my simple sentence?",
            expected = listOf("This", "is", "my", "simple", "sentence")
        )
    }

    @Test
    fun `simple sentence ending in exclamation mark just has words returned`() {
        doTestWithGivenText(
            givenText = "This is my simple sentence!",
            expected = listOf("This", "is", "my", "simple", "sentence")
        )
    }

    @Test
    fun `simple sentence with some commas just has words returned`() {
        doTestWithGivenText(
            givenText = "This is, in case you were curious, my simple sentence!",
            expected = listOf("This", "is", "in", "case", "you", "were", "curious", "my", "simple", "sentence")
        )
    }

    @Test
    fun `two sentences with some commas just has words returned`() {
        doTestWithGivenText(
            givenText = "This is, in case you were curious, my simple sentence! You were curious, right?",
            expected = listOf("This", "is", "in", "case", "you", "were", "curious", "my", "simple", "sentence", "You", "were", "curious", "right")
        )
    }

    @Test
    fun `simple sentence with parenthesis just has words returned`() {
        doTestWithGivenText(
            givenText = "This is (I know you were curious) my simple sentence!",
            expected = listOf("This", "is", "I", "know", "you", "were", "curious", "my", "simple", "sentence")
        )
    }

    @Test
    fun `simple sentence with double quotes just has words returned`() {
        doTestWithGivenText(
            givenText = "And he said, \"How dare you?!\"",
            expected = listOf("And", "he", "said", "How", "dare", "you")
        )
    }

    @Test
    fun `simple sentence with double quotes and single quotes just has words returned`() {
        doTestWithGivenText(
            givenText = "And he said, \"And he said, \'How dare you?!\'\"",
            expected = listOf("And", "he", "said", "And", "he", "said", "How", "dare", "you")
        )
    }

    @Test
    fun `simple sentence with bracket comment just has words returned`() {
        doTestWithGivenText(
            givenText = "This is [I know you were curious] my simple sentence!",
            expected = listOf("This", "is", "I", "know", "you", "were", "curious", "my", "simple", "sentence")
        )
    }

    @Test
    fun `verse with a semi-colon`() {
        doTestWithGivenText(
            givenText = "I am for peace; but when I speak, they are for war.",
            expected = listOf("I", "am", "for", "peace", "but", "when", "I", "speak", "they", "are", "for", "war")
        )
    }

    @Test
    fun `verse with a colon`() {
        doTestWithGivenText(
            givenText = "My command is this: Love each other as I have loved you.",
            expected = listOf("My", "command", "is", "this", "Love", "each", "other", "as", "I", "have", "loved", "you")
        )
    }

    @Test
    fun `text with an ellipsis`() {
        doTestWithGivenText(
            givenText = "For God so loved the world…",
            expected = listOf("For", "God", "so", "loved", "the", "world")
        )
    }

    @Test
    fun `verse with a hyphen should have just words and the hyphenated should be there correctly`() {
        doTestWithGivenText(
            givenText = "And he reigned forty-one years in Jerusalem; and his mother’s name was Maacah the daughter of Abishalom.",
            expected = listOf("And", "he", "reigned", "forty-one", "years", "in", "Jerusalem", "and", "his", "mother’s", "name", "was", "Maacah", "the", "daughter", "of", "Abishalom")
        )
    }

    @Test
    fun `verse with possessive noun should return correct words without breaking up the possessive noun`() {
        doTestWithGivenText(
            givenText = "And he reigned forty-one years in Jerusalem; and his mother\'s name was Maacah the daughter of Abishalom.",
            expected = listOf("And", "he", "reigned", "forty-one", "years", "in", "Jerusalem", "and", "his", "mother\'s", "name", "was", "Maacah", "the", "daughter", "of", "Abishalom")
        )
    }

    @Test
    fun `text with a conjunction shouldn't break up the conjunction`() {
        doTestWithGivenText(
            givenText = "This shouldn\'t break. It can\'t break. Don\'t break.",
            expected = listOf("This", "shouldn\'t", "break", "It", "can\'t", "break", "Don\'t", "break")
        )
    }

    @Test
    fun `proper noun possessive ending in s' should return correctly`() {
        doTestWithGivenText(
            givenText = "Example using Moses\' body",
            expected = listOf("Example", "using", "Moses\'", "body")
        )
    }

    @Test
    fun `should be able to parse text ending in a space`() {
        doTestWithGivenText(
            givenText = "Example using Moses\' body ",
            expected = listOf("Example", "using", "Moses\'", "body")
        )
    }

    private fun doTestWithGivenText(givenText: String, expected: List<String>) {
        val actual = ChooseNextWord().parseText(text = givenText)

        println("$expected\n$actual")

        assertTrue(
            message = "Expected: $expected, Actual: $actual"
        ) {
            expected.containsAll(actual) && actual.containsAll(expected)
        }
    }
}