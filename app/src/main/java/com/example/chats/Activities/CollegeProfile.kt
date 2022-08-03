package com.example.chats.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chats.Models.College
import com.example.chats.Models.Profile
import com.example.chats.R
import com.example.chats.databinding.ActivityCollegeProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CollegeProfile : AppCompatActivity() {
    private lateinit var binding: ActivityCollegeProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var collegeUid: String? = null
    private var college: College? = null
    private var userUid: String? = null
    private var user: Profile? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollegeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()
        userUid = intent.getStringExtra("UsersUid").toString()
        collegeUid = intent.getStringExtra("ClgUid")
        database.child("Colleges").child(collegeUid.toString()).get().addOnSuccessListener {
            college = it.getValue(College::class.java)
            binding.clgName.setText(college!!.CollegeName)
            Glide.with(this)
                .load(college!!.image)
                .override(1000, 1000)
                .placeholder(R.drawable.user)
                .into(binding.collegeImageShoww)
            database.child("Profile").child(userUid.toString()).child("Groups")
                .child(collegeUid.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            binding.sendCollegeRequest.visibility = View.GONE
                        } else {
                            database.child("Profile").child(userUid.toString()).get()
                                .addOnSuccessListener {
                                    user = it.getValue(Profile::class.java)
                                    Toast.makeText(
                                        this@CollegeProfile,
                                        user.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.sendCollegeRequest.setOnClickListener {
                                        database.child("Colleges").child(collegeUid.toString())
                                            .child("GrpAdmin").get().addOnSuccessListener {
                                                var admin = it.value.toString()
                                                Toast.makeText(
                                                    this@CollegeProfile,
                                                    admin.toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                database.child("Profile").child(admin)
                                                    .child("GroupAdmin")
                                                    .child(collegeUid.toString())
                                                    .child("RequestsPending")
                                                    .child(userUid.toString())
                                                    .setValue(user).addOnSuccessListener {
                                                        Toast.makeText(
                                                            this@CollegeProfile,
                                                            "Request Sent Successfully",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        binding.sendCollegeRequest.visibility =
                                                            View.INVISIBLE
                                                    }
                                            }

                                    }
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            database.child("Colleges").child(collegeUid.toString()).child("Posts")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var postCount = snapshot.childrenCount
                        binding.noOfGroupPosts.setText(postCount.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            database.child("Colleges").child(collegeUid.toString()).child("Users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var memberCount = snapshot.childrenCount
                        binding.noOfGroupMembers.setText(memberCount.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

        }
    }
}