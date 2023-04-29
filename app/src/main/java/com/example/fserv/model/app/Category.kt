package com.example.fserv.model.app

import androidx.annotation.StringRes
import com.example.fserv.R

enum class Category(
    @StringRes val tagName: Int ,
    val code: Int ,
    var status: Boolean = false
) {
    All(
        tagName = R.string.all_tag,
        code = 0,
        status = true
    ),
    Music(
        tagName = R.string.music_tag,
        code = 1
    ),
    Study(
        tagName = R.string.study_tag,
        code = 2
    ),
    Mystezztwo(
        tagName = R.string.mys_tag,
        code = 3
    ),
    Sport(
        tagName = R.string.sport_tag,
        code = 4
    ),
    Food(
        tagName = R.string.food_tag,
        code = 5
    ),
    Health(
        tagName = R.string.health_tag,
        code = 6
    )
}

