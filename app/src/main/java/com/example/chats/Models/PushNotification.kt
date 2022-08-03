package com.example.chats.Models

import com.example.chats.Models.NotificationData

data class PushNotification(
    var data: NotificationData,
    var to: String
)
