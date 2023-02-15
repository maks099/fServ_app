package com.example.fserv.api

import com.example.fserv.model.UserInfo
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

}