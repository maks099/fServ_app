package com.example.fserv.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fserv.api.TicketRepository
import com.example.fserv.model.app.DownloadType
import com.example.fserv.model.server.TicketGroup
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EventViewModel : ViewModel() {

    var actionButtonStatus by mutableStateOf(true)
    var downloadType by mutableStateOf(DownloadType.PREVIEW)
    var ticketsGroups :Array<TicketGroup> = arrayOf<TicketGroup>()
    val repository = TicketRepository.get()


    fun getTickets(eventID: String) {
        repository.getTicketsGroupsByEvent(eventID)
            .enqueue(
                object : Callback<String> {
                    override fun onResponse(call: Call<String> , response: Response<String>) {
                        if(response.isSuccessful){
                            downloadType.message = response.body().toString()
                            ticketsGroups = Gson().fromJson(
                                downloadType.message,
                                Array<TicketGroup>::class.java
                            )
                            downloadType = DownloadType.SUCCESS
                        }

                    }

                    override fun onFailure(call: Call<String> , t: Throwable) {
                        downloadType.message = t.message.toString()
                        downloadType = DownloadType.FAIL
                    }
                }

            )

    }
}