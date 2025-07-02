package com.yveltius.memorize.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yveltius.memorize.ui.components.AppScaffold
import com.yveltius.memorize.ui.dialogs.AddVerseFullscreenDialog
import com.yveltius.memorize.ui.text.buildAnnotatedVerse
import com.yveltius.memorize.viewmodels.VersesListViewModel
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import org.koin.androidx.compose.koinViewModel

@Composable
fun VerseListScreen(
    onAddVerse: () -> Unit,
    versesListViewModel: VersesListViewModel = koinViewModel()
) {
    val uiState by versesListViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { versesListViewModel.getVerses() }
    MainView(
        uiState = uiState,
        onFabClick = onAddVerse
    )
}

@Composable
fun MainView(
    uiState: VersesListViewModel.UiState,
    onFabClick: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            AnimatedVisibility(visible = lazyListState.lastScrolledBackward || !lazyListState.canScrollBackward) {
                FloatingActionButton(
                    onClick = onFabClick,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                }
            }
        }
    ) { contentPadding ->
        Content(
            verses = uiState.verses,
            contentPadding = contentPadding,
            lazyListState = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun Content(
    verses: List<Verse>,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun VerseView(
    verse: Verse,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "${verse.book} ${verse.chapter}:${verse.getVerseNumberString()}")
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}