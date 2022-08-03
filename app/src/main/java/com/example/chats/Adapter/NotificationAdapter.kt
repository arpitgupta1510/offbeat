package com.example.chats.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chats.Activities.CommentActivity
import com.example.chats.Models.Notification
import com.example.chats.Models.ProfileMain
import com.example.chats.R
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.android.synthetic.main.comment.view.*
import kotlinx.android.synthetic.main.grpfriendlist.view.*
import kotlinx.android.synthetic.main.notification.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class NotificationAdapter(private var context:Context,private var notificationList:ArrayList<Notification>,private var usersUid:String,private var clgUid:String)
    :RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>(){
    class NotificationViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
       var view=LayoutInflater.from(context).inflate(R.layout.notification,parent,false)
        return NotificationViewHolder(view)
    }
    private var date: DateFormat = SimpleDateFormat("hh:mm a")
    private var date2: DateFormat = SimpleDateFormat("dd MMM")
    private var database:DatabaseReference=FirebaseDatabase.getInstance().reference
    var userImage:String?=null
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
         var notification=notificationList[position]
        holder.itemView.apply {
            notificationSenderName.setText(notification.notificationBy)
            if(notification.notificationType==1){
                notificationTitle.setText("Commented On Your Post")
            }
            else{
                notificationTitle.setText("Hello")
            }
            var time1 = System.currentTimeMillis() / 86400000
            var time2 = notification.date.toString().toLong() / 86400000
            var const: Long = 1
            if (time1 != time2) {
                if (time1 - time2 == const)
                    holder.itemView.notificationTime.setText("Yesterday")
                else {
                    holder.itemView.notificationTime.setText(
                        date2.format(
                            notification.date.toString().toLong()
                        ).toString()
                    )
                }
            } else {
                holder.itemView.notificationTime.setText(
                    date.format(
                        notification.date.toString().toLong()
                    ).toString()
                )
            }
            holder.itemView.setOnClickListener {
                var intent= Intent(context,CommentActivity::class.java)
                intent.putExtra("UsersUid",usersUid)
                intent.putExtra("FriendUid",notification.notificationBy)
                intent.putExtra("ClgUid",clgUid)
                intent.putExtra("PostUid",notification.postId)
                context.startActivity(intent)
            }
            userImage=fetchImageFromPreferences(notification.notificationBy.toString())
            if(userImage==""){
                database.child("Profile").child(notification.notificationBy.toString()).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                         var user=snapshot.getValue(ProfileMain::class.java)
                        Toast.makeText(context, user!!.image, Toast.LENGTH_SHORT).show()
                            Glide.with(context)
                            .load(user!!.image)
                            .override(1000, 1000)
                            .circleCrop()
                            .placeholder(R.drawable.user)
                            .into(holder.itemView.notificationSenderImage)
                        user.image?.let {
                            setImageToPreferences(notification.notificationBy.toString(),
                                it
                            )
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
            else{
                Glide.with(context)
                    .load(userImage)
                    .override(1000, 1000)
                    .circleCrop()
                    .placeholder(R.drawable.user)
                    .into(holder.itemView.notificationSenderImage)
            }
        }
    }
     private fun fetchImageFromPreferences(userId:String):String{
        val sharedPref = context.getSharedPreferences("Images",
            FirebaseMessagingService.MODE_PRIVATE
        )
        return sharedPref.getString(userId, "").toString()
    }
    private fun setImageToPreferences(userId:String,imageId:String){
        var sharedPref = context.getSharedPreferences("Images", Context.MODE_PRIVATE)
        var editor = sharedPref.edit()
        editor.putString(userId, imageId)
        editor.apply()
    }


    override fun getItemCount(): Int {
        return notificationList.size
    }
}