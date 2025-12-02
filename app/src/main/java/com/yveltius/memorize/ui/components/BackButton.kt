package com.yveltius.memorize.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.yveltius.memorize.R

@Composable
fun BackButton(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onBackPress
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_arrow_left),
            contentDescription = stringResource(R.string.content_description_back_arrow)
        )
    }
}