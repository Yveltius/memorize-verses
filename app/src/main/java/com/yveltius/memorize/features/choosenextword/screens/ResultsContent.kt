package com.yveltius.memorize.features.choosenextword.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.yveltius.memorize.R
import com.yveltius.memorize.features.choosenextword.viewmodels.ChooseNextWordViewModel
import com.yveltius.memorize.ui.components.VerticalGrid
import com.yveltius.versememorization.entity.verses.Verse
import kotlin.math.roundToInt

@Composable
fun ResultsContent(
    allWordsStates: List<List<ChooseNextWordViewModel.WordGuessState>>,
    guessCounts: List<Int>,
    onComplete: () -> Unit,
    verse: Verse,
    modifier: Modifier = Modifier,
) {
    assert(allWordsStates.size == guessCounts.size)

    Column(
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    ScoredResult(
                        topText = stringResource(R.string.results_overall),
                        scorePercentage = getOverallScorePercentage(allWordsStates, guessCounts),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(4f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                VerticalGrid(
                    items = allWordsStates,
                    columns = 2,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) { index, item ->
                    ScoredResult(
                        topText = verse.getVerseString(index = index),
                        scorePercentage = item.getScore(guessCount = guessCounts[index]),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            CompleteButton(
                onComplete = onComplete,
                modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun ScoredResult(
    topText: String,
    scorePercentage: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { scorePercentage },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            val localDensity = LocalDensity.current
            val maxSize = with(localDensity) { this@BoxWithConstraints.maxWidth }
            val topTextSize =
                maxSize * if (topText.length < 12) 0.15f else if (topText.length < 17) 0.1f else 0.08f
            val scoreTextSize =
                maxSize * if (topText.length < 12) 0.2f else if (topText.length < 17) 0.15f else 0.12f
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = topText, fontSize = topTextSize.toSp(), modifier = Modifier)
                Text(
                    text = "${(scorePercentage * 100f).roundToInt()}%",
                    fontSize = scoreTextSize.toSp()
                )
            }
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

private fun getOverallScorePercentage(
    allWordsStates: List<List<ChooseNextWordViewModel.WordGuessState>>,
    guessCounts: List<Int>
): Float {
    return allWordsStates.getSumForScore().toFloat() / guessCounts.sum().toFloat()
}

private fun List<List<ChooseNextWordViewModel.WordGuessState>>.getSumForScore(): Int {
    return this
        .flatten()
        .filter { wordState -> wordState.isGuessable }
        .size
}

private fun List<ChooseNextWordViewModel.WordGuessState>.getScore(guessCount: Int): Float {
    return this.filter { wordGuessState -> wordGuessState.isGuessable }.size.toFloat() / guessCount
}

@Composable
fun Dp.toSp(): TextUnit {
    return with(LocalDensity.current) { this@toSp.toSp() }
}

// region Previews
@Preview
@Composable
private fun ScoredResultPreview() {
    ScoredResult(
        topText = "Overall",
        scorePercentage = 0.85f,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Preview
@Composable
private fun ScoredResultSmallPreview() {
    ScoredResult(
        topText = "John 1:1",
        scorePercentage = 0.45f,
        modifier = Modifier
            .width(108.dp)
            .padding(16.dp)
    )
}

@Preview
@Composable
private fun ScoredResultSemiLongVerseNamePreview() {
    ScoredResult(
        topText = "2 Peter 3:14",
        scorePercentage = 1f,
        modifier = Modifier
            .width(160.dp)
            .padding(16.dp)
    )
}

@Preview
@Composable
private fun ScoredResultLongVerseNamePreview() {
    ScoredResult(
        topText = "Song of Solomon 8:14",
        scorePercentage = 0.65f,
        modifier = Modifier
            .width(160.dp)
            .padding(16.dp)
    )
}
// endregion