package com.example.chats.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.chats.Models.ProfileMain
import com.example.chats.R
import com.example.chats.databinding.ActivityFriendProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FriendProfile : AppCompatActivity() {
    private lateinit var binding: ActivityFriendProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private lateinit var userUid: String
    private lateinit var clgUid: String
    private lateinit var friendUid: String
    private lateinit var friendName: String
    private lateinit var friendImage: String
    private lateinit var userProfile: Map<String, String>
    private lateinit var friendProfile: Map<String, String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()
        clgUid = intent.getStringExtra("ClgUid").toString()
        userUid = intent.getStringExtra("UsersUid").toString()
        friendUid = intent.getStringExtra("FriendUid").toString()
        friendImage = intent.getStringExtra("FriendImage").toString()
        friendName = intent.getStringExtra("FriendName").toString()
        binding.friendNameShow.setText(friendName)
        binding.friendsUid.setText(friendUid)
        Glide.with(this)
            .load(friendImage)
            .override(1000, 1000)
            .circleCrop()
            .placeholder(R.drawable.user)
            .into(binding.friendsImage)
        binding.message.setOnClickListener {
            database.child("Profile").child(userUid).get().addOnSuccessListener {
                var user = it.getValue(ProfileMain::class.java)
                var userName = user!!.name.toString()
                var userImage = user.image.toString()
                userProfile = mapOf<String, String>(
                    "name" to userName,
                    "image" to userImage,
                    "usersUid" to userUid
                )
                friendProfile = mapOf<String, String>(
                    "name" to friendName,
                    "image" to friendImage,
                    "usersUid" to friendUid
                )
                database.child("Colleges").child(clgUid).child("Users").child(userUid)
                    .child("Friends").child(friendUid).updateChildren(friendProfile)
                    .addOnSuccessListener {
                        database.child("Colleges").child(clgUid).child("Users").child(friendUid)
                            .child("Friends").child(userUid).updateChildren(userProfile)
                            .addOnSuccessListener {
                                var intent = Intent(this, FriendsChat::class.java)
                                intent.putExtra("UsersUid", userUid)
                                intent.putExtra("FriendUid", friendUid)
                                intent.putExtra("FriendName", friendName)
                                intent.putExtra("FriendImage", friendImage)
                                intent.putExtra("ClgUid", clgUid)
                                startActivity(intent)
                            }
                    }
            }
        }
    }

    private fun setData() {

    }
}