package com.example.fserv.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fserv.api.TicketRepository
import com.example.fserv.model.app.SearchOptions
import com.example.fserv.model.server.Event
import com.example.fserv.model.server.Ticket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TicketsListViewModel(val event: Event): ViewModel() {

    var isRefreshing = MutableStateFlow(false)
    private val repo = TicketRepository.get()


    fun getTickets(): Flow<PagingData<Ticket>> =
        repo.getTickets(
            eventId = event._id
        ).cachedIn(viewModelScope)
}