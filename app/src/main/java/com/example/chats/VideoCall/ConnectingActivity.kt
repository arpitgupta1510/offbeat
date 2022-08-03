package com.example.chats.VideoCall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.chats.databinding.ActivityConnectingBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError

import android.content.Intent

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener


class ConnectingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConnectingBinding
    private lateinit var database: DatabaseReference
    private lateinit var userUid: String
    private lateinit var usersImage: String
    private var isOkay: Boolean = false
    private var x: String = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().reference
        userUid = intent.getStringExtra("UsersUid").toString()
        usersImage = intent.getStringExtra("UsersImage").toString()
        Glide.with(this)
            .load(usersImage)
            .override(1000, 1000)
            .circleCrop()
            .into(binding.profile)


        database.child("users")
            .orderByChild("status")
            .equalTo(x).limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount > 0) {
                        isOkay = true
                        for (childSnap in snapshot.children) {
                            database.child("users").child(childSnap.key.toString())
                                .child("incoming").setValue(userUid)
                            database.child("users").child(childSnap.key.toString()).child("status")
                                .setValue("1")
                            val intent = Intent(this@ConnectingActivity, CallActivity::class.java)
                            val incoming = childSnap.child("incoming").getValue(
                                String::class.java
                            )
                            val createdBy = childSnap.child("createdBy").getValue(
                                String::class.java
                            )
                            val isAvailable = childSnap.child("isAvailable").getValue(
                                Boolean::class.java
                            )!!
                            intent.putExtra("UsersUid", userUid)
                            intent.putExtra("username", userUid)
                            intent.putExtra("incoming", incoming)
                            intent.putExtra("createdBy", createdBy)
                            intent.putExtra("isAvailable", isAvailable)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val room: HashMap<String, Any> = HashMap()
                        room["incoming"] = userUid
                        room["createdBy"] = userUid
                        room["isAvailable"] = true
                        room["status"] = "0"

                        database.child("users").child(userUid).setValue(room).addOnSuccessListener {
                            database.child("users").child(userUid)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.child("status").exists()) {
                                            if (snapshot.child("status")
                                                    .getValue(String::class.java) == "1"
                                            ) {
                                                if (isOkay) return
                                                isOkay = true
                                                val intent = Intent(
                                                    this@ConnectingActivity,
                                                    CallActivity::class.java
                                                )
                                                val incoming =
                                                    snapshot.child("incoming").getValue(
                                                        String::class.java
                                                    )
                                                val createdBy =
                                                    snapshot.child("createdBy").getValue(
                                                        String::class.java
                                                    )
                                                val isAvailable =
                                                    snapshot.child("isAvailable").getValue(
                                                        Boolean::class.java
                                                    )!!
                                                intent.putExtra("UsersUid", userUid)
                                                intent.putExtra("username", userUid)
                                                intent.putExtra("incoming", incoming)
                                                intent.putExtra("createdBy", createdBy)
                                                intent.putExtra("isAvailable", isAvailable)
                                                startActivity(intent)
                                                finish()
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }
}