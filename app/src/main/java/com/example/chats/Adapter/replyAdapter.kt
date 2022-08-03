package com.example.chats.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chats.Models.Comment
import com.example.chats.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.comment.view.*
import kotlinx.android.synthetic.main.reply.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class replyAdapter(
    private var context: Context,
    private var commentList: ArrayList<Comment>,
    private var usersUid: String
) : RecyclerView.Adapter<replyAdapter.ReplyViewHolder>() {
    class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var date: DateFormat = SimpleDateFormat("hh:mm a")
    private var database: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private var date2: DateFormat = SimpleDateFormat("dd MMM")
    var repliedTo: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.reply, parent, false)
        return ReplyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        var comment = commentList[position]
        holder.itemView.apply {
            replyUploaderName.setText(comment.uploadedBy)
            uploadedReply.setText(comment.comment)
            var time1 = System.currentTimeMillis() / 86400000
            var time2 = comment.time.toString().toLong() / 86400000
            var const: Long = 1
            if (time1 != time2) {
                if (time1 - time2 == const)
                    holder.itemView.commentUploadedTime.setText("Yesterday")
                else {
                    holder.itemView.commentUploadedTime.setText(
                        date2.format(
                            comment.time.toString().toLong()
                        ).toString()
                    )
                }
            } else {
                holder.itemView.commentUploadedTime.setText(
                    date.format(
                        comment.time.toString().toLong()
                    ).toString()
                )
            }
            database.child("Profile").child(usersUid).child("image").get().addOnSuccessListener {
                var image = it.getValue()
                Glide.with(this)
                    .load(image)
                    .override(1000, 1000)
                    .circleCrop()
                    .placeholder(R.drawable.user)
                    .into(replyUploaderImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}