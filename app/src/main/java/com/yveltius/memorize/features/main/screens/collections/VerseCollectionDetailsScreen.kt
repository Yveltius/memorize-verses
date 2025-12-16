package com.yveltius.memorize.features.main.screens.collections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yveltius.memorize.R
import com.yveltius.memorize.features.main.viewmodels.collections.VerseCollectionDetailsViewModel
import com.yveltius.memorize.ui.components.BackButton
import com.yveltius.memorize.ui.components.FailedToLoad
import com.yveltius.memorize.ui.components.Loading
import com.yveltius.memorize.ui.components.SectionHeader
import com.yveltius.memorize.ui.text.buildAnnotatedVerse
import com.yveltius.memorize.ui.theme.AppTheme
import com.yveltius.versememorization.entity.collections.VerseCollection
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText

@Composable
fun VerseCollectionDetailsScreen(
    onBackPress: () -> Unit,
    onEditCollection: (String) -> Unit,
    verseCollectionName: String,
    verseCollectionDetailsViewModel: VerseCollectionDetailsViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        verseCollectionDetailsViewModel.getCollection(collectionName = verseCollectionName)
    }

    val uiState by verseCollectionDetailsViewModel.uiState.collectAsState()

    AppTheme {
        when (uiState) {
            is VerseCollectionDetailsViewModel.UiState.Content -> {
                Content(
                    verseCollection = (uiState as VerseCollectionDetailsViewModel.UiState.Content).verseCollection,
                    onEditCollection = { onEditCollection(verseCollectionName) },
                    onBackPress = onBackPress
                )
            }

            VerseCollectionDetailsViewModel.UiState.FailedToLoadVerseCollection -> {
                FailedToLoad(
                    retryMessage = stringResource(R.string.collection_details_failed_to_get_collection, verseCollectionName),
                    onRetry = { verseCollectionDetailsViewModel.getCollection(collectionName = verseCollectionName) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            VerseCollectionDetailsViewModel.UiState.Loading -> {
                Loading(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun Content(
    verseCollection: VerseCollection,
    onEditCollection: () -> Unit,
    onBackPress: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                collectionName = verseCollection.name,
                onBackPress = onBackPress
            )
        },
        floatingActionButton = {
            EditFAB(
                onEditCollection = onEditCollection
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader(
                text = stringResource(R.string.section_header_verses)
            )

            if (verseCollection.verses.isNotEmpty()) {
                verseCollection.verses.forEachIndexed { index, verse ->
                    VerseView(
                        verse = verse,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )

                    if (index < (verseCollection.verses.size - 1)) {
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.collection_details_no_verses_in_collection),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
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
        navigationIcon = { BackButton(onBackPress = onBackPress) },
    )
}

@Composable
private fun EditFAB(onEditCollection: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onEditCollection
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_edit),
            contentDescription = stringResource(R.string.content_description_edit_collection)
        )
    }
}

@Composable
private fun VerseView(
    verse: Verse,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = verse.getVerseString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
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
    }
}

@Composable
private fun FailedToLoadVerseCollection(
    verseCollectionName: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier.align(alignment = Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(
                    R.string.collection_details_failed_to_get_collection,
                    verseCollectionName
                )
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.collection_details_button_retry))
            }
        }
    }
}

@Preview
@Composable
private fun EmptyContentPreview() {
    AppTheme {
        Content(
            verseCollection = VerseCollection(
                name = "My Collection",
                verses = setOf()
            ),
            onEditCollection = {},
            onBackPress = {}
        )
    }
}

@Preview
@Composable
private fun FailedToLoadVerseCollectionPreview() {
    AppTheme {
        FailedToLoadVerseCollection(
            verseCollectionName = "My Collection",
            onRetry = {},
            modifier = Modifier.fillMaxSize()
        )
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
    tags = listOf("Discipleship Verses", "Obedience to Christ", "Worry", "Territory", "Rererepeat")
)

@Preview
@Composable
private fun VerseViewPreview() {
    VerseView(
        verse = verseForPreviews,
        modifier = Modifier.fillMaxWidth()
    )
}