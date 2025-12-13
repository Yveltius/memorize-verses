package com.yveltius.memorize.features.main.screens.collections

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yveltius.memorize.R
import com.yveltius.memorize.features.main.viewmodels.collections.VerseCollectionEditViewModel
import com.yveltius.memorize.ui.components.BackButton
import com.yveltius.memorize.ui.components.SectionHeader
import com.yveltius.memorize.ui.text.buildAnnotatedVerse
import com.yveltius.memorize.ui.theme.AppTheme
import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText

@Composable
fun VerseCollectionEditScreen(
    onBackPress: () -> Unit,
    collectionName: String,
    verseCollectionEditViewModel: VerseCollectionEditViewModel = viewModel()
) {
    LaunchedEffect(Unit) { verseCollectionEditViewModel.getVerseCollection(collectionName) }
    val uiState by verseCollectionEditViewModel.uiState.collectAsState()

    when (uiState) {
        is VerseCollectionEditViewModel.UiState.Content -> {
            val state = uiState as VerseCollectionEditViewModel.UiState.Content
            RootView(
                verseCollection = state.verseCollection,
                versesNotInCollection = state.versesNotInCollection,
                onAddVerseToCollection = { verse ->
                    verseCollectionEditViewModel.onAddVerseToCollection(
                        collectionName = state.verseCollection.name,
                        verse = verse
                    )
                },
                onRemoveVerseFromCollection = { verse ->
                    verseCollectionEditViewModel.onRemoveVerseFromCollection(
                        collectionName = state.verseCollection.name,
                        verse = verse
                    )
                },
                onBackPress = onBackPress,
                modifier = Modifier.fillMaxSize()
            )
        }

        VerseCollectionEditViewModel.UiState.FailedToLoadVerseCollection -> {}
        VerseCollectionEditViewModel.UiState.Loading -> {}
    }
}

@Composable
fun RootView(
    verseCollection: VerseCollection,
    versesNotInCollection: List<Verse>,
    onAddVerseToCollection: (Verse) -> Unit,
    onRemoveVerseFromCollection: (Verse) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppTheme {
        Scaffold(
            topBar = {
                TopBar(
                    collectionName = verseCollection.name,
                    onBackPress = onBackPress
                )
            },
            modifier = modifier
        ) { innerPadding ->
            Content(
                versesInCollection = verseCollection.verses.toList(),
                versesNotInCollection = versesNotInCollection,
                onAddVerseToCollection = onAddVerseToCollection,
                onRemoveVerseFromCollection = onRemoveVerseFromCollection,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    collectionName: String,
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = { Text(text = collectionName) },
        navigationIcon = {
            BackButton(onBackPress = onBackPress)
        }
    )
}

@Composable
private fun Content(
    versesInCollection: List<Verse>,
    versesNotInCollection: List<Verse>,
    onAddVerseToCollection: (Verse) -> Unit,
    onRemoveVerseFromCollection: (Verse) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (versesInCollection.isNotEmpty()) {
            item(key = "SectionHeaderInCollection") {
                SectionHeader(
                    text = stringResource(R.string.edit_collection_section_header_in_collection),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }

            items(items = versesInCollection, key = { verse -> verse.uuid }) { verse ->
                VerseView(
                    verse = verse,
                    isInCollection = true,
                    onSelectedAction = onRemoveVerseFromCollection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }
        }

        if (versesNotInCollection.isNotEmpty()) {
            item(key = "SectionHeaderNotInCollection") {
                SectionHeader(
                    text = stringResource(R.string.edit_collection_section_header_not_in_collection),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }

            items(items = versesNotInCollection, key = { verse -> verse.uuid }) { verse ->
                VerseView(
                    verse = verse,
                    isInCollection = false,
                    onSelectedAction = onAddVerseToCollection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }
        }
    }
}

@Composable
private fun VerseView(
    verse: Verse,
    isInCollection: Boolean,
    onSelectedAction: (Verse) -> Unit,
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(value = defaultExpanded) }
    val expandedIconResId = if (expanded) R.drawable.icon_collapse else R.drawable.icon_expand

    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .background(
                    color = if (expanded) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface,
                    shape = if (expanded) RoundedCornerShape(size = 16.dp) else RectangleShape
                )
                .clickable(
                    onClick = { expanded = !expanded },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VerseString(verse, expanded)
                VerseSelectedIconButton(
                    expanded = expanded,
                    onSelectedAction = onSelectedAction,
                    verse = verse,
                    isInCollection = isInCollection
                )

                Icon(painter = painterResource(expandedIconResId), contentDescription = null)
            }
            VerseText(expanded = expanded, verse = verse)
        }
    }
}

@Composable
private fun VerseText(
    expanded: Boolean,
    verse: Verse
) {
    AnimatedContent(
        targetState = expanded
    ) { targetState ->
        when (targetState) {
            true -> {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Text(
                    text = buildAnnotatedVerse(verseNumberAndTexts = verse.verseText),
                    modifier = Modifier.padding(16.dp)
                )
            }

            false -> {}
        }
    }
}

@Composable
private fun VerseSelectedIconButton(
    expanded: Boolean,
    onSelectedAction: (Verse) -> Unit,
    verse: Verse,
    isInCollection: Boolean
) {
    val selectedVerseActionIconResId = if (isInCollection) {
        R.drawable.icon_minus
    } else {
        R.drawable.icon_plus
    }

    val selectedVerseActionContentDescription = if (isInCollection) {
        stringResource(R.string.content_description_remove_from_collection)
    } else {
        stringResource(
            R.string.content_description_add_to_collection
        )
    }

    Crossfade(
        targetState = expanded
    ) { targetState ->
        when (targetState) {
            true -> {
                FilledTonalIconButton(onClick = { onSelectedAction(verse) }) {
                    Icon(
                        painter = painterResource(selectedVerseActionIconResId),
                        contentDescription = null,
                    )
                }
            }

            false -> {
                IconButton(onClick = { onSelectedAction(verse) }) {
                    Icon(
                        painter = painterResource(selectedVerseActionIconResId),
                        contentDescription = selectedVerseActionContentDescription,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.VerseString(
    verse: Verse,
    expanded: Boolean
) {
    Text(
        text = verse.getVerseString(),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = if (expanded) FontWeight.SemiBold else FontWeight.Normal,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f)
    )
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
    tags = listOf("Discipleship Verses", "Obedience to Christ", "Worry", "Territory", "Rererepeat")
)

@Preview
@Composable
private fun InCollectionVerseView() {
    VerseView(
        verse = verseForPreviews,
        isInCollection = true,
        onSelectedAction = {},
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun NotInCollectionVerseView() {
    VerseView(
        verse = verseForPreviews,
        isInCollection = false,
        onSelectedAction = {},
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun VerseViewExpanded() {
    VerseView(
        verse = verseForPreviews,
        isInCollection = true,
        onSelectedAction = {},
        modifier = Modifier.fillMaxWidth(),
        defaultExpanded = true
    )
}