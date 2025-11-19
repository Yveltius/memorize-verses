package com.yveltius.memorize.features.addverse.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarHorizontalFabPosition
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yveltius.memorize.R
import com.yveltius.memorize.features.addverse.components.BookAndChapter
import com.yveltius.memorize.ui.components.AppTopBar
import com.yveltius.memorize.ui.text.buildAnnotatedVerse
import com.yveltius.memorize.ui.theme.AppTheme
import com.yveltius.memorize.features.addverse.viewmodels.AddVerseViewModel
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.util.UUID
import com.yveltius.memorize.features.addverse.components.EditableVerseNumber
import com.yveltius.memorize.features.addverse.components.EditableVerseText
import com.yveltius.memorize.features.addverse.components.Tags
import com.yveltius.memorize.ui.animation.AnimatedHeightCrossfade

@Composable
fun AddVerseScreen(
    onBackPress: () -> Unit,
    verseUUID: UUID? = null,
    addVerseViewModel: AddVerseViewModel = koinViewModel()
) {
    val uiState by addVerseViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showConfirmDeletionDialog by remember { mutableStateOf(value = false) }
    var indexOfVerseAndNumberSelectedForDeletion by remember { mutableIntStateOf(value = -1) }

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
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = { AppTopBar(onBackPress = onBackPress) }
        ) { paddingValues ->
            Content(
                book = uiState.book,
                chapter = uiState.chapter,
                verseNumberAndTextList = uiState.verseNumberAndTextList,
                indexBeingEdited = uiState.indexBeingEdited,
                onSelectForEdit = addVerseViewModel::onSelectForEdit,
                onBookChanged = addVerseViewModel::onBookChanged,
                onChapterChanged = addVerseViewModel::onChapterChanged,
                onVerseNumberChanged = addVerseViewModel::onVerseNumberChanged,
                onVerseTextChanged = addVerseViewModel::onVerseTextChanged,
                onDeleteVerseNumberAndText = {
                    indexOfVerseAndNumberSelectedForDeletion = it
                    showConfirmDeletionDialog = true
                },
                onAddTag = addVerseViewModel::onAddTag,
                onRemoveTag = addVerseViewModel::onRemoveTag,
                tags = uiState.tags,
                allTags = uiState.allTags,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )

            if (showConfirmDeletionDialog) {
                val verseStringForDialog = addVerseViewModel.getSnapshotOfVerse()
                    ?.getVerseString(index = indexOfVerseAndNumberSelectedForDeletion)

                if (verseStringForDialog != null) {
                    ConfirmDeletionDialog(
                        verseAndNumberName = verseStringForDialog,
                        onConfirmDelete = {
                            if (indexOfVerseAndNumberSelectedForDeletion >= 0) {
                                addVerseViewModel.onDeleteVerseNumberAndText(
                                    indexOfVerseAndNumberSelectedForDeletion
                                )
                            }
                            indexOfVerseAndNumberSelectedForDeletion = -1
                            showConfirmDeletionDialog = false
                        },
                        onDismissRequest = {
                            indexOfVerseAndNumberSelectedForDeletion = -1
                            showConfirmDeletionDialog = false
                        }
                    )
                } else {
                    indexOfVerseAndNumberSelectedForDeletion = -1
                    showConfirmDeletionDialog = false
                }
            }
        }
    }
}

@Composable
fun ConfirmDeletionDialog(
    verseAndNumberName: String,
    onConfirmDelete: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(
                    R.string.dialog_title_delete_verse_number_and_text,
                    verseAndNumberName
                )
            )
        },
        text = {
            Text(
                text = stringResource(
                    R.string.dialog_text_delete_verse_number_and_text,
                    verseAndNumberName
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmDelete) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun Content(
    book: String,
    chapter: String,
    verseNumberAndTextList: List<AddVerseViewModel.AddVerseNumberAndText>,
    indexBeingEdited: Int,
    onSelectForEdit: (Int) -> Unit,
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
            indexBeingEdited = indexBeingEdited,
            onSelectForEdit = onSelectForEdit,
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
    indexBeingEdited: Int,
    onSelectForEdit: (Int) -> Unit,
    tags: List<String>,
    allTags: List<String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
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

        itemsIndexed(
            items = verseNumberAndTextList,
            key = { index, verseNumberAndText -> verseNumberAndText.verseNumber }
        ) { index, verseNumberAndText ->

            AnimatedHeightCrossfade(
                targetState = indexBeingEdited,
                animationSpec = tween(durationMillis = 100)
            ) { targetState ->
                Box(
                    modifier = Modifier
                ) {
                    when (targetState) {
                        index -> {
                            EditableVerseNumberAndText(
                                index = index,
                                verseNumberAndText = verseNumberAndText,
                                onVerseNumberChanged = onVerseNumberChanged,
                                onVerseTextChanged = onVerseTextChanged,
                                onDeleteVerseNumberAndText = onDeleteVerseNumberAndText,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        else -> {
                            NoEditVerseNumberAndText(
                                index = index,
                                verseNumberAndText = verseNumberAndText,
                                onSelectForEdit = onSelectForEdit,
                                onDeleteVerseNumberAndText = { onDeleteVerseNumberAndText(index) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
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
    val isError = verseNumberAndText.verseNumber.isNotEmpty() && ((currentVerseNumber ?: -1) <= 0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnnotatedVerse(
                verseNumberAndText = verseNumberAndText.transform(),
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { onDeleteVerseNumberAndText(index) }) {
                Icon(
                    painter = painterResource(R.drawable.outline_delete_24),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        EditableVerseNumber(
            verseNumberAndText = verseNumberAndText,
            onVerseNumberChanged = onVerseNumberChanged,
            index = index,
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )

        EditableVerseText(
            verseNumberAndText = verseNumberAndText,
            onVerseTextChanged = onVerseTextChanged,
            index = index,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun NoEditVerseNumberAndText(
    index: Int,
    verseNumberAndText: AddVerseViewModel.AddVerseNumberAndText,
    onDeleteVerseNumberAndText: () -> Unit,
    onSelectForEdit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
            .clickable(onClick = { onSelectForEdit(index) }),
    ) {
        AnnotatedVerse(
            verseNumberAndText = verseNumberAndText.transform(),
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onDeleteVerseNumberAndText) {
            Icon(
                painter = painterResource(R.drawable.outline_delete_24),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun AnnotatedVerse(
    verseNumberAndText: VerseNumberAndText,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = buildAnnotatedVerse(listOf(verseNumberAndText)),
        modifier = modifier,
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines
    )
}

@Preview(showBackground = true)
@Composable
private fun ContentPreview() {
    Content(
        book = "John",
        chapter = "1",
        verseNumberAndTextList = listOf(
            AddVerseViewModel.AddVerseNumberAndText(
                verseNumber = "1",
                verseText = "In the beginning was the Word, and the Word was with God, and the Word was God."
            )
        ),
        onBookChanged = { },
        onChapterChanged = {},
        onVerseNumberChanged = { index, num -> },
        onVerseTextChanged = { index, text -> },
        onDeleteVerseNumberAndText = {},
        indexBeingEdited = 0,
        onSelectForEdit = { index -> },
        onAddTag = {},
        onRemoveTag = {},
        tags = listOf(),
        allTags = listOf(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun SelectedEditVerse() {
    EditableVerseNumberAndText(
        index = 0,
        verseNumberAndText = AddVerseViewModel.AddVerseNumberAndText(
            verseNumber = "1",
            verseText = "In the beginning was the Word, and the Word was with God, and the Word was God."
        ),
        onVerseNumberChanged = { index, num -> },
        onVerseTextChanged = { index, text -> },
        onDeleteVerseNumberAndText = { index -> },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun NotSelectedEditVerse() {
    NoEditVerseNumberAndText(
        index = 0,
        verseNumberAndText = AddVerseViewModel.AddVerseNumberAndText(
            verseNumber = "1",
            verseText = "This book of the law shall not depart from your mouth, but you shall meditate on it day and night, so that you may be careful to do according to all that is written in it; for then you will make your way successful, and then you will be prosperous."
        ),
        onDeleteVerseNumberAndText = {},
        onSelectForEdit = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}