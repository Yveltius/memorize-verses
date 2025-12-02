package com.yveltius.memorize.features.verselist

import com.yveltius.memorize.R
import com.yveltius.versememorization.data.versesearch.VerseSearchCategory

val VerseSearchCategory.iconResId: Int
    get() = when (this) {
        VerseSearchCategory.Book -> R.drawable.icon_book
        VerseSearchCategory.Text -> R.drawable.icon_text
        VerseSearchCategory.Tag -> R.drawable.icon_tag
    }