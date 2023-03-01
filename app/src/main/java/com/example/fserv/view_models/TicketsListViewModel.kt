package com.example.fserv.view_models

import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fserv.api.TicketRepository
import com.example.fserv.model.server.Event
import com.example.fserv.model.server.Ticket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.apache.commons.io.IOUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class TicketsListViewModel(val event: Event): ViewModel() {

    var isRefreshing = MutableStateFlow(false)
    private val repo = TicketRepository.get()
    lateinit var myFile: File

    fun getTickets(): Flow<PagingData<Ticket>> =
        repo.getTickets(
            eventId = event._id
        ).cachedIn(viewModelScope)

    fun checkTicketExisting(ticketId: String , next: () -> Unit){
        val path: File =  Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS);
        myFile = File("$path/$ticketId.pdf")
        if(myFile.exists()) {
            next()
        } else {
            viewModelScope.launch {
                repo.downloadTicket(ticketId).enqueue(
                    object : Callback<ResponseBody>{
                        override fun onResponse(
                            call: Call<ResponseBody> ,
                            response: Response<ResponseBody>
                        ) {
                            if(response.isSuccessful){
                                getPdfFromResponse(response, next)
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody> , t: Throwable) {
                            TODO("Not yet implemented")
                        }

                    }
                )
            }
        }
    }

     fun getPdfFromResponse(responseBody: Response<ResponseBody>, next: () -> Unit){
        try {
            val fileOutputStream = FileOutputStream(myFile)
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            IOUtils.write(
                responseBody.body()!!.bytes() ,
                fileOutputStream
            )
            next()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}