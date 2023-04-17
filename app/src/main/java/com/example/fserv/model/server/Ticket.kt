package com.example.fserv.model.server

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ticket(
    val _id: String,
    val eventId: String,
    val price: Int = 0,
    val groupName: String = ""
): Parcelable{

    companion object {
        fun getEmpty(): Ticket {
            return Ticket("", "");
        }
    }
}



data class TicketResponse(
    val tickets: List<Ticket>
)
