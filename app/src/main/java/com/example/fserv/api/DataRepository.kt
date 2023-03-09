package com.example.fserv.api

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.fserv.model.app.SearchOptions
import com.example.fserv.model.server.Client
import com.example.fserv.model.server.NotificationResponse
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create


const val NETWORK_PAGE_SIZE = 25
class DataRepository {

    var userId: String = ""
    set(value) {
        field = value.replace("\"", "")
    }

    fun forgotPassword(email: String): Call<String> = api.forgotPassword(email)
    fun resetPassword(token: String, newPassword: String): Call<String> = api.resetPassword(token, newPassword)

    public val api: Api
    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.43.247:3000/")// https://fserv-api.onrender.com/
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        api = retrofit.create<Api>()
    }

    fun registerNewUser(userData: Client): Call<String> {
        return api.registerNewClient(userData.email, userData.password, userData.firstName, userData.lastName)
    }

    fun loginClient(userData: Client): Call<String> {
        return api.loginClient(userData.email, userData.password)
    }

    fun getUserBilling(): Call<String> {
        Log.d("Get billing id", userId)
        return api.getUserBilling(userId)
    }

    fun confirmAccount(token: String): Call<String> {
        return api.confirmAccount(token)
    }

    fun searchNotifications(clientId: String): Call<NotificationResponse> {
        return api.searchNotifications(clientId)
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