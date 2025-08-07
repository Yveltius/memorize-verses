package com.yveltius.memorize.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yveltius.memorize.ui.components.AppScaffold
import com.yveltius.memorize.viewmodels.ChooseNextWordViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChooseNextWordScreen(
    onBackPress: () -> Boolean,
    verseUUIDString: String,
    chooseNextWordViewModel: ChooseNextWordViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        chooseNextWordViewModel.getVerse(verseUUIDString = verseUUIDString)
    }

    val uiState by chooseNextWordViewModel.uiState.collectAsState()

    AppScaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                uiState.currentWords.forEach {
                    Text(
                        text = it.toEmptySpaces(),
                        fontSize = 24.sp,
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                }
            }
        }
    }
}

fun String.toEmptySpaces(): String {
    val stringBuilder = StringBuilder()

    repeat(this.length) {
        stringBuilder.append(' ')
    }
    return stringBuilder.toString()
}