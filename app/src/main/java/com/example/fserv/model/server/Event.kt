package com.example.fserv.model.server


import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.example.fserv.utils.parseJSDate
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
public data class Event(
    val _id: String,
    val name: String,
    val address: String,
    val trademark: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val isPaid: Boolean,
    val date: String,
    val secondDate: String?,
    val tags: List<String>,
    val gallery: List<String>
):Parcelable{
    override fun toString(): String = Uri.encode(Gson().toJson(this))

    fun getParsedDate(currentLocale: Locale): String{

        return if(secondDate != null){
            "${parseJSDate(
                date,
                currentLocale
            )} - ${parseJSDate(
                secondDate,
                currentLocale
            )}"
        } else {
            parseJSDate(
                date,
                currentLocale
            )
        }
    }
}
@Parcelize
class UserActivity(
    val _id: String,
    val name: String = "",
    val gallery: List<String>,
    val date: String,
    val secondDate: String?
) :Parcelable {
    fun getParsedDate(currentLocale: Locale): String{

        return if(secondDate != null){
            "${parseJSDate(
                date,
                currentLocale
            )} - ${parseJSDate(
                secondDate,
                currentLocale
            )}"
        } else {
            parseJSDate(
                date,
                currentLocale
            )
        }
    }
}

data class UserActivityResponse(
    val tickets: List<UserActivity>
)

class EventArgType : JsonNavType<Event>() {
    override fun fromJsonParse(value: String): Event = Gson().fromJson(value, Event::class.java)
    override fun Event.getJsonParse(): String {
        return Gson().toJson(this)
    }

}

abstract class JsonNavType<T> : NavType<T>(isNullableAllowed = false) {
    abstract fun fromJsonParse(value: String): T
    abstract fun T.getJsonParse(): String

    override fun get(bundle: Bundle , key: String): T? =
        bundle.getString(key)?.let { parseValue(it) }

    override fun parseValue(value: String): T = fromJsonParse(value)

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, value.getJsonParse())
    }
}




data class EventResponse(
    val events: List<Event>
)

