package com.yveltius.memorize.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.errorDark
import com.example.compose.errorLight
import com.example.compose.onPrimaryDark
import com.example.compose.onPrimaryLight
import com.yveltius.memorize.R
import com.yveltius.memorize.ui.components.AppTopBar
import com.yveltius.memorize.ui.theme.AppTheme
import com.yveltius.memorize.viewmodels.ChooseNextWordViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChooseNextWordScreen(
    onBackPress: () -> Unit,
    verseUUIDString: String,
    chooseNextWordViewModel: ChooseNextWordViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        chooseNextWordViewModel.getVerse(verseUUIDString = verseUUIDString)
    }

    val uiState by chooseNextWordViewModel.uiState.collectAsState()

    AppTheme {
        Scaffold(
            topBar = { AppTopBar(onBackPress = onBackPress, topBarText = uiState.verse?.getVerseString()) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uiState.verse?.getVerseString(index = uiState.words.indexOf(uiState.currentWords))
                                ?: stringResource(R.string.encountered_error_for_verse_string),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        WordBlanks(
                            currentWordsAndPunctuation = uiState.currentWords,
                            currentGuessIndex = uiState.currentGuessIndex,
                            lastGuessIncorrect = uiState.lastGuessIncorrect,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Text(text = uiState.currentWords.count { it.isGuessed }
                        .toString() + "/" + uiState.currentGuessCount.toString())

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 4.dp,
                            alignment = Alignment.CenterHorizontally
                        )
                    ) {
                        uiState.availableGuesses.forEach {
                            Button(onClick = { chooseNextWordViewModel.onGuess(word = it.string) }) {
                                Text(text = it.string)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WordBlanks(
    currentWordsAndPunctuation: List<ChooseNextWordViewModel.WordGuessState>,
    currentGuessIndex: Int,
    lastGuessIncorrect: Boolean,
    modifier: Modifier = Modifier
) {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val underlineColor = if (isSystemInDarkTheme) onPrimaryDark else onPrimaryLight
    val errorUnderlineColor = if (isSystemInDarkTheme) errorDark else errorLight
    val textSize = remember { 32.sp }
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

fun String.toEmptySpaces(): String {
    val stringBuilder = StringBuilder()

    repeat(this.length) {
        stringBuilder.append("  ")
    }
    return stringBuilder.toString()
}