package com.yveltius.memorize.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarHorizontalFabPosition
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.yveltius.memorize.ui.components.AppTopBar
import com.yveltius.memorize.ui.text.buildAnnotatedVerse
import com.yveltius.memorize.ui.theme.AppTheme
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
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        verseUUID?.let {
            addVerseViewModel.getVerseBeingEdited(uuid = verseUUID)
        }
    }

    LaunchedEffect(Unit) {
        addVerseViewModel.getAllTags()
    }

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

    LaunchedEffect(uiState.failedToLoadVerseForEdit) {
        if (uiState.failedToLoadVerseForEdit) {
            val snackbarString = context.getString(R.string.snackbar_failed_to_load_verse_for_edit)

            snackbarHostState.showSnackbar(message = snackbarString)

            addVerseViewModel.resetFailedToLoadVerseForEdit()
        }
    }

    AppTheme {
        Scaffold(
            floatingActionButton = {
                AddVerseBottomBar(
                    onAdd = addVerseViewModel::onAddVerseNumberAndText,
                    onDelete = addVerseViewModel::onDeleteLastVerseNumberAndText,
                    onSave = addVerseViewModel::addVerse,
                    onShowTag = { },
                    modifier = Modifier.imePadding()
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = { AppTopBar(onBackPress = onBackPress)}
        ) { paddingValues ->
            Content(
                book = uiState.book,
                chapter = uiState.chapter,
                verseNumberAndTextList = uiState.verseNumberAndTextList,
                onBookChanged = addVerseViewModel::onBookChanged,
                onChapterChanged = addVerseViewModel::onChapterChanged,
                onVerseNumberChanged = addVerseViewModel::onVerseNumberChanged,
                onVerseTextChanged = addVerseViewModel::onVerseTextChanged,
                onDeleteVerseNumberAndText = addVerseViewModel::onDeleteVerseNumberAndText,
                onAddTag = addVerseViewModel::onAddTag,
                onRemoveTag = addVerseViewModel::onRemoveTag,
                tags = uiState.tags,
                allTags = uiState.allTags,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun Content(
    book: String,
    chapter: String,
    verseNumberAndTextList: List<AddVerseViewModel.AddVerseNumberAndText>,
    onBookChanged: (String) -> Unit,
    onChapterChanged: (String) -> Unit,
    onVerseNumberChanged: (Int, String) -> Unit,
    onVerseTextChanged: (Int, String) -> Unit,
    onDeleteVerseNumberAndText: (Int) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    tags: List<String>,
    allTags: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        VerseForm(
            book = book,
            onBookChanged = onBookChanged,
            chapter = chapter,
            onChapterChanged = onChapterChanged,
            verseNumberAndTextList = verseNumberAndTextList,
            onVerseNumberChanged = onVerseNumberChanged,
            onVerseTextChanged = onVerseTextChanged,
            onDeleteVerseNumberAndText = onDeleteVerseNumberAndText,
            onAddTag = onAddTag,
            onRemoveTag = onRemoveTag,
            tags = tags,
            allTags = allTags
        )
    }
}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddVerseBottomBar(
    onAdd: () -> Unit,
    onDelete: () -> Unit,
    onSave: () -> Unit,
    onShowTag: () -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalFloatingToolbar(
        expanded = true,
        floatingActionButton = {
            FloatingToolbarDefaults.VibrantFloatingActionButton(onClick = onAdd) {
                Icon(
                    painter = painterResource(R.drawable.outline_add_24),
                    contentDescription = null
                )
            }
        },
        floatingActionButtonPosition = FloatingToolbarHorizontalFabPosition.End,
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
        modifier = modifier
    ) {
        IconButton(onClick = onDelete) {
            Icon(painter = painterResource(R.drawable.outline_delete_24), contentDescription = null)
        }
        IconButton(onClick = onShowTag) {
            Icon(
                painter = painterResource(R.drawable.outline_add_tag_24),
                contentDescription = null
            )
        }
        IconButton(onClick = onSave) {
            Icon(painter = painterResource(R.drawable.outline_save_24), contentDescription = null)
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
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    tags: List<String>,
    allTags: List<String>,
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
                modifier = Modifier.height(8.dp)
            )
        }

        item {
            Tags(
                modifier = Modifier.fillMaxWidth(),
                tags = tags,
                allTags = allTags,
                onAddTag = onAddTag,
                onRemoveTag = onRemoveTag,
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(8.dp)
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
                modifier = Modifier.height(8.dp)
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
    val hasChapterError = chapter.isNotEmpty() && (chapter.toIntOrNull() == null || (chapter.toIntOrNull() ?: -1) <= 0)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Tags(
    tags: List<String>,
    allTags: List<String>,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var emptyTagError by remember { mutableStateOf(value = false) }
    var expanded by remember { mutableStateOf(value = false) }
    val textFieldState = rememberTextFieldState()
    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                state = textFieldState,
                lineLimits = TextFieldLineLimits.SingleLine,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = emptyTagError,
                label = { Text(text = stringResource(R.string.label_add_tag)) },
                supportingText = if (emptyTagError) {
                    { Text(text = stringResource(R.string.error_empty_tag)) }
                } else {
                    null
                },
                trailingIcon = {
                    if (emptyTagError) {
                        Icon(
                            painter = painterResource(R.drawable.outline_error_24),
                            contentDescription = null
                        )
                    } else {
                        IconButton(
                            onClick = {
                                if (textFieldState.text.isEmpty()) {
                                    emptyTagError = true
                                } else {
                                    onAddTag(textFieldState.text.toString())
                                    textFieldState.clearText()
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_check_24),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )

            val filteredTags = allTags
                .filter { tag ->
                    tags.none { filterTag -> filterTag == tag }
                }
                .filter { tag ->
                    tag.contains(textFieldState.text, ignoreCase = true)
                }

            if (filteredTags.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.heightIn(max = 280.dp)
                ) {
                    filteredTags.forEach { tagSelection ->
                        DropdownMenuItem(
                            onClick = {
                                textFieldState.setTextAndPlaceCursorAtEnd(tagSelection)
                                expanded = false
                            },
                            text = { Text(text = tagSelection) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tags.forEach { tag ->
                InputChip(
                    selected = false,
                    onClick = { onRemoveTag(tag) },
                    label = { Text(text = tag) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.icon_x),
                            contentDescription = null,
                            modifier = Modifier
                                .size(InputChipDefaults.AvatarSize)
                        )
                    }
                )
            }
        }
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
    val currentVerseNumber = verseNumberAndText.verseNumber.toIntOrNull()
    val isError = verseNumberAndText.verseNumber.isNotEmpty() && (currentVerseNumber == null || (currentVerseNumber ?: -1) <= 0)

    Column(
        modifier = modifier.fillMaxWidth().imePadding(),
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
            isError = isError,
            supportingText = {
                if (isError) {
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