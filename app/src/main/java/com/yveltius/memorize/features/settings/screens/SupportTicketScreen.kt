package com.yveltius.memorize.features.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.yveltius.memorize.R
import com.yveltius.memorize.ui.components.BackButton

@Composable
fun SupportTicketScreen(
    onBackPress: () -> Unit
) {
    Scaffold(
        topBar = { SupportTicketTopBar(onBackPress = onBackPress) }
    ) { innerPadding ->
        Root(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportTicketTopBar(onBackPress: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.top_bar_title_support_ticket)) },
        navigationIcon = { BackButton(onBackPress = onBackPress) }
    )
}

@Composable
private fun Root(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {

    }
}