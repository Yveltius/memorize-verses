package com.yveltius.memorize.features.addverse.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yveltius.memorize.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tags(
    tags: List<String>,
    allTags: List<String>,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var emptyTagError by remember { mutableStateOf(value = false) }
    var expanded by remember { mutableStateOf(value = false) }
    val textFieldState = rememberTextFieldState()
    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                state = textFieldState,
                lineLimits = TextFieldLineLimits.SingleLine,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = emptyTagError,
                label = { Text(text = stringResource(R.string.label_add_tag)) },
                supportingText = if (emptyTagError) {
                    { Text(text = stringResource(R.string.error_empty_tag)) }
                } else {
                    null
                },
                trailingIcon = {
                    if (emptyTagError) {
                        Icon(
                            painter = painterResource(R.drawable.outline_error_24),
                            contentDescription = null
                        )
                    } else {
                        IconButton(
                            onClick = {
                                if (textFieldState.text.isEmpty()) {
                                    emptyTagError = true
                                } else {
                                    onAddTag(textFieldState.text.toString())
                                    textFieldState.clearText()
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_check_24),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )

            val filteredTags = allTags
                .filter { tag ->
                    tags.none { filterTag -> filterTag == tag }
                }
                .filter { tag ->
                    tag.contains(textFieldState.text, ignoreCase = true)
                }

            if (filteredTags.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.heightIn(max = 280.dp)
                ) {
                    filteredTags.forEach { tagSelection ->
                        DropdownMenuItem(
                            onClick = {
                                textFieldState.setTextAndPlaceCursorAtEnd(tagSelection)
                                expanded = false
                            },
                            text = { Text(text = tagSelection) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tags.forEach { tag ->
                InputChip(
                    selected = false,
                    onClick = { onRemoveTag(tag) },
                    label = { Text(text = tag) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.icon_x),
                            contentDescription = null,
                            modifier = Modifier
                                .size(InputChipDefaults.AvatarSize)
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Default() {
    Tags(
        tags = listOf(),
        allTags = listOf(),
        onAddTag = {},
        onRemoveTag = {},
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun WithSomeTags() {
    Tags(
        tags = listOf("Tag 1", "Tag 2", "Tag 3", "Tag 4", "Tag 5"),
        allTags = listOf(),
        onAddTag = {},
        onRemoveTag = {},
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    )
}