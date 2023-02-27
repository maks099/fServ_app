package com.example.fserv.model.app

import androidx.annotation.StringRes
import com.example.fserv.R

enum class FilterType(
    @StringRes val typeName: Int,
    val field: String,
    val fieldValue: Any
) {
    Paid(
        R.string.paid_filter,
        "isPaid",
        "true"
    ),
    Free(
        R.string.free_filter,
        "isPaid",
        "false"
    );

    override fun toString(): String {
        return "${field}/${fieldValue}"
    }
}