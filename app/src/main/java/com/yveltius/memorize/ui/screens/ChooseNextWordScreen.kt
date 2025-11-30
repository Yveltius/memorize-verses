package com.yveltius.memorize.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yveltius.memorize.ui.theme.errorDark
import com.yveltius.memorize.ui.theme.errorLight
import com.yveltius.memorize.ui.theme.onSurfaceDark
import com.yveltius.memorize.ui.theme.onSurfaceLight
import com.yveltius.memorize.R
import com.yveltius.memorize.ui.components.BackButton
import com.yveltius.memorize.ui.theme.AppTheme
import com.yveltius.memorize.viewmodels.ChooseNextWordViewModel

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                CorrectGuessIndicator(
                    currentGuessCount = uiState.currentGuessCount,
                    currentWords = uiState.currentWordsStates,
                    modifier = Modifier.fillMaxWidth()
                )

                WordBlanksArea(
                    currentVerse = uiState.currentVerse
                        ?: stringResource(R.string.encountered_error_for_verse_string),
                    currentWords = uiState.currentWordsStates,
                    currentGuessIndex = uiState.currentGuessIndex,
                    lastGuessIncorrect = uiState.lastGuessIncorrect,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                AvailableGuesses(
                    availableGuesses = uiState.availableGuesses,
                    onGuess = chooseNextWordViewModel::onGuess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                if (uiState.showNextButton) {
                    GoNextButton(
                        onGoNext = chooseNextWordViewModel::goNext,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                } else if (uiState.showFinishButton) {
                    CompleteButton(
                        onComplete = {
                            chooseNextWordViewModel.onComplete()
                            onBackPress()
                        },
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

@Composable
private fun WordBlanks(
    currentWordsAndPunctuation: List<ChooseNextWordViewModel.WordGuessState>,
    currentGuessIndex: Int,
    lastGuessIncorrect: Boolean,
    modifier: Modifier = Modifier
) {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val underlineColor = if (isSystemInDarkTheme) onSurfaceDark else onSurfaceLight
    val errorUnderlineColor = if (isSystemInDarkTheme) errorDark else errorLight
    val textSize = remember { 16.sp }
    FlowRow(
        modifier = modifier.fillMaxWidth()
    ) {
        currentWordsAndPunctuation.forEachIndexed { index, wordGuessState ->
            when {
                shouldShowBlank(wordGuessState) -> {
                    Text(
                        text = wordGuessState.string.toEmptySpaces(),
                        fontSize = textSize,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .drawBehind {
                                val strokeWidthPx = 1.dp.toPx()
                                val yOffset = size.height - 8.dp.toPx()

                                drawLine(
                                    color = if (index == currentGuessIndex && lastGuessIncorrect) errorUnderlineColor else underlineColor,
                                    strokeWidth = strokeWidthPx,
                                    start = Offset(0f, yOffset),
                                    end = Offset(size.width, yOffset)
                                )
                            }
                    )
                }

                else -> {
                    Text(
                        text = wordGuessState.string,
                        fontSize = textSize,
                        modifier = Modifier.padding(
                            start = if (!wordGuessState.isGuessable && !wordGuessState.string.matches(
                                    regex = "[(\\[]".toRegex()
                                )
                            ) 1.dp else 4.dp
                        )
                    )
                }
            }
        }
    }
}

private fun shouldShowBlank(wordGuessState: ChooseNextWordViewModel.WordGuessState): Boolean {
    return wordGuessState.isGuessable && !wordGuessState.isGuessed
}

@Composable
private fun AvailableGuesses(
    availableGuesses: List<ChooseNextWordViewModel.WordGuessState>,
    onGuess: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        availableGuesses.forEach {
            Button(onClick = { onGuess(it.string) }) {
                Text(text = it.string)
            }
        }
    }
}

private fun String.toEmptySpaces(): String {
    val stringBuilder = StringBuilder()

    repeat(this.length) {
        stringBuilder.append("  ")
    }
    return stringBuilder.toString()
}

@Composable
private fun CorrectGuessIndicator(
    currentGuessCount: Int,
    currentWords: List<ChooseNextWordViewModel.WordGuessState>,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (currentGuessCount > 0) {
            currentWords.count { it.isGuessed }.toFloat()
                .div(currentGuessCount.toFloat())
        } else {
            1f
        },
        animationSpec = tween(durationMillis = 300)
    )

    LinearProgressIndicator(
        progress = {
            animatedProgress
        },
        trackColor = MaterialTheme.colorScheme.error,
        strokeCap = StrokeCap.Square,
        modifier = modifier
    )
}

@Composable
private fun WordBlanksArea(
    currentVerse: String,
    currentWords: List<ChooseNextWordViewModel.WordGuessState>,
    currentGuessIndex: Int,
    lastGuessIncorrect: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = currentVerse,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )

        WordBlanks(
            currentWordsAndPunctuation = currentWords,
            currentGuessIndex = currentGuessIndex,
            lastGuessIncorrect = lastGuessIncorrect,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
private fun GoNextButton(
    onGoNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        FilledTonalIconButton(
            onClick = onGoNext,
            modifier = Modifier
                .size(48.dp)
                .align(alignment = Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_arrow_back_24),
                contentDescription = null,
                modifier = Modifier.rotate(180.0f)
            )
        }
    }
}

@Composable
private fun CompleteButton(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        FilledTonalIconButton(
            onClick = onComplete,
            modifier = Modifier
                .size(48.dp)
                .align(alignment = Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_check_24),
                contentDescription = null
            )
        }
    }
}