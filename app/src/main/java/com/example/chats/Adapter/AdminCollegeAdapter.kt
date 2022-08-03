package com.example.chats.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chats.Models.College
import com.example.chats.Activities.CollegeProfile
import com.example.chats.R
import kotlinx.android.synthetic.main.row_table.view.*

class AdminCollegeAdapter(
    private var context: Context,
    var collegeRequests: ArrayList<College>,
    private var userUid: String
) : RecyclerView.Adapter<AdminCollegeAdapter.CollegeRequestsViewHolder>() {
    class CollegeRequestsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollegeRequestsViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.row_table, parent, false)
        return CollegeRequestsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollegeRequestsViewHolder, position: Int) {
        var college = collegeRequests[position]
        holder.itemView.apply {
            friendClgName.setText(college.CollegeName)
            friendClgUid.setText(college.collegeUid)
            Glide.with(this)
                .load(college.image)
                .override(1000, 1000)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(friendClgImage)
        }
        holder.itemView.setOnClickListener {
            var intent = Intent(context, CollegeProfile::class.java)
            intent.putExtra("UsersUid", userUid)
            intent.putExtra("ClgUid", college.collegeUid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return collegeRequests.size
    }

}