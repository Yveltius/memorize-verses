package com.yveltius.memorize.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.yveltius.memorize.ui.components.AppScaffold
import com.yveltius.memorize.viewmodels.ChooseNextWordViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

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
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Welcome to the Choose Next Word Screen.\n${uiState.verse}")
        }
    }
}