package com.example.fserv.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.Date

data class Event(
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("address")
    @Expose
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val date: String,
    val tags: List<String>,
    val gallery: List<String>
)

data class EventResponse(
    val events: List<Event>
)
