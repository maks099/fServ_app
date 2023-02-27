package com.example.fserv.model.app

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
    val searchType: SearchType ,
    val sortType: SortType ,
    val pickedCategory: Category ,
    val filters: List<FilterType> ,
    val searchTerm: String ,
    val userID: String
){
    fun stringifyFilters(): String{
        var result = ""
        filters.forEach {
            filter ->
            result += "$filter,"
        }
        return result
    }
}