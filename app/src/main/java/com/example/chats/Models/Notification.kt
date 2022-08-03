package com.example.chats.Models

data class Notification(
   var notificationType:Int?=null,
    var notificationBy: String?=null,
    var date: Long?=null,
    var postId: String?=null,
     var checkOpen: Boolean = false,
     var clgUid: String?=null
)