package com.example.chats.Activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import com.example.chats.Models.Message
import com.example.chats.BroadCast.MyBroadCastReceiver
import com.example.chats.databinding.ActivityMessageRequestsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageRequests : AppCompatActivity() {
    private lateinit var binding: ActivityMessageRequestsBinding
    private lateinit var database2: DatabaseReference
    private lateinit var fauth: FirebaseAuth
    private lateinit var alarmManager: AlarmManager
    private lateinit var usersUid: String
    private lateinit var friendUid: String
    private lateinit var clgUid: String
    private var calendar: Calendar? = null
    private var currentHour: Int? = null
    private var currentMin: Int? = null
    private var hour: Int? = null
    private var min: Int? = null
    private var time: Long? = null
    private var am: String? = null
    private lateinit var messages: ArrayList<Message>
    private lateinit var rand: String
    private var date: DateFormat = SimpleDateFormat("hh")
    private var date2: DateFormat = SimpleDateFormat("mm")
    private var date3: DateFormat = SimpleDateFormat("a")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        messages = arrayListOf()
        fauth = FirebaseAuth.getInstance()
        usersUid = intent.getStringExtra("UsersUid").toString()
        friendUid = intent.getStringExtra("friendUid").toString()
        clgUid = intent.getStringExtra("ClgUid").toString()
        database2 = FirebaseDatabase.getInstance().getReference()
//        database2.child("Profile").child()
//            .child("friend").child(receiverId).child("messageRequest")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    messages.clear()
//                    for (dataSnapshot in snapshot.children) {
//                        var message = dataSnapshot.getValue(Message::class.java)
//                        if (message != null) {
//                            if (message.date.toString().toLong() <= System.currentTimeMillis()) {
//                                database2.child("Profile").child(senderId).child("friend")
//                                    .child(receiverId)
//                                    .child("messages")
//                                    .child(message.msgId.toString()).setValue(message)
//                                    .addOnSuccessListener {
//                                        Toast.makeText(
//                                            this@MessageRequests,
//                                            "Message Sent",
//                                            Toast.LENGTH_SHORT
//                                        )
//                                            .show()
//                                    }
//                            }
//
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//            })

        rand = database2.push().key.toString()
        binding.timeChangeBtn.setOnClickListener {
            timeBtnClick()
        }
        binding.requestMsgSentBtn.setOnClickListener {
            currentHour = date.format(System.currentTimeMillis().toLong()).toString().toInt()
            currentMin = date2.format(System.currentTimeMillis().toLong()).toString().toInt()
            am = date3.format(System.currentTimeMillis().toLong()).toString()
            if (am == "am")
                hour = hour!! + 12
            Toast.makeText(this, am, Toast.LENGTH_SHORT).show()
            var newMsg = binding.requestMsgBox.text
            time =
                (System.currentTimeMillis() + ((hour!! * 60 + min!!) - (currentHour!! * 60 + currentMin!!)) * 60000)
            if (newMsg == null) {
                Toast.makeText(this, "Type something", Toast.LENGTH_SHORT).show()
            } else {
                binding.requestMsgBox.setText("")
                var rand = database2.push().key.toString()
                var message = Message(
                    null,
                    null,
                    newMsg.toString().trim(),
                    usersUid,
                    (System.currentTimeMillis() + ((hour!! * 60 + min!!) - (currentHour!! * 60 + currentMin!!)) * 60000).toString(),
                    false,
                    rand
                )
                if (rand != "") {

                    var intent = Intent(this, MyBroadCastReceiver::class.java)
                    intent.putExtra("UsersUid", usersUid)
                    intent.putExtra("ClgUid", clgUid)
                    intent.putExtra("FriendUid", friendUid)
                    intent.putExtra("message", newMsg.toString())
                    var pendingIntent = PendingIntent.getBroadcast(this, 123123, intent, 0)
                    val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    alarmManager[AlarmManager.RTC_WAKEUP, time.toString().toLong()] = pendingIntent
                    Toast.makeText(
                        this,
                        "Message Sent",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }
        }
    }


    private fun timeBtnClick() {
        calendar = Calendar.getInstance()
        hour = calendar!!.get(Calendar.HOUR_OF_DAY)
        min = calendar!!.get(Calendar.MINUTE)
        var timePickerDialog = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                hour = hourOfDay
                min = minute
            }

        }, hour!!, min!!, false)
        timePickerDialog.show()
    }

}