package com.example.fserv.model

import androidx.annotation.StringRes
import com.example.fserv.R

enum class SearchType(
    @StringRes val typeName: Int,
) {
    Name(
        typeName = R.string.name
    ),
    Address(
        typeName = R.string.address
    ),
}

data class SearchOptions(
    val type: SearchType,
    val searchTerm: String,
    val userID: String
)