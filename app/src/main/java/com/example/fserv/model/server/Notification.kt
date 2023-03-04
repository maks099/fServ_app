package com.example.fserv.model.server

data class Notification(
    val _id: String,
    val message: String
)

data class NotificationResponse(
    val notifications: List<Notification>
)