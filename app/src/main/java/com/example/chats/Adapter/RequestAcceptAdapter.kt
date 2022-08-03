package com.example.chats.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chats.Models.Profile
import com.example.chats.Models.ProfileMain
import com.example.chats.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.useraccept.view.*

class RequestAcceptAdapter(
    private var context: Context,
    private var userList: ArrayList<ProfileMain>,
    private var userUid: String,
    private var clgUid: String,
    private var clgName: String,
    private var clgImage: String,
    private var alreadyUsersList:ArrayList<String>
) : RecyclerView.Adapter<RequestAcceptAdapter.RequestAcceptViewHolder>() {
    class RequestAcceptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private lateinit var clg: Map<String, String>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestAcceptViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.useraccept, parent, false)
        return RequestAcceptViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestAcceptViewHolder, position: Int) {
        var user = userList[position]
        holder.itemView.apply {
            requestUserName.setText(user.name)
            requestUserUid.setText(user.usersUid)
            Glide.with(this)
                .load(user.image)
                .override(1000, 1000)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(requestUseriamge)
            clg = mapOf<String, String>(
                "collegeUid" to clgUid,
                "CollegeName" to clgName,
                "image" to clgImage
            )
            database = FirebaseDatabase.getInstance().reference
            fAuth = FirebaseAuth.getInstance()
            requestAccepted.setOnClickListener {
                database.child("Colleges").child(clgUid).child("Users")
                    .child(user.usersUid.toString()).setValue(user).addOnSuccessListener {
                        database.child("Profile").child(userUid).child("GroupAdmin").child(clgUid)
                            .child("RequestsPending").child(user.usersUid.toString()).setValue(null)
                            .addOnSuccessListener {
                                database.child("Profile").child(user.usersUid.toString())
                                    .child("Groups").child(clgUid).updateChildren(clg)
                                    .addOnSuccessListener {
                                        for(i in alreadyUsersList) {
                                            database.child("Profile").child(i).child("Updates")
                                                .child(user.usersUid.toString()).setValue(user)
                                        }
                                        Toast.makeText(context, user.usersUid+" is added successfully", Toast.LENGTH_SHORT).show()
                                         alreadyUsersList.add(user.usersUid.toString())
                                    }
                            }
                    }
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}