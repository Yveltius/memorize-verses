package com.yveltius.memorize.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun <T> VerticalGrid(
    items: List<T>,
    columns: Int,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable (index: Int, item: T) -> Unit = { index, item -> },
) {
    val splitList by remember {
        derivedStateOf {
            val remainder = items.size % columns

            List(size = ceil(items.size.toFloat() / columns).roundToInt()) { index ->
                val trueStartIndex = (index * columns)

                if ((trueStartIndex + columns - 1) < items.size) {
                    items.slice(trueStartIndex until trueStartIndex + columns)
                } else {
                    // on last row with remainder
                    items.slice(trueStartIndex until trueStartIndex + columns - remainder)
                }
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        splitList.forEachIndexed { splitIndex, split ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = horizontalArrangement) {
                split.forEachIndexed { index, t ->
                    content((splitIndex * columns) + index, t)
                }
            }
        }
    }
}