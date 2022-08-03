package com.example.chats.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.chats.Models.VideoReels
import com.example.chats.R
import com.example.chats.databinding.VideoShowRowBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.exoplayer2.ExoPlayer
import com.google.firebase.FirebaseOptions

class VideoAdapter(options:FirebaseRecyclerOptions<VideoReels>)
    :FirebaseRecyclerAdapter<VideoReels,VideoAdapter.VideoViewHolder>(options){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.video_show_row,parent,false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int, videoModel: VideoReels) {
        holder.setData(videoModel)

    }
    inner class VideoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        lateinit var videoView:VideoView
        var videoDescription:TextView = itemView.findViewById(R.id.videoDescription)
        var videoUploaderName:TextView = itemView.findViewById(R.id.videoUploaderName)
        var videoLikeBtn:ImageView = itemView.findViewById(R.id.videoLikeBtn)
        var isFav:Boolean=false
        fun setData(obj:VideoReels){
            videoView.setVideoPath(obj.url)
            videoDescription.setText(obj.desc)
            videoUploaderName.setText(obj.title)
            videoView.setOnPreparedListener{
                it.start()
            }
            videoView.setOnCompletionListener {
                it.start()
            }
            if(!isFav){
                videoLikeBtn.setImageResource(R.drawable.like_red)
                isFav=true
            }else{
                videoLikeBtn.setImageResource(R.drawable.unlike_white)
                isFav=false
            }



        }
        init {
            videoView=itemView.findViewById(R.id.videoView)
        }
    }

}