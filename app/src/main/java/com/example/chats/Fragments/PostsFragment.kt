package com.example.chats.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chats.Activities.FirstActivity
import com.example.chats.Adapter.PostsAdapter
import com.example.chats.Models.Post
import com.example.chats.databinding.FragmentPostsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.chats.R
import android.content.Intent
import android.view.*
import com.example.chats.Activities.RequestUserAccept
import com.example.chats.Activities.SelectCollege
import com.example.chats.Models.Profile
import com.example.chats.SettingsActivity


class PostsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    private lateinit var binding: FragmentPostsBinding
    private lateinit var database: DatabaseReference
    private lateinit var fauth: FirebaseAuth
    lateinit var postLists: ArrayList<Post>
    lateinit var postAdapter: PostsAdapter
    private var userUid: String? = null
    private var clgUid: String? = null
    private var x: Long = 0
    private var isLoadedOnce:Boolean=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)
        if(!isLoadedOnce){
            binding.postsView.showShimmerAdapter()
        }
        database = FirebaseDatabase.getInstance().reference
        fauth = FirebaseAuth.getInstance()
        userUid = (activity as FirstActivity).userUid.toString()
        clgUid = (activity as FirstActivity).clgUid.toString()
        Toast.makeText(context, userUid, Toast.LENGTH_SHORT).show()
        Toast.makeText(context, clgUid, Toast.LENGTH_SHORT).show()
        postLists = arrayListOf()
        postAdapter = PostsAdapter(requireContext(), postLists, userUid!!, clgUid!!)

        binding.postsView.layoutManager = LinearLayoutManager(context)
        database.child("Colleges").child(clgUid!!).child("Posts").limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount == x) {
                        Toast.makeText(context, "No Post Available", Toast.LENGTH_SHORT).show()
                    } else {
                        postLists.clear()
                        for (postSnapshot in snapshot.children) {
                            val post = postSnapshot.getValue(Post::class.java)
                            if (post != null) {
                                postLists.add(post)
                            }
                        }
                        postLists.reverse()
                        if(!isLoadedOnce){
                            binding.postsView.adapter = postAdapter
                            binding.postsView.hideShimmerAdapter()
                            isLoadedOnce=true
                        }

                        postAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_post, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.settings->{
                var intent= Intent(context, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.starredPosts->
            {
                Toast.makeText(context, "Starred posts", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        isLoadedOnce=false
    }


}