package com.yveltius.memorize.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun FullscreenDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            decorFitsSystemWindows = properties.decorFitsSystemWindows,
            dismissOnClickOutside = properties.dismissOnClickOutside,
            dismissOnBackPress = properties.dismissOnBackPress,
            usePlatformDefaultWidth = false,
            securePolicy = properties.securePolicy
        ),
        content = content
    )
}