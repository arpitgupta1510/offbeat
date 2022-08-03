package com.example.chats.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chats.Adapter.AdminCollegeAdapter
import com.example.chats.Models.College
import com.example.chats.databinding.ActivityPendingRequestsAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PendingRequestsAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityPendingRequestsAdminBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var collegeList: ArrayList<College>
    private lateinit var collegeAdapter: AdminCollegeAdapter
    private var x: Long = 0
    private var userUid: String? = null
    private var clgUid: String? = null
    private var clgName: String? = null
    private var clgImage: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingRequestsAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fAuth = FirebaseAuth.getInstance()
        userUid = intent.getStringExtra("UsersUid")
        clgUid = intent.getStringExtra("ClgUid")
        clgName = intent.getStringExtra("ClgName")
        clgImage = intent.getStringExtra("ClgImage")
        database = FirebaseDatabase.getInstance().getReference()
        collegeList = arrayListOf()
        collegeAdapter = AdminCollegeAdapter(this, collegeList, userUid.toString())
        binding.collegeView.adapter = collegeAdapter
        binding.collegeView.layoutManager = LinearLayoutManager(this)
        database.child("Profile").child(userUid.toString()).child("GroupAdmin")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount == x) {
                        Toast.makeText(this@PendingRequestsAdmin, "No Request", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this@PendingRequestsAdmin, "Request", Toast.LENGTH_SHORT)
                            .show()
                        collegeList.clear()
                        for (clgSnapshot in snapshot.children) {
                            var clg = clgSnapshot.getValue(College::class.java)
                            if (clg != null) {
                                collegeList.add(clg)
                            }
                        }
                        collegeAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }
}