package com.example.chats.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chats.R
import com.example.chats.Models.Status
import com.example.chats.Activities.StatusShow
import com.example.chats.Models.ProfileMain
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.comment.view.*
import kotlinx.android.synthetic.main.status.view.*
import kotlinx.android.synthetic.main.useraccept.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class StatusAdapter(
    private var context: Context,
    private var statusList: ArrayList<Status>,
    private var usersUid: String
) : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {
    class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var date: DateFormat = SimpleDateFormat("hh:mm a")
    private var date2: DateFormat = SimpleDateFormat("dd MMM")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.status, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        var status = statusList[position]
        holder.itemView.apply {
            friendStatusCaption.setText(status.caption)
            var time1 = System.currentTimeMillis() / 86400000
            var time2 = status.date.toString().toLong() / 86400000
            var const: Long = 1
            if (time1 != time2) {
                if (time1 - time2 == const)
                    holder.itemView.friendStatusTime.setText("Yesterday")
                else {
                    holder.itemView.friendStatusTime.setText(
                        date2.format(
                            status.date.toString().toLong()
                        ).toString()
                    )
                }
            } else {
                holder.itemView.friendStatusTime.setText(
                    date.format(
                        status.date.toString().toLong()
                    ).toString()
                )
            }
            friendStatusTime.setText(date.format(status.date.toString().toLong()).toString())
            Glide.with(this)
                .load(status.statusUid)
                .override(1000, 1000)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(friendStatusImage)
            holder.itemView.friendName.setText(status.uploaderName)
            holder.itemView.setOnClickListener {
                var intent = Intent(context, StatusShow::class.java)
                intent.putExtra("UsersUid",usersUid)
                intent.putExtra("StatusUid", status.statusUid)
                intent.putExtra("FriendUid", status.friendUid)
                intent.putExtra("StatusTime", status.date)
                intent.putExtra("StatusCaption", status.caption)
                intent.putExtra("FriendName", status.uploaderName)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return statusList.size
    }
}