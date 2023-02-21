package com.example.fserv.model

import androidx.annotation.StringRes
import com.example.fserv.R

enum class SortType(
    @StringRes val typeName: Int,
) {
    DateAscending(
        typeName = R.string.date_ascending
    ),
    DateDescending(
        typeName = R.string.date_descending
    ),
}