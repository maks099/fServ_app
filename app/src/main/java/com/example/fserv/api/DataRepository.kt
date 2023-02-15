package com.example.fserv.api

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

class DataRepository {

    private val api: Api
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:3000/")// https://fserv-api.onrender.com/
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        api = retrofit.create<Api>()
    }

    suspend fun testFetch() = api.testFetch()
}