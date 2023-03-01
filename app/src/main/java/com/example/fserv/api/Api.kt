package com.example.fserv.api

import com.example.fserv.model.server.EventResponse
import com.example.fserv.model.server.TicketResponse
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.*


interface Api {


    @FormUrlEncoded
    @POST("registerNewClient")
    fun registerNewClient(@Field("email") email:String, @Field("password") password: String, @Field("firstName") firstName:String, @Field("lastName") lastName: String): Call<String>

    @FormUrlEncoded
    @POST("loginClient")
    fun loginClient(@Field("email") email:String, @Field("password") password: String): Call<String>




    @GET("confirmAccount/{token}")
    fun confirmAccount(@Path("token") token: String): Call<String>



    @FormUrlEncoded
    @POST("searchEvents")
    suspend fun searchEvents(
        @Field("searchTerm") searchTerm: String,
        @Field("searchType") searchType: String,
        @Field("sortType") sortType: String,
        @Field("category") category: Int,
        @Field("sortValue") sortValue: Any,
        @Field("filters") filters: String,
        @Field("userId") userId: String,
        @Field("page") page: Int = 0): EventResponse

    @GET("getEvents/{userId}/{page}")
    suspend fun getAllEvents(
        @Path("userId") userId: String,
        @Path("page") page: Int = 0
    ): EventResponse

    @GET("ticketsGroups/{eventID}")
    fun getTicketGroups(@Path("eventID") eventID: String): Call<String>
    @GET("clients/{id}")
    fun getClientById(@Path("id") id: String): Call<String>

    @FormUrlEncoded
    @POST("buyTickets")
    fun buyTickets(
        @Field("clientId") clientId:String,
        @Field("ticketGroupId") ticketGroupId: String,
        @Field("countOfTickets") countOfTickets: Int,
    ): Call<String>


    @FormUrlEncoded
    @POST("searchTickets")
    suspend fun getTicketsByEvent(
        @Field("clientId") clientId: String,
        @Field("eventId") eventId: String,
        @Field("page") page: Int): TicketResponse

    @Streaming
    @GET("downloadTicket/{ticketId}")
    fun downloadTicket(@Path("ticketId") ticketId: String): Call<ResponseBody>

}