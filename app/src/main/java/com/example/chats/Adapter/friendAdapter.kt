package com.example.chats.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chats.Activities.FriendsChat
import com.example.chats.Models.Message
import com.example.chats.Models.ProfileMain
import com.example.chats.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.grpfriendlist.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class friendAdapter(
    private var context: Context,
    var friendList: ArrayList<ProfileMain>,
    private var usersUid: String,
    private var clgUid: String
) : RecyclerView.Adapter<friendAdapter.FriendViewHolder>() {
    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.grpfriendlist, parent, false)
        return FriendViewHolder(view)
    }

    private var const: Long = 1
    private var date: DateFormat = SimpleDateFormat("hh:mm a")
    private var friendsChat:FriendsChat= FriendsChat()
    private var date2: DateFormat = SimpleDateFormat("dd MMM")

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        var friend = friendList[position]
        holder.itemView.apply {
            grpFriendName.setText(friend.name)
            Glide.with(this)
                .load(friend.image)
                .override(1000, 1000)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(grpFriendImage)
        }
        FirebaseDatabase.getInstance().getReference().child("Online_Status")
            .child(friend.usersUid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value.toString() == "Online") {
                        holder.itemView.view20.visibility = View.VISIBLE
                    } else {
                        holder.itemView.view20.visibility = View.GONE
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        FirebaseDatabase.getInstance().reference.child("Colleges").child(clgUid).child("Users")
            .child(usersUid).child("Friends").child(friend.usersUid.toString()).child("messages")
            .limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount.toInt() == 0) {
                    holder.itemView.grpFriendLastMsg.setText("Tap to chat")
                }
                for (msgSnapshot in snapshot.children) {
                    var lastMsg = msgSnapshot.getValue(Message::class.java)
                    if (lastMsg != null) {
                        holder.itemView.grpFriendLastMsg.setText(friendsChat.decryptMessage(lastMsg.msg.toString()))
                        var time1 = System.currentTimeMillis() / 86400000
                        var time2 = lastMsg.date.toString().toLong() / 86400000
                        if (time1 != time2) {
                            if (time1 - time2 == const)
                                holder.itemView.grpFriendLastMstTime.setText("Yesterday")
                            else {
                                holder.itemView.grpFriendLastMstTime.setText(
                                    date2.format(
                                        lastMsg.date.toString().toLong()
                                    ).toString()
                                )
                            }
                        } else {
                            holder.itemView.grpFriendLastMstTime.setText(
                                date.format(
                                    lastMsg.date.toString().toLong()
                                ).toString()
                            )
                        }
                    }
                }
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        holder.itemView.setOnClickListener {
            var intent = Intent(context, FriendsChat::class.java)
            intent.putExtra("UsersUid", usersUid)
            intent.putExtra("FriendUid", friend.usersUid)
            intent.putExtra("FriendName", friend.name)
            intent.putExtra("FriendImage", friend.image)
            intent.putExtra("ClgUid", clgUid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return friendList.size
    }
}