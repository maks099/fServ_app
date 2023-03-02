package com.example.fserv.model.server

class Ticket(
    val _id: String,
    val eventId: String
)
data class TicketResponse(
    val tickets: List<Ticket>
)
