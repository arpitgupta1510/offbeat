package com.example.chats.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.chats.Adapter.PostListAdapter
import com.example.chats.Models.Post
import com.example.chats.databinding.ActivityMyPostsBinding
import com.google.firebase.database.*

class MyPosts : AppCompatActivity() {
    private lateinit var binding: ActivityMyPostsBinding
    private lateinit var database: DatabaseReference
    private lateinit var postList: ArrayList<Post>
    private lateinit var postListAdapter: PostListAdapter
    private lateinit var userUid: String
    private var x: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.userPostView.showShimmerAdapter()
        userUid = intent.getStringExtra("UsersUid").toString()
        postList = arrayListOf()
        postListAdapter = PostListAdapter(this, postList)
        binding.userPostView.layoutManager = GridLayoutManager(this, 3)
        database = FirebaseDatabase.getInstance().reference
        database.child("Profile").child(userUid.toString()).child("Posts")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount == x) {
                        Toast.makeText(this@MyPosts, "No Post", Toast.LENGTH_SHORT).show()
                    } else {
                        postList.clear()
                        for (postSnapshot in snapshot.children) {
                            var post = postSnapshot.getValue(Post::class.java)
                            postList.add(post!!)
                        }
                        binding.userPostView.adapter = postListAdapter
                        binding.userPostView.hideShimmerAdapter()
                        postListAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}