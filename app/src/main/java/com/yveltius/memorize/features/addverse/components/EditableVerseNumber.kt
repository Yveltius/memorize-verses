package com.yveltius.memorize.features.addverse.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yveltius.memorize.R
import com.yveltius.memorize.features.addverse.viewmodels.AddVerseViewModel

@Composable
fun EditableVerseNumber(
    verseNumberAndText: AddVerseViewModel.AddVerseNumberAndText,
    onVerseNumberChanged: (Int, String) -> Unit,
    index: Int,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = verseNumberAndText.verseNumber,
        onValueChange = { onVerseNumberChanged(index, it) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        label = {
            Text(
                text = stringResource(R.string.verse_number)
            )
        },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(text = stringResource(R.string.input_error))
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun Default() {
    EditableVerseNumber(
        verseNumberAndText = AddVerseViewModel.AddVerseNumberAndText(),
        onVerseNumberChanged = { integer, string -> },
        index = 0,
        isError = false,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}