package com.yveltius.memorize.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.yveltius.memorize.R
import com.yveltius.memorize.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onBackPress: () -> Unit,
    topBarText: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    AppTheme {
        TopAppBar(
            title = { if (topBarText != null) Text(text = topBarText) },
            navigationIcon = {
                IconButton(
                    onClick = onBackPress
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_arrow_back_24),
                        contentDescription = null
                    )
                }
            },
            actions = actions
        )
    }
}