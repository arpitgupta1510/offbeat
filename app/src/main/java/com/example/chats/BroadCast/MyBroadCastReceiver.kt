package com.example.chats.BroadCast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.chats.Models.PushNotification
import com.example.chats.Models.Message
import com.example.chats.Models.NotificationData
import com.example.chats.Services.RetrofitInstance
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyBroadCastReceiver : BroadcastReceiver() {
    private lateinit var database: DatabaseReference
    private lateinit var usersUid: String
    private lateinit var friendUid: String
    private lateinit var clgUid: String
    private lateinit var message: String
    private lateinit var rand: String
    private var friendsToken: String? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Message", Toast.LENGTH_LONG).show()
        database = FirebaseDatabase.getInstance().getReference()
        usersUid = intent?.getStringExtra("UsersUid").toString()
        clgUid = intent?.getStringExtra("ClgUid").toString()
        friendUid = intent?.getStringExtra("FriendUid").toString()
        message = intent?.getStringExtra("message").toString()
        friendsToken()
        rand = database.push().key.toString()
        var msg =
            Message(null,null, message, usersUid, System.currentTimeMillis().toString(), false, rand)
        database.child("Colleges").child(clgUid).child("Users").child(usersUid).child("Friends")
            .child(friendUid).child("messages")
            .child(rand).setValue(msg).addOnSuccessListener {
                database.child("Colleges").child(clgUid).child("Users").child(friendUid)
                    .child("Friends").child(usersUid).child("messages")
                    .child(rand).setValue(msg).addOnSuccessListener {
                        if (friendsToken != null) {
                            PushNotification(
                                NotificationData(usersUid, msg.msg.toString()),
                                friendsToken.toString()
                            ).also { sendNotification(it) }
                        } else {
                            friendsToken()
                        }

                    }
            }.addOnFailureListener {

            }

    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {

                } else {

                }
            } catch (e: Exception) {

            }
        }

    private fun friendsToken() {
        database.child("Profile").child(friendUid).child("token").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendsToken = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}