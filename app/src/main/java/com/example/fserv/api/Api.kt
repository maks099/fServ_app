package com.example.fserv.api

import com.example.fserv.model.Event
import com.example.fserv.model.EventResponse
import retrofit2.Call
import retrofit2.http.*


interface Api {


    @FormUrlEncoded
    @POST("registerNewClient")
    fun registerNewClient(@Field("email") email:String, @Field("password") password: String): Call<String>

    @FormUrlEncoded
    @POST("loginClient")
    fun loginClient(@Field("email") email:String, @Field("password") password: String): Call<String>




    @GET("confirmAccount/{token}")
    fun confirmAccount(@Path("token") token: String): Call<String>



    @FormUrlEncoded
    @POST("searchEvents")
    fun searchEvents(
        @Field("searchTerm") searchTerm: String,
        @Field("searchType") searchType: String,
        @Field("userID") userID: String,
        @Field("page") page: Int = 0): EventResponse

    @GET("getEvents/{userId}/{page}")
    suspend fun getAllEvents(
        @Path("userId") userId: String,
        @Path("page") page: Int = 0
    ): EventResponse



}