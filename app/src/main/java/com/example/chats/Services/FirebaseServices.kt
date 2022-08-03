package com.example.chats.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.chats.*
import com.example.chats.Activities.MainScreen
import com.example.chats.VideoCall.CallActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "myChannel"

class FirebaseServices : FirebaseMessagingService() {
    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    fun readLastButtonPressed( senderId:String): Boolean {
        val sharedPref = getSharedPreferences("Mute Notifications", MODE_PRIVATE)
        return sharedPref.getBoolean(senderId, false)
    }


    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if(!readLastButtonPressed(message.data["senderId"].toString())){
            if(message.data["notificationId"].equals("1")){
                val intent = Intent(this, MainScreen::class.java)
                    intent.putExtra("UsersUid",message.data["senderId"])
                    val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    val notificationID = Random.nextInt()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        createNotificationChannel(notificationManager)
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)

                    val Group_Key = "com.example.chats.Services"
                    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(message.data["senderId"])
                        .setContentText(message.data["message"])
                        .setSmallIcon(R.drawable.app)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setStyle(
                            NotificationCompat.BigTextStyle()
                                .bigText(message.data["message"])
                        )
                        .setGroup(Group_Key)
                        .build()

                    notificationManager.notify(notificationID, notification)



            }
            else if(message.data["notificationId"].equals("2")){
                val intent = Intent(this, CallActivity::class.java)
                intent.putExtra("UsersUid",message.data["receiverId"])
                intent.putExtra("FriendUid",message.data["senderId"])
                intent.putExtra("CallId",message.data["callId"])
                intent.putExtra("CalledBy",message.data["senderId"])
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notificationID = Random.nextInt()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel(notificationManager)
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
                val Group_Key = "com.example.chats.Services"
                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(message.data["senderId"])
                    .setContentText(message.data["message"])
                    .setSmallIcon(R.drawable.app)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(message.data["message"])
                    )
                    .setGroup(Group_Key)
                    .build()

                notificationManager.notify(notificationID, notification)
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

}