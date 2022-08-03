package com.example.chats.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chats.Models.Comment
import com.example.chats.Models.ProfileMain
import com.example.chats.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.comment.view.*
import kotlinx.android.synthetic.main.notification.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class CommentAdapter(
    private var context: Context,
    private var commentList: ArrayList<Comment>,
    private var usersUid: String,
    private var clgUid: String
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var date: DateFormat = SimpleDateFormat("hh:mm a")
    private var date2: DateFormat = SimpleDateFormat("dd MMM")
    private var database: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    var repliedTo: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        var comment = commentList[position]

        holder.itemView.apply {
            holder.itemView.commentUploaderName.setText(comment.uploadedBy)
            holder.itemView.uploadedComment.setText(comment.comment)
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
            holder.itemView.commentReply.setOnClickListener {
                repliedTo = comment.commentId
            }
            holder.itemView.viewReplies.setOnClickListener {
                repliesView.visibility = View.VISIBLE
            }
            database.child("Profile").child(comment.uploadedBy.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var image = snapshot.getValue(ProfileMain::class.java)
                        if (image != null) {
                            Glide.with(context)
                                .load(image.image)
                                .override(1000, 1000)
                                .circleCrop()
                                .placeholder(R.drawable.user)
                                .into(holder.itemView.commentUploaderImage)
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}