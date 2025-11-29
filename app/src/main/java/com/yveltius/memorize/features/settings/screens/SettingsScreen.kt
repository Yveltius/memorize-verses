package com.yveltius.memorize.features.settings.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yveltius.memorize.R
import com.yveltius.memorize.features.settings.viewmodels.SettingsViewModel
import com.yveltius.memorize.ui.components.BackButton

@Composable
fun SettingsScreen(
    onBackPress: () -> Unit,
    onGoToSupportTicket: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            SettingsTopBar(onBackPress = onBackPress)
        }
    ) { innerPadding ->
        Root(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
                .padding(16.dp),
            onGoToSupportTicket = onGoToSupportTicket
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onBackPress: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.top_bar_title_settings)) },
        navigationIcon = { BackButton(onBackPress = onBackPress) }
    )
}

@Composable
private fun Root(
    onGoToSupportTicket: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onGoToSupportTicket)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.settings_item_support_ticket), modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.icon_arrow_left),
                    modifier = Modifier.rotate(180f),
                    contentDescription = stringResource(R.string.content_description_go_support_ticket)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RootPreview() {
    Root(onGoToSupportTicket = {}, modifier = Modifier.fillMaxSize().padding(16.dp))
}