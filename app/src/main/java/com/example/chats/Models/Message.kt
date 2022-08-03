package com.example.chats.Models

import com.google.firebase.firestore.ServerTimestamp
import java.sql.Timestamp

class Message {
    var image: String? = null
    var msg: String? = null
    var senderId: String? = null
    var repliedTo:String?=null
    @ServerTimestamp
    var date: String? = null
    var seen: Boolean = false
    var msgId: String? = null

    constructor() {}
    constructor(
        repliedTo:String?,
        image: String?,
        msg: String?,
        senderId: String?,
        date: String?,
        seen: Boolean,
        msgId: String?
    ) {
        this.repliedTo=repliedTo
        this.image = image
        this.msg = msg
        this.senderId = senderId
        this.date = date
        this.seen = seen
        this.msgId = msgId
    }
}