package com.example.fserv.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fserv.api.TicketRepository
import com.example.fserv.model.server.Event
import com.example.fserv.model.server.myInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ActivitiesListViewModel : ViewModel() {

    val isRefreshing = MutableStateFlow(false)
    private val ticketRepository = TicketRepository.get()

    fun getCustomInfos(): Flow<PagingData<Event>> {
        Log.d("ActivitiesListViewModel", "is")
        return  ticketRepository.getCustomMyInfo().cachedIn(viewModelScope)
    }





    companion object {
        private var INSTANCE: ActivitiesListViewModel? = null
        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = ActivitiesListViewModel()
            }
        }

        fun get(): ActivitiesListViewModel {
            return INSTANCE ?:
            throw IllegalStateException("Repository must be initialized")
        }
    }
}