package com.example.fserv.api

import com.example.fserv.model.UserInfo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class DataRepository {

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
}