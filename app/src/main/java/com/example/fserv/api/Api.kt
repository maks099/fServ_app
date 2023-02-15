package com.example.fserv.api

import retrofit2.http.GET

interface Api {

    @GET("/test2")
    suspend fun testFetch(): String

}