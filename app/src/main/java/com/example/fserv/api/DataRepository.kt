package com.example.fserv.api

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.fserv.model.app.SearchOptions
import com.example.fserv.model.server.Client
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

const val NETWORK_PAGE_SIZE = 25
class DataRepository {
   private var client: Client = Client()


    fun updateUserId(newUserId: String){
        client._id = newUserId
    }

    fun updateClient(client: Client){
        this.client = client
    }

    fun getClient(): Client {
        return client
    }

    fun getClientFromServer(): Call<String>{
        Log.d("EEEeee", client._id)
        return api.getClientById(client._id)
    }




    public val api: Api
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")// https://fserv-api.onrender.com/
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create<Api>()
    }

    fun registerNewUser(userData: Client): Call<String> {
        return api.registerNewClient(userData.email, userData.password)
    }

    fun loginClient(userData: Client): Call<String> {
        return api.loginClient(userData.email, userData.password)
    }

    fun confirmAccount(token: String): Call<String> {
        return api.confirmAccount(token)
    }



    fun searchEvents(options: SearchOptions) = Pager(
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false
        ) ,
        pagingSourceFactory = {
            EventPagingSource(api = api, options = options)
        }
    ).flow


    companion object {
        private var INSTANCE: DataRepository? = null
        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = DataRepository()
            }
        }
        fun get(): DataRepository {
            return INSTANCE ?:
            throw IllegalStateException("Repository must be initialized")
        }
    }
}