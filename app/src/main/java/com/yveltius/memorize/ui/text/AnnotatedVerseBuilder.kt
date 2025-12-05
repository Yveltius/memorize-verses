package com.yveltius.memorize.ui.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.yveltius.versememorization.entity.verses.VerseNumberAndText

@Composable
fun buildAnnotatedVerse(verseNumberAndTexts: List<VerseNumberAndText>): AnnotatedString {
    val superscript = SpanStyle(
        baselineShift = BaselineShift.Superscript,
        fontSize = 10.sp, // font size of superscript,
        color = MaterialTheme.colorScheme.primary
    )

    return buildAnnotatedString {
        for (index in 0 until verseNumberAndTexts.count()) {
            withStyle(superscript) {
                append(verseNumberAndTexts[index].verseNumber.toString() + " ")
            }
            append(verseNumberAndTexts[index].text.takeUnless { text -> text.isEmpty() } ?: "No Text")

            if (index < verseNumberAndTexts.count()) append('\n')
        }
    }
}