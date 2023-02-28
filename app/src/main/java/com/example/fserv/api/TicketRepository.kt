package com.example.fserv.api

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.fserv.model.app.SearchOptions
import retrofit2.Call

class TicketRepository {

    val mainRepository = DataRepository.get()
    private var api: Api = mainRepository.api

    fun getTicketsGroupsByEvent(eventID: String): Call<String> {
        return api.getTicketGroups(eventID)
    }

    fun buyTickets(ticketGroupId: String, countOfTickets: Int): Call<String>{
        return api.buyTickets(
            clientId = mainRepository.getClient()._id,
            ticketGroupId = ticketGroupId,
            countOfTickets = countOfTickets
        )
    }

    fun getTickets(eventId: String) = Pager(
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false
        ) ,
        pagingSourceFactory = {
            TicketPagingSource(api = api, clientId = mainRepository.getClient()._id, eventId = eventId)
        }
    ).flow

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