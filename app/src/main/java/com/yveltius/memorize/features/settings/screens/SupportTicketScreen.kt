package com.yveltius.memorize.features.settings.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yveltius.memorize.R
import com.yveltius.memorize.features.settings.viewmodels.SupportTicketViewModel
import com.yveltius.memorize.ui.components.BackButton
import com.yveltius.memorize.ui.theme.AppTheme
import androidx.core.net.toUri

@Composable
fun SupportTicketScreen(
    onBackPress: () -> Unit,
    supportTicketViewModel: SupportTicketViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by supportTicketViewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.hasNoActivityError) {
        val snackbarString = context.getString(R.string.snackbar_no_email_client_found)
        if (uiState.hasNoActivityError) {
            snackbarHostState.showSnackbar(message = snackbarString)

            supportTicketViewModel.clearErrors()
        }
    }

    LaunchedEffect(uiState.hasUnknownError) {
        val snackbarString = context.getString(R.string.snackbar_support_ticket_unknown_error)
        if (uiState.hasUnknownError) {
            snackbarHostState.showSnackbar(message = snackbarString)

            supportTicketViewModel.clearErrors()
        }
    }

    AppTheme {
        Scaffold(
            topBar = { SupportTicketTopBar(onBackPress = onBackPress) },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            RootView(
                subject = uiState.subject,
                onSubjectChanged = supportTicketViewModel::onSubjectChanged,
                body = uiState.body,
                onBodyChanged = supportTicketViewModel::onBodyChanged,
                onNoActivityError = supportTicketViewModel::onNoActivityFound,
                onUnknownError = supportTicketViewModel::onUnknownError,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = innerPadding)
                    .padding(16.dp)
            )
        }
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
private fun RootView(
    subject: String,
    onSubjectChanged: (String) -> Unit,
    body: String,
    onBodyChanged: (String) -> Unit,
    onNoActivityError: () -> Unit,
    onUnknownError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Subject(
            subject = subject,
            onSubjectChanged = onSubjectChanged,
            modifier = Modifier.fillMaxWidth()
        )

        Body(
            body = body,
            onBodyChanged = onBodyChanged,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Button(
            onClick = {
                sendSupportTicket(
                    context = context,
                    subject = subject,
                    body = body,
                    onNoActivityError = onNoActivityError,
                    onUnknownError = onUnknownError,
                )
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.button_support_ticket_send))
        }
    }
}

@Composable
private fun Subject(
    subject: String,
    onSubjectChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = subject,
        onValueChange = onSubjectChanged,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        label = { Text(text = stringResource(R.string.label_support_ticket_subject)) },
        modifier = modifier
    )
}

@Composable
private fun Body(
    body: String,
    onBodyChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = body,
        onValueChange = onBodyChanged,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        label = { Text(text = stringResource(R.string.label_support_ticket_body)) },
        modifier = modifier
    )
}

private fun sendSupportTicket(
    context: Context,
    subject: String,
    body: String,
    onNoActivityError: () -> Unit,
    onUnknownError: () -> Unit
) {
    val supportTicketEmailAddress = context.getString(R.string.support_ticket_email)
    try {
        val supportTicketUri = "mailto:$supportTicketEmailAddress?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}".toUri()
        val sendSupportTicketIntent = Intent(Intent.ACTION_SENDTO, supportTicketUri)

        sendSupportTicketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(sendSupportTicketIntent)
    } catch (e: ActivityNotFoundException) {
        onNoActivityError()
    } catch (throwable: Throwable) {
        onUnknownError()
    }
}

@Preview(showBackground = true)
@Composable
private fun RootPreview() {
    RootView(
        subject = "Test Subject",
        onSubjectChanged = { },
        body = "This is my test body.",
        onBodyChanged = {},
        onNoActivityError = {},
        onUnknownError = {},
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}