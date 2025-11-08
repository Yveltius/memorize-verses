package com.yveltius.memorize.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yveltius.memorize.R
import com.yveltius.memorize.ui.components.AppScaffold
import com.yveltius.memorize.ui.text.buildAnnotatedVerse
import com.yveltius.memorize.ui.theme.AppTheme
import com.yveltius.memorize.viewmodels.VersesListViewModel
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VerseListScreen(
    onAddVerse: () -> Unit,
    onEditVerse: (Verse) -> Unit,
    onGoToChooseNextWord: (Verse) -> Unit,
    versesListViewModel: VersesListViewModel = koinViewModel()
) {
    val uiState by versesListViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { versesListViewModel.getVerses() }
    MainView(
        uiState = uiState,
        onEdit = onEditVerse,
        onFabClick = onAddVerse,
        onDeleteConfirmed = versesListViewModel::removeVerse,
        onGoToChooseNextWord = onGoToChooseNextWord,
    )
}

@Composable
fun MainView(
    uiState: VersesListViewModel.UiState,
    onFabClick: () -> Unit,
    onEdit: (Verse) -> Unit,
    onDeleteConfirmed: (Verse) -> Unit,
    onGoToChooseNextWord: (Verse) -> Unit,
) {
    var showDeletePrompt by remember { mutableStateOf(value = false) }
    var verseToBeDeleted: Verse? by remember { mutableStateOf(value = null) }

    val lazyListState = rememberLazyListState()
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                AnimatedVisibility(visible = lazyListState.lastScrolledBackward || !lazyListState.canScrollBackward) {
                    FloatingActionButton(
                        onClick = onFabClick,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_add_24),
                            contentDescription = null
                        )
                    }
                }
            }
        ) { contentPadding ->
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                Content(
                    verses = uiState.verses,
                    contentPadding = contentPadding,
                    lazyListState = lazyListState,
                    onEdit = onEdit,
                    onShowDeletePrompt = {
                        verseToBeDeleted = it
                        showDeletePrompt = true
                    },
                    onGoToChooseNextWord = onGoToChooseNextWord,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                if (showDeletePrompt) {
                    DeleteVerseAlertDialog(
                        onDismissRequest = {
                            verseToBeDeleted = null
                            showDeletePrompt = false
                        },
                        onConfirmRequest = { verse ->
                            onDeleteConfirmed(verse)
                            showDeletePrompt = false
                        },
                        verseToBeDeleted = verseToBeDeleted
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteVerseAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: (Verse) -> Unit,
    verseToBeDeleted: Verse?,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmRequest(verseToBeDeleted!!) }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        text = {
            Text(
                text = stringResource(
                    id = R.string.dialog_delete_verse_description,
                    verseToBeDeleted?.getVerseString()
                        ?: stringResource(R.string.dialog_delete_verse_description)
                )
            )
        }
    )
}

@Composable
fun Content(
    verses: List<Verse>,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    onEdit: (Verse) -> Unit,
    onShowDeletePrompt: (Verse) -> Unit,
    onGoToChooseNextWord: (Verse) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = lazyListState
    ) {
        items(verses) { verse ->
            VerseView(
                verse = verse,
                onEdit = onEdit,
                onShowDeletePrompt = onShowDeletePrompt,
                onGoToChooseNextWord = onGoToChooseNextWord,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun VerseView(
    verse: Verse,
    onEdit: (Verse) -> Unit,
    onShowDeletePrompt: (Verse) -> Unit,
    onGoToChooseNextWord: (Verse) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(value = false) }
    Card(
        modifier = modifier.clickable(onClick = { onGoToChooseNextWord(verse) })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = verse.getVerseString())
                IconButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    VerseDropdownMenu(
                        verse = verse,
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        onEdit = {
                            onEdit(verse)
                            expanded = false
                        },
                        onShowDeletePrompt = {
                            onShowDeletePrompt(verse)
                            expanded = false
                        }
                    )
                }
            }
            Text(
                text = buildAnnotatedVerse(verseNumberAndTexts = verse.verseText),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = if (verse.tags.isNotEmpty()) verse.tags.toString() else "No Tags"/*verse.getFormattedTags()*/,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .align(Alignment.End),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun VerseDropdownMenu(
    verse: Verse,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onEdit: (Verse) -> Unit,
    onShowDeletePrompt: (Verse) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.edit)) },
                onClick = { onEdit(verse) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_edit_24),
                        contentDescription = null
                    )
                }
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.delete)) },
                onClick = { onShowDeletePrompt(verse) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_delete_24),
                        contentDescription = null
                    )
                }
            )
        }
        Icon(
            painter = painterResource(R.drawable.outline_more_vert_24),
            contentDescription = null
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun VerseViewPreviewLight() {
    VerseView(
        verse = Verse(
            book = "Romans",
            chapter = 12,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 1,
                    text = "Therefore, brothers, I urge you, by the mercies of God, to present your bodies as a living sacrifice - holy and pleasing to God. This is your spiritual worship."
                ),
                VerseNumberAndText(
                    verseNumber = 2,
                    text = "Do not be conformed to this age, but be transformed by the renewing of your mind, so that you may discern what is the good, please, and perfect will of God."
                )
            ),
            tags = listOf("Discipleship Verse", "Obedience to Christ", "Romans")
        ),
        onEdit = {},
        onShowDeletePrompt = {},
        onGoToChooseNextWord = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun VerseViewPreviewDark() {
    VerseView(
        verse = Verse(
            book = "Romans",
            chapter = 12,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 1,
                    text = "Therefore, brothers, I urge you, by the mercies of God, to present your bodies as a living sacrifice - holy and pleasing to God. This is your spiritual worship."
                ),
                VerseNumberAndText(
                    verseNumber = 2,
                    text = "Do not be conformed to this age, but be transformed by the renewing of your mind, so that you may discern what is the good, please, and perfect will of God."
                )
            ),
            tags = listOf("Discipleship Verse", "Obedience to Christ", "Romans")
        ),
        onEdit = {},
        onShowDeletePrompt = {},
        onGoToChooseNextWord = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun VerseViewPreviewDarkNoTags() {
    VerseView(
        verse = Verse(
            book = "Romans",
            chapter = 12,
            verseText = listOf(
                VerseNumberAndText(
                    verseNumber = 1,
                    text = "Therefore, brothers, I urge you, by the mercies of God, to present your bodies as a living sacrifice - holy and pleasing to God. This is your spiritual worship."
                ),
                VerseNumberAndText(
                    verseNumber = 2,
                    text = "Do not be conformed to this age, but be transformed by the renewing of your mind, so that you may discern what is the good, please, and perfect will of God."
                )
            ),
            tags = listOf()
        ),
        onEdit = {},
        onShowDeletePrompt = {},
        onGoToChooseNextWord = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}