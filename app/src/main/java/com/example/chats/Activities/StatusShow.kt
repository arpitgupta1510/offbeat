package com.example.chats.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.chats.Models.ProfileMain
import com.example.chats.R
import com.example.chats.databinding.ActivityStatusShowBinding
import com.google.firebase.database.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class StatusShow : AppCompatActivity() {
    private lateinit var binding:ActivityStatusShowBinding
    private lateinit var database:DatabaseReference
    private lateinit var userUid:String
    private lateinit var friendName:String
    private lateinit var friendUid:String
    private lateinit var userImage:String
    private lateinit var statusUid:String
    private lateinit var statusTime:String
    private lateinit var statusCaption:String
    private var date: DateFormat = SimpleDateFormat("hh:mm a")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStatusShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database=FirebaseDatabase.getInstance().reference
        userUid=intent.getStringExtra("UsersUid").toString()
        friendUid=intent.getStringExtra("FriendUid").toString()
        friendName=intent.getStringExtra("FriendName").toString()
        statusUid=intent.getStringExtra("StatusUid").toString()
        statusCaption=intent.getStringExtra("StatusCaption").toString()
        statusTime=intent.getStringExtra("StatusTime").toString()
        Glide.with(this)
            .load(statusUid)
            .override(1000, 1000)
            .centerCrop()
            .into(binding.friendStatusShow)
        binding.textView5.setText(statusCaption)
        binding.friendStatusShowName.setText(friendName)
        binding.friendStatusShowTime.setText(date.format(statusTime.toLong()).toString())
        database.child("Profile").child(friendUid).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var user=snapshot.getValue(ProfileMain::class.java)
                if(user!=null){
                    Glide.with(this@StatusShow)
                        .load(user.image)
                        .override(1000, 1000)
                        .centerCrop()
                        .into(binding.friendStatusShowImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}