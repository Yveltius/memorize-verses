package com.yveltius.memorize.features.main

import com.yveltius.memorize.R
import com.yveltius.versememorization.entity.versesearch.SearchCategory

val SearchCategory.iconResId: Int
    get() = when (this) {
        SearchCategory.Book -> R.drawable.icon_book
        SearchCategory.Text -> R.drawable.icon_text
        SearchCategory.Tag -> R.drawable.icon_tag
        SearchCategory.Collection -> R.drawable.icon_collection
    }