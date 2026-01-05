package com.yveltius.memorize.features.main.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yveltius.memorize.R
import com.yveltius.memorize.features.main.components.VersesTopSearchBar
import com.yveltius.memorize.features.main.viewmodels.MainViewModel
import com.yveltius.memorize.ui.components.SectionHeader
import com.yveltius.memorize.ui.text.buildAnnotatedVerse
import com.yveltius.memorize.ui.theme.AppTheme
import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import com.yveltius.versememorization.entity.versesearch.SearchResult
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun MainScreen(
    onAddVerse: () -> Unit,
    onVerseCollectionSelected: (String) -> Unit,
    onGoToVerseDetails: (Verse) -> Unit,
    onGoToSettings: () -> Unit,
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by mainViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // having this here means the verses will be fetched again when returning from add/edit
        mainViewModel.getVerses()
        mainViewModel.getCollections()
    }

    // todo the way I did this screen isn't quite right, need to abstract correctly
    //  and not directly pass UiState object
    RootView(
        uiState = uiState,
        onAddVerse = onAddVerse,
        onQueryChanged = mainViewModel::onQueryChanged,
        onDeleteConfirmed = mainViewModel::removeVerse,
        onVerseCollectionSelected = onVerseCollectionSelected,
        onAddCollection = mainViewModel::onAddCollection,
        onGoToVerseDetails = onGoToVerseDetails,
        onGoToSettings = onGoToSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RootView(
    uiState: MainViewModel.UiState,
    onAddVerse: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onVerseCollectionSelected: (String) -> Unit,
    onAddCollection: (String) -> Unit,
    onDeleteConfirmed: (Verse) -> Unit,
    onGoToVerseDetails: (Verse) -> Unit,
    onGoToSettings: () -> Unit,
) {
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    var showAddCollectionDialog by remember { mutableStateOf(value = false) }

    var showDeletePrompt by remember { mutableStateOf(value = false) }
    var verseToBeDeleted: Verse? by remember { mutableStateOf(value = null) }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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
                    onGoToSettings = onGoToSettings,
                    scrollToItem = { searchResult ->
                        // not sure of the better way to handle this atm
                        when (searchResult) {
                            is SearchResult.CollectionSearchResult -> {
                                coroutineScope.launch {
                                    lazyListState.animateScrollToItem(
                                        index = uiState.collections.indexOf(searchResult.verseCollection)
                                                + 2 // headers and spacers
                                    )
                                }
                            }

                            is SearchResult.VerseSearchResult -> {
                                coroutineScope.launch {
                                    // the +2 is for the section headers
                                    lazyListState.animateScrollToItem(
                                        index = uiState.collections.size
                                                + uiState.verses.indexOf(searchResult.verse)
                                                + 4 // headers and spacers
                                    )
                                }
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                AddFAB(
                    lazyListState = lazyListState,
                    onAddVerse = onAddVerse,
                    onAddCollection = { showAddCollectionDialog = true }
                )
            },
        ) { contentPadding ->
            Content(
                verses = uiState.verses,
                collections = uiState.collections,
                contentPadding = contentPadding,
                lazyListState = lazyListState,
                onVerseCollectionSelected = onVerseCollectionSelected,
                onGoToVerseDetails = onGoToVerseDetails,
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
            } else if (showAddCollectionDialog) {
                AddCollectionAlertDialog(
                    onDismissRequest = { showAddCollectionDialog = false },
                    onConfirmRequest = onAddCollection
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddFAB(
    lazyListState: LazyListState,
    onAddVerse: () -> Unit,
    onAddCollection: () -> Unit
) {
    val closeIcon = painterResource(R.drawable.icon_x)
    val addIcon = painterResource(R.drawable.icon_plus)

    val fabVisible by remember {
        derivedStateOf {
            lazyListState.lastScrolledBackward
                    || !lazyListState.canScrollBackward
                    || !lazyListState.canScrollForward
        }
    }
    val focusRequester = remember { FocusRequester() }
    var expanded by remember { mutableStateOf(value = false) }
    BackHandler(expanded) { expanded = false }

    FloatingActionButtonMenu(
        modifier = Modifier.offset(x = 16.dp, y = 16.dp),
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                modifier = Modifier
                    .animateFloatingActionButton(
                        visible = fabVisible || expanded,
                        alignment = Alignment.BottomEnd
                    )
                    .focusRequester(focusRequester),
                checked = expanded,
                onCheckedChange = { expanded = !expanded }
            ) {
                val painter by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) closeIcon else addIcon
                    }
                }
                Icon(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.animateIcon(checkedProgress = { checkedProgress })
                )
            }
        },
    ) {
        FloatingActionButtonMenuItem(
            onClick = {
                expanded = false
                onAddCollection()
            },
            text = { Text(text = stringResource(R.string.verses_list_fab_menu_add_collection)) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.icon_collection),
                    contentDescription = null
                )
            }
        )

        FloatingActionButtonMenuItem(
            onClick = {
                expanded = false
                onAddVerse()
            },
            text = { Text(text = stringResource(R.string.verses_list_fab_menu_add_verse)) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.icon_text),
                    contentDescription = null
                )
            }
        )
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
private fun AddCollectionAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var collectionName by remember { mutableStateOf(value = "") }

    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .sizeIn(minWidth = 280.dp, maxWidth = 560.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(text = stringResource(R.string.dialog_title_add_collection))

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )

                OutlinedTextField(
                    value = collectionName,
                    onValueChange = { collectionName = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.End),
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }

                    TextButton(
                        onClick = {
                            onConfirmRequest(collectionName)
                            onDismissRequest()
                        }
                    ) {
                        Text(text = stringResource(R.string.add))
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun Content(
    verses: List<Verse>,
    collections: List<VerseCollection>,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    onVerseCollectionSelected: (String) -> Unit,
    onGoToVerseDetails: (Verse) -> Unit,
    modifier: Modifier = Modifier
) {
    if (verses.isEmpty() && collections.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.verses_list_no_verses_or_collections),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(alignment = Alignment.Center)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = contentPadding,
            state = lazyListState
        ) {
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
            }
            item {
                SectionHeader(
                    text = stringResource(R.string.section_header_collections),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (collections.isNotEmpty()) {
                // use a VerticalGrid for larger screens
                itemsIndexed(
                    items = collections,
                    key = { _, collection -> collection.name }) { index, collection ->
                    CollectionView(
                        verseCollection = collection,
                        onVerseCollectionSelected = onVerseCollectionSelected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                item {
                    Text(
                        text = stringResource(R.string.verses_list_no_collections),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
            }
            item {
                SectionHeader(
                    text = stringResource(R.string.section_header_verses),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (verses.isNotEmpty()) {
                itemsIndexed(
                    items = verses,
                    key = { _, verse -> verse.getVerseString() }
                ) { index, verse ->
                    VerseView(
                        verse = verse,
                        onGoToVerseDetails = onGoToVerseDetails,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (index < (verses.size - 1)) {
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    }
                }
            } else {
                item {
                    Text(
                        text = stringResource(R.string.verses_list_no_verses),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VerseView(
    verse: Verse,
    onGoToVerseDetails: (Verse) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable(onClick = { onGoToVerseDetails(verse) }),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = verse.getVerseString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = buildAnnotatedVerse(verseNumberAndTexts = verse.verseText),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (verse.tags.isNotEmpty()) verse.tags.toString() else stringResource(R.string.verses_list_no_tags),
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CollectionView(
    verseCollection: VerseCollection,
    onVerseCollectionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.clickable(onClick = { onVerseCollectionSelected(verseCollection.name) }),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = verseCollection.name,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = when (verseCollection.verses.size) {
                    0 -> stringResource(R.string.verses_list_no_verses_in_collection)
                    1 -> stringResource(R.string.verses_list_one_verse_in_collection)
                    else -> stringResource(
                        R.string.verses_list_multiple_verses_in_collection,
                        verseCollection.verses.size
                    )
                },
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { onVerseCollectionSelected(verseCollection.name) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(R.string.main_screen_button_collection_view_details))
                }
            }
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
        onGoToVerseDetails = {},
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
            onGoToVerseDetails = {},
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
                verses = setOf(verseForPreviews),
            ),
            onVerseCollectionSelected = {},
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
            onVerseCollectionSelected = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}