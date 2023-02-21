package com.example.fserv.api

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.fserv.model.Event
import com.example.fserv.model.SearchOptions
import com.example.fserv.model.UserInfo
import kotlinx.coroutines.flow.Flow
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

const val NETWORK_PAGE_SIZE = 25
class DataRepository {
    private lateinit var userId: String


    fun updateUserId(newUserId: String){
        userId = newUserId
    }

    fun getUserId(): String {
        return userId
    }




    private val api: Api
    init {


        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")// https://fserv-api.onrender.com/
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create<Api>()
    }

    fun registerNewUser(userData: UserInfo): Call<String> {
        return api.registerNewClient(userData.email, userData.password)
    }

    fun loginClient(userData: UserInfo): Call<String> {
        return api.loginClient(userData.email, userData.password)
    }

    fun confirmAccount(token: String): Call<String> {
        return api.confirmAccount(token)
    }

    private fun getPager(options: SearchOptions): Flow<PagingData<Event>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ) ,
            pagingSourceFactory = {
                EventPagingSource(api = api, options = options)
            }
        ).flow
    }



    fun searchEvents(options: SearchOptions) = getPager(options)



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