package com.example.fserv.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fserv.api.DataRepository
import com.example.fserv.api.TicketRepository
import com.example.fserv.model.app.DownloadType
import com.example.fserv.model.server.Event
import com.example.fserv.model.server.TicketGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TicketsGroupsListViewModel(val ticketsGroups: List<TicketGroup>, val event: Event): ViewModel() {

    private val repo = TicketRepository.get()
    private val dataRepo = DataRepository.get()
    var transactionStatus by mutableStateOf(DownloadType.PREVIEW)
    var account by mutableStateOf(-1.0)

    init {
        dataRepo.getUserBilling().enqueue(
            object : Callback<Double>{
                override fun onResponse(call: Call<Double> , response: Response<Double>) {
                    when(response.isSuccessful){
                        true -> response.body()?.let {
                            try {
                                account = it
                            } catch (ex: java.lang.NumberFormatException){
                                ex.printStackTrace()
                            }
                        }
                        false -> {}
                    }
                }

                override fun onFailure(call: Call<Double> , t: Throwable) {
                    Log.d("ERROR", t.message.toString())
                }
            }
        )
    }

    fun buyTickets(countOfTickets: Int, ticketGroupId: String){
        repo.buyTickets(ticketGroupId, countOfTickets)
            .enqueue(
                object : Callback<String>{
                    override fun onResponse(call: Call<String> , response: Response<String>) {
                        if(response.isSuccessful){
                            transactionStatus = DownloadType.SUCCESS
                        } else {
                            transactionStatus = DownloadType.FAIL
                        }
                    }

                    override fun onFailure(call: Call<String> , t: Throwable) {
                        transactionStatus = DownloadType.FAIL
                    }

                }
            )
    }



}