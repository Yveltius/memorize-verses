package com.yveltius.memorize.ui.screens

import androidx.annotation.DrawableRes
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yveltius.memorize.R
import com.yveltius.memorize.ui.components.AppScaffold
import com.yveltius.memorize.ui.text.buildAnnotatedVerse
import com.yveltius.memorize.viewmodels.AddVerseViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
fun AddVerseScreen(
    onBackPress: () -> Unit,
    verseUUID: UUID? = null,
    addVerseViewModel: AddVerseViewModel = koinViewModel()
) {
    val uiState by addVerseViewModel.uiState.collectAsState()
    var fabExpanded by remember { mutableStateOf(value = false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.recentlySavedVerse) {
        delay(500) // wait for the FAB menu to close, not sure how else to handle it.
        uiState.recentlySavedVerse?.let { recentlySavedVerse ->
            val snackbarString = context.getString(
                R.string.snackbar_saved_verse,
                recentlySavedVerse.getVerseString()
            )

            snackbarHostState.showSnackbar(
                message = snackbarString
            )
        }
    }

    LaunchedEffect(uiState.encounteredSaveError) {
        delay(500) // wait for the FAB menu to close, not sure how else to handle it.
        if (uiState.encounteredSaveError) {
            val snackbarString = context.getString(R.string.input_error)

            snackbarHostState.showSnackbar(message = snackbarString)

            addVerseViewModel.resetEncounteredSaveError()
        }
    }

    AppScaffold(
        floatingActionButton = {
            AddVerseFAB(
                fabExpanded = fabExpanded,
                onFabExpandedChanged = { fabExpanded = it },
                onDelete = addVerseViewModel::onDeleteLastVerseNumberAndText,
                onSave = addVerseViewModel::addVerse,
                onAdd = addVerseViewModel::onAddVerseNumberAndText
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopBar(
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
                    onDeleteVerseNumberAndText = addVerseViewModel::onDeleteVerseNumberAndText
                )
            }
        }
    }
}

@Composable
fun TopBar(
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
        IconButton(
            onClick = onBackPress
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_arrow_back_24),
                contentDescription = null
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun AddVerseFAB(
    fabExpanded: Boolean,
    onFabExpandedChanged: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onSave: () -> Unit,
    onAdd: () -> Unit
) {
    FloatingActionButtonMenu(
        modifier = Modifier,
        horizontalAlignment = Alignment.End,
        expanded = fabExpanded,
        button = {
            ToggleFloatingActionButton(
                modifier = Modifier.animateFloatingActionButton(
                    visible = true,
                    alignment = Alignment.BottomEnd
                ),
                checked = fabExpanded,
                onCheckedChange = onFabExpandedChanged
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_edit_24),
                    contentDescription = null
                )
            }
        }
    ) {
        FabMenuItems.entries.forEach { fabMenuItems ->
            FloatingActionButtonMenuItem(
                onClick = {
                    when (fabMenuItems) {
                        FabMenuItems.Delete -> onDelete()
                        FabMenuItems.Save -> onSave()
                        FabMenuItems.Add -> onAdd()
                    }
                    onFabExpandedChanged(false)
                },
                icon = {
                    Icon(
                        painter = painterResource(fabMenuItems.getDrawableResId()),
                        contentDescription = null
                    )
                },
                text = { Text(text = fabMenuItems.name) }
            )
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
        verticalAlignment = Alignment.Top
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
            supportingText = {
                if (chapter.toIntOrNull() == null && chapter.isNotEmpty()) {
                    Text(text = stringResource(R.string.input_error))
                }
            },
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
                painter = painterResource(R.drawable.outline_delete_24),
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
            isError = verseNumberAndText.verseNumber.toIntOrNull() == null && verseNumberAndText.verseNumber.isNotEmpty(),
            supportingText = {
                if (verseNumberAndText.verseNumber.toIntOrNull() == null && verseNumberAndText.verseNumber.isNotEmpty()) {
                    Text(text = stringResource(R.string.input_error))
                }
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
            supportingText = {
                if (verseNumberAndText.verseText.isEmpty()) {
                    Text(text = stringResource(R.string.input_cant_be_empty))
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private enum class FabMenuItems {
    Delete,
    Save,
    Add,
}

@DrawableRes
private fun FabMenuItems.getDrawableResId(): Int {
    return when (this) {
        FabMenuItems.Add -> R.drawable.outline_add_24
        FabMenuItems.Delete -> R.drawable.outline_delete_24
        FabMenuItems.Save -> R.drawable.outline_save_24
    }
}