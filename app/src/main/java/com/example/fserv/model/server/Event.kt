package com.example.fserv.model.server


import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
public data class Event(
    val _id: String,
    val name: String,
    val address: String,
    val organizerName: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val isPaid: Boolean,
    val date: String,
    val tags: List<String>,
    val gallery: List<String>
):Parcelable{
    override fun toString(): String = Uri.encode(Gson().toJson(this))
}
@Parcelize
class myInfo(
    val _id: String,
    val name: String = "hello",
    val gallery: List<String>
) :Parcelable
data class myInfoResponse(
    val tickets: List<myInfo>
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

