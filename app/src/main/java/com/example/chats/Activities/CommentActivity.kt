package com.example.chats.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chats.Adapter.CommentAdapter
import com.example.chats.Models.Comment
import com.example.chats.Models.Notification
import com.example.chats.Models.Post
import com.example.chats.R
import com.example.chats.databinding.ActivityCommentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.post.view.*

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private lateinit var usersUid: String
    private lateinit var postUid: String
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentList: ArrayList<Comment>
    private lateinit var clgUid: String
    private var post: Post? = null
    private var repliedTo: String? = null
    private lateinit var friendUid:String
    private var x: Long = 0
    private var commentUpload: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        usersUid = intent.getStringExtra("UsersUid").toString()
        clgUid = intent.getStringExtra("ClgUid").toString()
        postUid = intent.getStringExtra("PostUid").toString()
        friendUid=intent.getStringExtra("FriendUid").toString()
        Toast.makeText(this, friendUid, Toast.LENGTH_SHORT).show()
        database = FirebaseDatabase.getInstance().getReference()
//        database.child("Colleges").child(clgUid).child("Posts").child(postUid).get().addOnSuccessListener {
//            post=it.getValue(Post::class.java)

        database.child("Colleges").child(clgUid).child("Posts").child(postUid)
            .get().addOnSuccessListener {
                post = it.getValue(Post::class.java)!!
                binding.postLikesCommentBtn.setText(post!!.likes.toString())
                binding.uploaderNameComment.setText(post!!.uploaderId)
                if (post!!.postCaption != null) {
                    binding.postCaptionCommentShow.visibility = View.VISIBLE
                    binding.postCaptionCommentShow.setText(post!!.postCaption.toString())
                }
                Glide.with(this@CommentActivity)
                    .load(post!!.image)
                    .override(1000, 1000)
                    .placeholder(R.drawable.user)
                    .into(binding.uploadedPostComment)
//                Glide.with(this@CommentActivity)
//                    .load(post!!.uploaderImage)
//                    .override(1000, 1000)
//                    .circleCrop()
//                    .placeholder(R.drawable.user)
//                    .into(binding.uploaderImageComment)
                database.child("Colleges").child(clgUid).child("Posts").child(postUid)
                    .child("LikedBy")
                    .child(usersUid).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                binding.postLikesCommentBtn.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.like_red, 0, 0, 0
                                )
                            } else {
                                binding.postLikesCommentBtn.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.unlike_white, 0, 0, 0
                                )
                                database.child("Colleges").child(clgUid).child("Posts")
                                    .child(postUid).child("LikedBy")
                                    .child(usersUid).setValue(true).addOnSuccessListener {
                                        database.child("Colleges").child(clgUid).child("Posts")
                                            .child(postUid).child("likes")
                                            .setValue(ServerValue.increment(1))
                                        Toast.makeText(
                                            this@CommentActivity,
                                            "LIKED",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
            }
        binding.postLikesCommentBtn.setOnClickListener {
            database.child("Colleges").child(clgUid).child("Posts").child(postUid).child("LikedBy")
                .child(usersUid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            binding.postLikesCommentBtn.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.unlike_white,
                                0,
                                0,
                                0
                            )
                            database.child("Colleges").child(clgUid).child("Posts").child(postUid)
                                .child("LikedBy")
                                .child(usersUid).setValue(null).addOnSuccessListener {
                                    database.child("Colleges").child(clgUid).child("Posts")
                                        .child(postUid).child("likes")
                                        .setValue(ServerValue.increment(-1))
                                    Toast.makeText(
                                        this@CommentActivity,
                                        "UNLIKED",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            binding.postLikesCommentBtn.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.like_red,
                                0,
                                0,
                                0
                            )
                            database.child("Colleges").child(clgUid).child("Posts").child(postUid)
                                .child("LikedBy")
                                .child(usersUid).setValue(true).addOnSuccessListener {
                                    database.child("Colleges").child(clgUid).child("Posts")
                                        .child(postUid).child("likes")
                                        .setValue(ServerValue.increment(1))
                                    Toast.makeText(
                                        this@CommentActivity,
                                        "LIKED",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

        commentList = arrayListOf()
        commentAdapter = CommentAdapter(this@CommentActivity, commentList, usersUid, clgUid)
        binding.commentView.layoutManager = LinearLayoutManager(this@CommentActivity)
        binding.commentView.adapter = commentAdapter
        database.child("Colleges").child(clgUid).child("Posts").child(postUid).child("Comments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount == x) {
                        Toast.makeText(this@CommentActivity, "No comments yet", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        commentList.clear()
                        for (commentSnapshot in snapshot.children) {
                            var comment = commentSnapshot.getValue(Comment::class.java)
                            if (comment != null) {
                                commentList.add(comment)
                            }
                        }
                        commentAdapter.notifyDataSetChanged()
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        binding.postCommentBtnn.setOnClickListener {
            commentUpload = binding.addCommentBox.text.toString().trim()
            binding.addCommentBox.setText("")
            repliedTo = commentAdapter.repliedTo
            if (repliedTo == null) {
                if (commentUpload != "") {
                    var rand = database.push().key.toString()
                    var comment = Comment(
                        usersUid,
                        System.currentTimeMillis().toString(),
                        commentUpload,
                        rand
                    )
                    database.child("Colleges").child(clgUid).child("Posts").child(postUid)
                        .child("Comments").child(rand).setValue(comment).addOnSuccessListener {
                            database.child("Colleges").child(clgUid).child("Posts").child(postUid)
                                .child("comment")
                                .setValue(ServerValue.increment(1)).addOnSuccessListener {
                                    var notification=Notification(1,usersUid,System.currentTimeMillis(),postUid,false,clgUid)
                                    var rand=database.push().key.toString()
                                    database.child("Colleges").child(clgUid).child("Users").child(friendUid).child("Notifications").child(rand).setValue(notification).addOnSuccessListener {
                                        Toast.makeText(
                                            this@CommentActivity,
                                            "Comment",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                } else {
                    Toast.makeText(
                        this@CommentActivity,
                        "Write something here...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                var rand = database.push().key.toString()
                var comment = Comment(
                    usersUid,
                    System.currentTimeMillis().toString(),
                    commentUpload,
                    rand
                )
                database.child("Colleges").child(clgUid).child("Posts").child(postUid)
                    .child("Comments").child(repliedTo.toString()).child("replies").child(rand)
                    .setValue(comment).addOnSuccessListener {
                        database.child("Colleges").child(clgUid).child("Posts").child(postUid)
                            .child("comment").setValue(ServerValue.increment(1))
                            .addOnSuccessListener {
                                Toast.makeText(this@CommentActivity, "Comment", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
            }
        }

    }

}