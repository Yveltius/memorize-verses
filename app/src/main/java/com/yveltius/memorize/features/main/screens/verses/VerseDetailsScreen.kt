package com.yveltius.memorize.features.main.screens.verses

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yveltius.memorize.R
import com.yveltius.memorize.features.main.viewmodels.verses.VerseDetailsViewModel
import com.yveltius.memorize.ui.components.BackButton
import com.yveltius.memorize.ui.components.FailedToLoad
import com.yveltius.memorize.ui.components.Loading
import com.yveltius.memorize.ui.components.SectionHeader
import com.yveltius.memorize.ui.text.buildAnnotatedVerse
import com.yveltius.memorize.ui.theme.AppTheme
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import java.util.UUID

@Composable
fun VerseDetailsScreen(
    onBackPress: () -> Unit,
    onEditVerse: () -> Unit,
    onPracticeVerse: () -> Unit,
    verseUUID: UUID,
    verseDetailsViewModel: VerseDetailsViewModel = viewModel()
) {
    val uiState by verseDetailsViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        verseDetailsViewModel.getVerse(verseUUID = verseUUID)
    }

    AppTheme {
        when (uiState) {
            is VerseDetailsViewModel.UiState.Content -> {
                Content(
                    verse = (uiState as VerseDetailsViewModel.UiState.Content).verse,
                    onBackPress = onBackPress,
                    onEditVerse = onEditVerse,
                    onPracticeVerse = onPracticeVerse
                )
            }

            VerseDetailsViewModel.UiState.FailedToLoadVerse -> {
                FailedToLoad(
                    retryMessage = stringResource(R.string.verse_details_failed_to_load_verse),
                    onRetry = { verseDetailsViewModel.onRetry(verseUUID) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            VerseDetailsViewModel.UiState.Loading -> Loading(modifier = Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Content(
    verse: Verse,
    onBackPress: () -> Unit,
    onEditVerse: () -> Unit,
    onPracticeVerse: () -> Unit
) {
    Scaffold(
        topBar = { TopBar(title = verse.getVerseString(), onBackPress = onBackPress) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues = innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            VerseText(verse)

            Tags(verse = verse)

            Actions(
                onEditVerse = onEditVerse,
                onPracticeVerse = onPracticeVerse
            )
        }
    }
}

@Composable
private fun VerseText(verse: Verse) {
    SectionHeader(
        text = stringResource(R.string.verse_details_section_header_text),
        modifier = Modifier.fillMaxWidth()
    )

    Text(
        text = buildAnnotatedVerse(verseNumberAndTexts = verse.verseText),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
private fun Tags(verse: Verse) {
    SectionHeader(
        text = stringResource(R.string.verse_details_section_header_tags),
        modifier = Modifier.fillMaxWidth()
    )

    Text(
        text = if (verse.tags.isNotEmpty()) verse.tags.toString() else stringResource(R.string.verses_list_no_tags),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun Actions(onEditVerse: () -> Unit, onPracticeVerse: () -> Unit) {
    SectionHeader(
        text = stringResource(R.string.verse_details_section_header_actions),
        modifier = Modifier.fillMaxWidth()
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MediumSquareFilledTonalIconButton(
            onClick = {},
            iconResId = R.drawable.icon_trash,
            contentDescriptionResId = R.string.content_description_delete_verse
        )

        MediumSquareFilledTonalIconButton(
            onClick = onEditVerse,
            iconResId = R.drawable.icon_edit,
            contentDescriptionResId = R.string.content_description_edit_verse
        )

        MediumSquareFilledTonalIconButton(
            onClick = onPracticeVerse,
            iconResId = R.drawable.icon_practice,
            contentDescriptionResId = R.string.content_description_practice_verse
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun MediumSquareFilledTonalIconButton(
    onClick: () -> Unit,
    iconResId: Int,
    contentDescriptionResId: Int
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier.size(IconButtonDefaults.mediumContainerSize()),
        shape = IconButtonDefaults.mediumSquareShape
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = stringResource(contentDescriptionResId),
            modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    title: String,
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = { BackButton(onBackPress = onBackPress) }
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
private fun ContentPreview() {
    AppTheme {
        Content(
            verse = verseForPreviews,
            onBackPress = {},
            onPracticeVerse = {},
            onEditVerse = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DarkContentPreview() {
    AppTheme {
        Content(
            verse = verseForPreviews,
            onBackPress = {},
            onEditVerse = {},
            onPracticeVerse = {}
        )
    }
}