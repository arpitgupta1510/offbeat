package com.example.chats.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chats.Adapter.ContactAdapter
import com.example.chats.Models.ProfileMain
import com.example.chats.databinding.ActivityContactListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class contactList : AppCompatActivity() {
    private lateinit var binding: ActivityContactListBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var usersUid: String
    private lateinit var clgUid: String
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var grpFriends: ArrayList<ProfileMain>
    private var x: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fAuth = FirebaseAuth.getInstance()
        grpFriends = arrayListOf()
        usersUid = intent.getStringExtra("UsersUid").toString()
        clgUid = intent.getStringExtra("ClgUid").toString()
        contactAdapter = ContactAdapter(this, grpFriends, usersUid, clgUid)
        binding.contactListView.layoutManager = LinearLayoutManager(this)
        binding.contactListView.adapter = contactAdapter
        database = FirebaseDatabase.getInstance().getReference()
        database.child("Colleges").child(clgUid).child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount == x) {
                        Toast.makeText(this@contactList, "No Other Users", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        grpFriends.clear()
                        for (friendSnapshot in snapshot.children) {
                            var friend = friendSnapshot.getValue(ProfileMain::class.java)
                            if (friend != null && friend.usersUid != usersUid) {
                                grpFriends.add(friend)
                            }
                        }
                        contactAdapter.notifyDataSetChanged()
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}