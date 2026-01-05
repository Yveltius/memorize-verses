package com.yveltius.memorize.features.addverse.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yveltius.memorize.R

@Composable
fun BookAndChapter(
    book: String,
    onBookChanged: (String) -> Unit,
    chapter: String,
    onChapterChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasChapterError =
        chapter.isNotEmpty() && (chapter.toIntOrNull() == null || (chapter.toIntOrNull()
            ?: -1) <= 0)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        OutlinedTextField(
            value = book,
            onValueChange = onBookChanged,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences
            ),
            label = {
                Text(text = stringResource(R.string.book))
            },
            supportingText = {
                if (book.isEmpty()) {
                    Text(text = stringResource(R.string.input_cant_be_empty))
                }
            },
            modifier = Modifier.weight(0.6f)
        )
        OutlinedTextField(
            value = chapter,
            onValueChange = onChapterChanged,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            label = {
                Text(
                    text = stringResource(R.string.chapter),
                    overflow = TextOverflow.Ellipsis
                )
            },
            isError = hasChapterError,
            supportingText = {
                if (hasChapterError) {
                    Text(text = stringResource(R.string.input_error))
                }
            },
            modifier = Modifier.weight(0.4f)
        )
    }
}
