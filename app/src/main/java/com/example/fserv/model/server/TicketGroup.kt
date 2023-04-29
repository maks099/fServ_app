package com.example.fserv.model.server

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
class TicketGroup(
    val _id: String,
    val name: String,
    val price: Int,
    val count: Int,
    val eventID: String,
    val color: String
):Parcelable{
    override fun toString(): String = Uri.encode(Gson().toJson(this))
}

@Parcelize
class TicketsGroupsContainer(
    val list: List<TicketGroup>
): Parcelable {
    override fun toString(): String = Uri.encode(Gson().toJson(this))
}

class TicketGroupContainerArgType : JsonNavType<TicketsGroupsContainer>() {
    override fun fromJsonParse(value: String): TicketsGroupsContainer = Gson().fromJson(value, TicketsGroupsContainer::class.java)
    override fun TicketsGroupsContainer.getJsonParse(): String {
        return Gson().toJson(this)
    }

}
