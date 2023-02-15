package com.example.fserv.model

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
