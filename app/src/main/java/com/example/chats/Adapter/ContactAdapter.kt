package com.example.chats.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chats.Activities.FriendProfile
import com.example.chats.Models.ProfileMain
import com.example.chats.R
import kotlinx.android.synthetic.main.row_table.view.*

class ContactAdapter(
    private var context: Context,
    private var contactList: ArrayList<ProfileMain>,
    private var usersUid: String,
    private var clgUid: String
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.row_table, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        var contact = contactList[position]
        holder.itemView.apply {
            friendClgName.setText(contact.name)
            friendClgUid.setText(contact.usersUid)
            Glide.with(this)
                .load(contact.image)
                .override(1000, 1000)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(friendClgImage)
            holder.itemView.setOnClickListener {
                var intent = Intent(context, FriendProfile::class.java)
                intent.putExtra("FriendUid", contact.usersUid)
                intent.putExtra("UsersUid", usersUid)
                intent.putExtra("FriendName", contact.name)
                intent.putExtra("FriendImage", contact.image)
                intent.putExtra("ClgUid", clgUid)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

}