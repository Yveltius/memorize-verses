package com.yveltius.memorize.features.verselist.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yveltius.memorize.R
import com.yveltius.memorize.features.verselist.components.VersesTopSearchBar
import com.yveltius.memorize.features.verselist.viewmodels.VersesListViewModel
import com.yveltius.memorize.ui.components.SectionHeader
import com.yveltius.memorize.ui.text.buildAnnotatedVerse
import com.yveltius.memorize.ui.theme.AppTheme
import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import java.util.UUID

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VerseListScreen(
    onAddVerse: () -> Unit,
    onEditVerse: (Verse) -> Unit,
    onGoToChooseNextWord: (Verse) -> Unit,
    onGoToSettings: () -> Unit,
    versesListViewModel: VersesListViewModel = viewModel()
) {
    val uiState by versesListViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // having this here means the verses will be fetched again when returning from add/edit
        versesListViewModel.getVerses()
        versesListViewModel.getCollections()
    }

    // todo the way I did this screen isn't quite right, need to abstract correctly
    //  and not directly pass UiState object
    RootView(
        uiState = uiState,
        onEdit = onEditVerse,
        onFabClick = onAddVerse,
        onQueryChanged = versesListViewModel::onQueryChanged,
        onDeleteConfirmed = versesListViewModel::removeVerse,
        onGoToChooseNextWord = onGoToChooseNextWord,
        onGoToSettings = onGoToSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RootView(
    uiState: VersesListViewModel.UiState,
    onFabClick: () -> Unit,
    onEdit: (Verse) -> Unit,
    onQueryChanged: (String) -> Unit,
    onDeleteConfirmed: (Verse) -> Unit,
    onGoToChooseNextWord: (Verse) -> Unit,
    onGoToSettings: () -> Unit,
) {
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    var showDeletePrompt by remember { mutableStateOf(value = false) }
    var verseToBeDeleted: Verse? by remember { mutableStateOf(value = null) }

    val lazyListState = rememberLazyListState()

    AppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                VersesTopSearchBar(
                    query = uiState.query,
                    onQueryChanged = onQueryChanged,
                    searchResults = uiState.searchResults,
                    scrollBehavior = scrollBehavior,
                    allVerses = uiState.verses,
                    lazyListState = lazyListState
                )
            },
            floatingActionButton = {
                AddVerseFAB(
                    lazyListState = lazyListState,
                    onFabClick = onFabClick
                )
            }
        ) { contentPadding ->
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

@Composable
private fun AddVerseFAB(
    lazyListState: LazyListState,
    onFabClick: () -> Unit
) {
    AnimatedVisibility(
        visible = lazyListState.lastScrolledBackward
                || !lazyListState.canScrollBackward
    ) {
        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_add_24),
                contentDescription = stringResource(R.string.content_description_add_verse)
            )
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
private fun Content(
    verses: List<Verse>,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    onEdit: (Verse) -> Unit,
    onShowDeletePrompt: (Verse) -> Unit,
    onGoToChooseNextWord: (Verse) -> Unit,
    modifier: Modifier = Modifier
) {
    if (verses.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.no_verses),
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = contentPadding,
            state = lazyListState
        ) {
            item {
                SectionHeader(
                    text = stringResource(R.string.verses_list_verses_section_header),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            itemsIndexed(verses) { index, verse ->
                VerseView(
                    verse = verse,
                    onEdit = onEdit,
                    onShowDeletePrompt = onShowDeletePrompt,
                    onGoToChooseNextWord = onGoToChooseNextWord,
                    modifier = Modifier.fillMaxWidth()
                )

                if (index < (verses.size - 1)) {
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun VerseView(
    verse: Verse,
    onEdit: (Verse) -> Unit,
    onShowDeletePrompt: (Verse) -> Unit,
    onGoToChooseNextWord: (Verse) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(value = false) }

    Row(
        modifier = modifier.clickable(onClick = { onGoToChooseNextWord(verse) }),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = verse.getVerseString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                // TODO this functionality needs to be in the Verse Details Screen
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
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (verse.tags.isNotEmpty()) verse.tags.toString() else "No Tags",
                modifier = Modifier
                    .fillMaxWidth(),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Icon(
            painter = painterResource(R.drawable.icon_arrow_right),
            contentDescription = null,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
        )
    }
}

@Composable
private fun VerseDropdownMenu(
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

@Composable
private fun CollectionView(
    verseCollection: VerseCollection,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = verseCollection.name)

            Text(
                text = pluralStringResource(
                    id = R.plurals.verses_in_collection,
                    count = verseCollection.verses.size,
                    verseCollection.verses.size
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

private val verseForPreviews = Verse(
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
)

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun VerseViewPreviewLight() {
    VerseView(
        verse = verseForPreviews,
        onEdit = {},
        onShowDeletePrompt = {},
        onGoToChooseNextWord = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun VerseViewPreviewDark() {
    AppTheme {
        VerseView(
            verse = verseForPreviews,
            onEdit = {},
            onShowDeletePrompt = {},
            onGoToChooseNextWord = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun CollectionViewDark() {
    AppTheme {
        CollectionView(
            verseCollection = VerseCollection(
                name = "My Collection",
                verses = setOf(verseForPreviews, verseForPreviews.copy(uuid = UUID.randomUUID())),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun CollectionViewDarkWithALotOfVerses() {
    AppTheme {
        CollectionView(
            verseCollection = VerseCollection(
                name = "My Collection",
                verses = List(size = 100) { index -> verseForPreviews.copy(uuid = UUID.randomUUID()) }.toSet(),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}