package com.yveltius.memorize.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yveltius.memorize.R
import com.yveltius.memorize.ui.components.AppScaffold
import com.yveltius.memorize.ui.text.buildAnnotatedVerse
import com.yveltius.memorize.viewmodels.AddVerseViewModel
import com.yveltius.versememorization.entity.verses.Verse
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddVerseScreen(
    onBackPress: () -> Unit,
    addVerseViewModel: AddVerseViewModel = koinViewModel()
) {
    AppScaffold {
        val uiState by addVerseViewModel.uiState.collectAsState()

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopBar(
                    onAddVerseRequest = {
                        addVerseViewModel.addVerse(
                            Verse(
                                book = uiState.book,
                                chapter = uiState.chapter.toInt(),
                                verseText = uiState.verseNumberAndTextList.map { it.transform() },
                                tags = listOf()
                            )
                        )
                    },
                    onBackPress = onBackPress
                )

                VerseForm(
                    book = uiState.book,
                    onBookChanged = addVerseViewModel::onBookChanged,
                    chapter = uiState.chapter,
                    onChapterChanged = addVerseViewModel::onChapterChanged,
                    verseNumberAndTextList = uiState.verseNumberAndTextList,
                    onVerseNumberChanged = addVerseViewModel::onVerseNumberChanged,
                    onVerseTextChanged = addVerseViewModel::onVerseTextChanged,
                    onAddVerseNumberAndTextClick = addVerseViewModel::onAddVerseNumberAndText,
                    onDeleteVerseNumberAndText = addVerseViewModel::onDeleteVerseNumberAndText
                )
            }
        }
    }
}

@Composable
fun TopBar(
    onAddVerseRequest: () -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = null,
            modifier = Modifier.clickable { onBackPress() }
        )

        TextButton(
            onClick = onAddVerseRequest
        ) {
            Text(text = stringResource(R.string.add))
        }
    }
}

@Composable
private fun VerseForm(
    book: String,
    onBookChanged: (String) -> Unit,
    chapter: String,
    onChapterChanged: (String) -> Unit,
    verseNumberAndTextList: List<AddVerseViewModel.AddVerseNumberAndText>,
    onVerseNumberChanged: (index: Int, String) -> Unit,
    onVerseTextChanged: (index: Int, String) -> Unit,
    onAddVerseNumberAndTextClick: () -> Unit,
    onDeleteVerseNumberAndText: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BookAndChapter(book, onBookChanged, chapter, onChapterChanged)
        }

        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }

        itemsIndexed(items = verseNumberAndTextList) { index, verseNumberAndText ->
            EditableVerseNumberAndText(
                index = index,
                verseNumberAndText = verseNumberAndText,
                onVerseNumberChanged = onVerseNumberChanged,
                onVerseTextChanged = onVerseTextChanged,
                onDeleteVerseNumberAndText = onDeleteVerseNumberAndText,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }

        item {
            Button(
                onClick = onAddVerseNumberAndTextClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Text(text = stringResource(R.string.add_verse_text))
                }
            }
        }
    }
}

@Composable
private fun BookAndChapter(
    book: String,
    onBookChanged: (String) -> Unit,
    chapter: String,
    onChapterChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = book,
            onValueChange = onBookChanged,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            label = {
                Text(text = stringResource(R.string.book))
            },
            modifier = Modifier.weight(0.6f)
        )
        OutlinedTextField(
            value = chapter,
            onValueChange = onChapterChanged,
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
            isError = chapter.toIntOrNull() == null && chapter.isNotEmpty(),
            modifier = Modifier.weight(0.4f)
        )
    }
}

@Composable
private fun EditableVerseNumberAndText(
    index: Int,
    verseNumberAndText: AddVerseViewModel.AddVerseNumberAndText,
    onVerseNumberChanged: (index: Int, String) -> Unit,
    onVerseTextChanged: (index: Int, String) -> Unit,
    onDeleteVerseNumberAndText: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = buildAnnotatedVerse(listOf(verseNumberAndText.transform())),
                modifier = Modifier.fillMaxWidth(),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                modifier = Modifier.clickable { onDeleteVerseNumberAndText(index) })
        }
        OutlinedTextField(
            value = verseNumberAndText.verseNumber,
            onValueChange = { onVerseNumberChanged(index, it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            label = {
                Text(
                    text = stringResource(R.string.verse_number)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = verseNumberAndText.verseText,
            onValueChange = { onVerseTextChanged(index, it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            label = {
                Text(text = stringResource(R.string.verse_text))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}