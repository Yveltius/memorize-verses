package com.yveltius.memorize.features.verselist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yveltius.memorize.R
import com.yveltius.memorize.features.verselist.iconResId
import com.yveltius.memorize.ui.theme.AppTheme
import com.yveltius.versememorization.entity.versesearch.VerseSearchCategory
import com.yveltius.versememorization.entity.versesearch.VerseSearchResult
import com.yveltius.versememorization.entity.verses.Verse
import com.yveltius.versememorization.entity.verses.VerseNumberAndText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersesTopSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    searchResults: Map<VerseSearchCategory, List<VerseSearchResult>>,
    scrollBehavior: SearchBarScrollBehavior,
    allVerses: List<Verse>,
    lazyListState: LazyListState
) {
    val textFieldState = remember(query) {
        TextFieldState(initialText = query)
    }

    LaunchedEffect(textFieldState.text) {
        if (textFieldState.text != query) {
            onQueryChanged(textFieldState.text.toString())
        }
    }
    val searchBarState = rememberSearchBarState()
    val coroutineScope = rememberCoroutineScope()
    val inputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            onSearch = {},
            placeholder = {
                Text(text = stringResource(R.string.top_bar_search_placeholder))
            },
            leadingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded) {
                    IconButton(onClick = { coroutineScope.launch { searchBarState.animateToCollapsed() } }) {
                        Icon(
                            painter = painterResource(R.drawable.icon_arrow_left),
                            contentDescription = stringResource(R.string.content_description_close_search)
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(R.drawable.icon_search),
                        contentDescription = null
                    )
                }
            },
            trailingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded && query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChanged("") }) {
                        Icon(
                            painter = painterResource(R.drawable.icon_x),
                            contentDescription = stringResource(R.string.content_description_clear_search)
                        )
                    }
                }
            }
        )
    }

    TopSearchBar(
        state = searchBarState,
        inputField = inputField,
        scrollBehavior = scrollBehavior,
    )

    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            searchResults.forEach { (category, categoryResults) ->
                SearchResultCategory(
                    query = query,
                    category = category,
                    categoryResults = categoryResults,
                    onSearchResultSelected = {
                        coroutineScope.launch {
                            searchBarState.animateToCollapsed()
                            lazyListState.animateScrollToItem(
                                index = allVerses.indexOf(it.verse)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun SearchResultCategory(
    query: String,
    category: VerseSearchCategory,
    categoryResults: List<VerseSearchResult>,
    onSearchResultSelected: (VerseSearchResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchResultCategoryHeader(
            category = category,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        if (categoryResults.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                categoryResults.forEach { searchResult ->
                    SearchResultItem(
                        query = query,
                        searchResult = searchResult,
                        onSearchResultSelected = onSearchResultSelected,
                        category = category,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            Text(text = stringResource(R.string.search_no_results))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchResultCategoryHeader(
    category: VerseSearchCategory,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = category.name, style = MaterialTheme.typography.titleLargeEmphasized)
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
    }
}

@Composable
private fun SearchResultItem(
    query: String,
    searchResult: VerseSearchResult,
    category: VerseSearchCategory,
    modifier: Modifier = Modifier,
    onSearchResultSelected: (VerseSearchResult) -> Unit
) {
    when (category) {
        VerseSearchCategory.Book -> BookSearchResultItem(
            query,
            searchResult,
            category,
            onSearchResultSelected,
            modifier
        )

        VerseSearchCategory.Text -> TextSearchResultItem(
            query,
            searchResult,
            category,
            onSearchResultSelected,
            modifier
        )

        VerseSearchCategory.Tag -> TagSearchResultItem(
            query,
            searchResult,
            category,
            onSearchResultSelected,
            modifier
        )
    }

}

@Composable
private fun BookSearchResultItem(
    query: String,
    searchResult: VerseSearchResult,
    category: VerseSearchCategory,
    onSearchResultSelected: (VerseSearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable { onSearchResultSelected(searchResult) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(painter = painterResource(category.iconResId), contentDescription = null)
        Text(
            text = annotateStringWithQuery(query, string = searchResult.verse.getVerseString()),
            modifier = Modifier.weight(1f)
        )
        Icon(painter = painterResource(R.drawable.icon_arrow_right), contentDescription = null)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TextSearchResultItem(
    query: String,
    searchResult: VerseSearchResult,
    category: VerseSearchCategory,
    onSearchResultSelected: (VerseSearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable { onSearchResultSelected(searchResult) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(painter = painterResource(category.iconResId), contentDescription = null)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = searchResult.verse.getVerseString(),
                style = MaterialTheme.typography.titleMediumEmphasized,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = annotateStringWithQuery(
                    query,
                    string = searchResult.verse.verseText.joinToString(separator = "\n") { it.text },
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Icon(painter = painterResource(R.drawable.icon_arrow_right), contentDescription = null)
    }
}

@Composable
private fun TagSearchResultItem(
    query: String,
    searchResult: VerseSearchResult,
    category: VerseSearchCategory,
    onSearchResultSelected: (VerseSearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable { onSearchResultSelected(searchResult) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(painter = painterResource(category.iconResId), contentDescription = null)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = searchResult.verse.getVerseString(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = annotateStringWithQuery(
                    query,
                    string = searchResult.verse.tags.joinToString(),
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Icon(painter = painterResource(R.drawable.icon_arrow_right), contentDescription = null)
    }
}

private fun annotateStringWithQuery(
    query: String,
    string: String,
): AnnotatedString {
    return buildAnnotatedString {
        val queryIndices = string.indexesOf(query, ignoreCase = true)
        val boldIndices = queryIndices.map { List(size = query.length) { index -> it + index} }.flatten()

        string.forEachIndexed { index, char ->
            if (index in boldIndices) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(char.uppercase())
                }
            } else {
                append(char)
            }
        }
    }
}

private fun String.indexesOf(query: String, ignoreCase: Boolean = false): List<Int> {
    val indices = mutableListOf<Int>()
    if (query.isEmpty()) {
        // Handle empty substring case if needed, e.g., return empty list or all indices
        return indices
    }

    var currentIndex = 0
    while (true) {
        val foundIndex = this.indexOf(
            string = query,
            startIndex = currentIndex,
            ignoreCase = ignoreCase
        )
        when (foundIndex) {
            -1 -> return indices // Substring not found, exit loop
            else -> {
                indices.add(foundIndex)
                currentIndex = foundIndex + 1 // Start next search after the current match
            }
        }
    }
}

private val verseForPreviews = Verse(
    book = "Romans",
    chapter = 12,
    verseText = listOf(
        VerseNumberAndText(
            verseNumber = 1,
            text = "Therefore, brothers, I urge you, by the mercies of God, to present your bodies as a living sacrifice - holy and pleasing to God. This is your spiritual worship."
        ),
        VerseNumberAndText(
            verseNumber = 2,
            text = "Do not be conformed to this age, but be transformed by the renewing of your mind, so that you may discern what is the good, please, and perfect will of God."
        )
    ),
    tags = listOf("Discipleship Verses", "Obedience to Christ", "Worry", "Territory", "Rererepeat")
)

@Preview(showBackground = true)
@Composable
private fun BookSearchResultCategory() {
    SearchResultCategory(
        query = "rom",
        category = VerseSearchCategory.Book,
        categoryResults = listOf(verseForPreviews, verseForPreviews)
            .map { VerseSearchResult(it) },
        onSearchResultSelected = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun TextSearchResultCategory() {
    SearchResultCategory(
        query = "do not be conformed",
        category = VerseSearchCategory.Text,
        categoryResults = listOf(
            verseForPreviews, verseForPreviews
        ).map { VerseSearchResult(it) },
        onSearchResultSelected = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun TagSearchResultCategory() {
    SearchResultCategory(
        query = "ver",
        category = VerseSearchCategory.Tag,
        categoryResults = listOf(
            verseForPreviews, verseForPreviews
        ).map { VerseSearchResult(it) },
        onSearchResultSelected = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun TagSearchResultCategoryRepeatLetter() {
    SearchResultCategory(
        query = "re",
        category = VerseSearchCategory.Tag,
        categoryResults = listOf(
            verseForPreviews, verseForPreviews
        ).map { VerseSearchResult(it) },
        onSearchResultSelected = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchResultItemPreview() {
    AppTheme {
        SearchResultItem(
            query = "rom",
            searchResult = VerseSearchResult(verseForPreviews),
            category = VerseSearchCategory.Book,
            onSearchResultSelected = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}