package com.example.chats.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chats.Adapter.VideoAdapter
import com.example.chats.Models.VideoReels
import com.example.chats.databinding.FragmentProfileBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_form_college.*


class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var binding: FragmentProfileBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference().child("Videos")
       val options= FirebaseRecyclerOptions.Builder<VideoReels>()
           .setQuery(database,VideoReels::class.java)
           .build()
        videoAdapter= VideoAdapter(options)
        binding.videoViewPager.adapter=videoAdapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        videoAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        videoAdapter.stopListening()
    }
}


