package com.yveltius.memorize.features.addverse.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.yveltius.memorize.R
import com.yveltius.memorize.features.addverse.viewmodels.AddVerseViewModel

@Composable
fun EditableVerseText(
    verseNumberAndText: AddVerseViewModel.AddVerseNumberAndText,
    onVerseTextChanged: (Int, String) -> Unit,
    index: Int,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = verseNumberAndText.verseText,
        onValueChange = { onVerseTextChanged(index, it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        label = {
            Text(text = stringResource(R.string.verse_text))
        },
        supportingText = {
            if (verseNumberAndText.verseText.isEmpty()) {
                Text(text = stringResource(R.string.input_cant_be_empty))
            }
        },
        modifier = modifier
    )
}