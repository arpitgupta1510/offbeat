package com.example.chats.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chats.Models.Message
import com.example.chats.R
import kotlinx.android.synthetic.main.postlist.view.*

class PhotosAdapter(private var context: Context, private var photosList:ArrayList<Message>,private var usersUid:String)
    :RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>(){
    class PhotosViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.postlist,parent,false)
        return PhotosViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        var photo=photosList[position]
        holder.itemView.apply {
            Glide.with(context)
                .load(photo.image)
                .override(1000, 1000)
                .placeholder(R.drawable.user)
                .into(holder.itemView.postListShow)
        }
    }

    override fun getItemCount(): Int {
        return photosList.size
    }

}