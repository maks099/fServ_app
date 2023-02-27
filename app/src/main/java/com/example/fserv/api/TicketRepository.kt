package com.example.fserv.api

import retrofit2.Call

class TicketRepository {

    private var api: Api = DataRepository.get().api

    fun getTicketsGroupsByEvent(eventID: String): Call<String> {
        return api.getTicketGroups(eventID)
    }

    companion object {
        private var INSTANCE: TicketRepository? = null
        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = TicketRepository()
            }
        }
        fun get(): TicketRepository {
            return INSTANCE ?:
            throw IllegalStateException("Repository must be initialized")
        }
    }
}