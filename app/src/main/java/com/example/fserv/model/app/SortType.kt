package com.example.fserv.model.app

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fserv.R

enum class SortType(
    @StringRes val typeName: Int,
    val subName: String,
    val item: Any,
    val drawable: Int
) {
    DateAscending(
        typeName = R.string.date_ascending,
        subName = "date",
        item = 1,
        R.drawable.baseline_arrow_upward_24
    ),
    DateDescending(
        typeName = R.string.date_descending,
        subName = "date",
        item = -1,
        R.drawable.baseline_arrow_downward_24
    ),

}