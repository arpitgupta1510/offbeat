package com.example.chats.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chats.Models.College
import com.example.chats.Activities.FirstActivity
import com.example.chats.R
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.android.synthetic.main.clgnames.view.*

class CollegeAdapter(
    private var context: Context,
    private var clgList: ArrayList<College>,
    private var usersUid: String
) : RecyclerView.Adapter<CollegeAdapter.CollegeViewHolder>() {
    class CollegeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollegeViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.clgnames, parent, false)
        return CollegeViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: CollegeViewHolder, position: Int) {
        var clg = clgList[position]
        holder.itemView.apply {
            collegeIdShow.setText(clg.collegeUid)
            collegeNameShow.setText(clg.CollegeName)
            Glide.with(this)
                .load(clg.image)
                .override(1000, 1000)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(collegeImageShow)

        }
        holder.itemView.setOnClickListener {
            var intent = Intent(context, FirstActivity::class.java)
            intent.putExtra("UsersUid", usersUid)
            intent.putExtra("ClgUid", clg.collegeUid)
            intent.putExtra("ClgName", clg.CollegeName)
            intent.putExtra("ClgImage", clg.image)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return clgList.size
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
}