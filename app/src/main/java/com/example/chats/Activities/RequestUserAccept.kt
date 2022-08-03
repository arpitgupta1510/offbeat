package com.example.chats.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chats.Adapter.RequestAcceptAdapter
import com.example.chats.Models.Profile
import com.example.chats.Models.ProfileMain
import com.example.chats.databinding.ActivityRequestUserAcceptBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_request_user_accept.*

class RequestUserAccept : AppCompatActivity() {
    private lateinit var binding: ActivityRequestUserAcceptBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userList: ArrayList<ProfileMain>
    private lateinit var userAdapter: RequestAcceptAdapter
    private var userUid: String? = null
    private var clgUid: String? = null
    private var clgName: String? = null
    private var clgImage: String? = null
    private lateinit var alreadyUsersList:ArrayList<String>
    private var x: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRequestUserAcceptBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()
        userUid = intent.getStringExtra("UsersUid")
        clgUid = intent.getStringExtra("ClgUid")
        clgName = intent.getStringExtra("ClgName")
        clgImage = intent.getStringExtra("ClgImage")
        userList = arrayListOf()
        alreadyUsersList= arrayListOf()
        userAdapter = RequestAcceptAdapter(
            this,
            userList,
            userUid.toString(),
            clgUid.toString(),
            clgName.toString(),
            clgImage.toString(),
            alreadyUsersList
        )
        database.child("Colleges").child(clgUid.toString()).child("Users").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                alreadyUsersList.clear()
                for (userSnapshot in snapshot.children){
                    val user=userSnapshot.key
                    if(user!=null){
                        alreadyUsersList.add(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        userView.layoutManager = LinearLayoutManager(this)
        userView.adapter = userAdapter
        database.child("Profile").child(userUid!!).child("GroupAdmin").child(clgUid!!)
            .child("RequestsPending").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount == x) {
                    Toast.makeText(this@RequestUserAccept, "No User Request", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@RequestUserAccept, "User Request", Toast.LENGTH_SHORT)
                        .show()
                    userList.clear()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(ProfileMain::class.java)
                        if (user != null) {
                            userList.add(user)
                        }
                    }
                    userAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}