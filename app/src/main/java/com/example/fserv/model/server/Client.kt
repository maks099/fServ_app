package com.example.fserv.model.server

data class Client(
    var _id: String = "",
    val email: String = "",
    val password: String = "",
    var account: Int = 0,
    var firstName: String = "",
    var lastName: String = ""
)
