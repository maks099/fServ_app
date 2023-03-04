package com.example.fserv.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fserv.api.DataRepository
import com.example.fserv.api.TicketRepository
import com.example.fserv.model.server.UserActivityObj
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivitiesListViewModel : ViewModel() {

    val isRefreshing = MutableStateFlow(false)
    private val ticketRepository = TicketRepository.get()
    private val dataRepo = DataRepository.get()
    var account by mutableStateOf(-1)


    fun getCustomInfos(): Flow<PagingData<UserActivityObj>> {
        getBilling()
        return  ticketRepository.getUserActivities().cachedIn(viewModelScope)
    }



    init {
        getBilling()
    }

    private fun getBilling(){
        dataRepo.getUserBilling().enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String> , response: Response<String>) {
                    when(response.isSuccessful){
                        true -> response.body()?.let {
                            try {
                                account = Integer.parseInt(it)
                            } catch (ex: java.lang.NumberFormatException){
                                ex.printStackTrace()
                            }
                        }
                        false -> {}
                    }
                }

                override fun onFailure(call: Call<String> , t: Throwable) {
                    Log.d("ERROR", t.message.toString())
                }
            }
        )
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