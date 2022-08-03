package com.example.chats.Activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.chats.Adapter.PhotosAdapter
import com.example.chats.Adapter.PostListAdapter
import com.example.chats.Models.Message
import com.example.chats.R
import com.example.chats.databinding.ActivitySharedFilesBinding
import com.google.firebase.database.*

class SharedFilesActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySharedFilesBinding
    private lateinit var usersUid:String
    private lateinit var friendUid:String
    private lateinit var friendName:String
    private lateinit var clgUid:String
    private lateinit var photosList:ArrayList<Message>
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var database:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySharedFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        usersUid=intent.getStringExtra("UsersUid").toString()
        friendUid=intent.getStringExtra("FriendUid").toString()
        friendName=intent.getStringExtra("FriendName").toString()
        clgUid=intent.getStringExtra("ClgUid").toString()
        photosList= arrayListOf()
        photosAdapter = PhotosAdapter(this, photosList,usersUid)
        binding.photosView.layoutManager = GridLayoutManager(this, 3)
        binding.photosView.adapter=photosAdapter
        database =
            FirebaseDatabase.getInstance().reference.child("Colleges").child(clgUid).child("Users")
                .child(usersUid).child("Friends").child(friendUid).child("messages")
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                photosList.clear()
                for (dataSnapshot in snapshot.children) {
                    val message = dataSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        if(message.image!=null){
                            photosList.add(message)
                        }
                    }
                }
                photosAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SharedFilesActivity, "No Message sent", Toast.LENGTH_SHORT).show()
            }
        })
        photosAdapter.notifyDataSetChanged()
    }
}