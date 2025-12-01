package com.yveltius.memorize.features.choosenextword.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yveltius.memorize.R
import com.yveltius.memorize.ui.components.BackButton
import com.yveltius.memorize.ui.theme.AppTheme
import com.yveltius.memorize.features.choosenextword.viewmodels.ChooseNextWordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseNextWordScreen(
    onBackPress: () -> Unit,
    verseUUIDString: String,
    chooseNextWordViewModel: ChooseNextWordViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        chooseNextWordViewModel.getVerse(verseUUIDString = verseUUIDString)
    }

    val uiState by chooseNextWordViewModel.uiState.collectAsState()

    AppTheme {
        Scaffold(
            topBar = {
                TopBar(
                    titleText = uiState.verse?.getVerseString(),
                    onBackPress = onBackPress
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            AnimatedContent(
                targetState = uiState.showResults
            ) { showResults ->
                if (!showResults) {
                    PracticeContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState()),
                        currentGuessCount = uiState.currentGuessCount,
                        currentWordsStates = uiState.currentWordsStates,
                        currentVerse = uiState.currentVerse,
                        currentGuessIndex = uiState.currentGuessIndex,
                        lastGuessIncorrect = uiState.lastGuessIncorrect,
                        availableGuesses = uiState.availableGuesses,
                        onGuess = chooseNextWordViewModel::onGuess,
                        showNextButton = uiState.showNextButton,
                        showResultsButton = uiState.showResultsButton,
                        onGoNext = chooseNextWordViewModel::goNext,
                        onGoToResults = chooseNextWordViewModel::goToResults
                    )
                } else {
                    ResultsContent(
                        allWordsStates = uiState.allWordsStates,
                        guessCounts = uiState.guessCounts,
                        onComplete = {
                            chooseNextWordViewModel.onComplete()
                            onBackPress()
                        },
                        verse = uiState.verse!!, // this should never be null if you have already been in practice
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    titleText: String?,
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = {
            if (titleText != null) Text(text = titleText)
        },
        navigationIcon = {
            BackButton(
                onBackPress = onBackPress
            )
        }
    )
}
