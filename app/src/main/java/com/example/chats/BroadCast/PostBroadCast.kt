package com.example.chats.BroadCast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import com.example.chats.Models.Post
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PostBroadCast : BroadcastReceiver() {
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var usersUid: String
    private lateinit var friendUid: String
    private lateinit var clgUid: String
    private lateinit var selectedImage: Uri
    private lateinit var postCaption: String
    private lateinit var grpList: ArrayList<String>
    private lateinit var rand: String
    private var friendsToken: String? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Message", Toast.LENGTH_LONG).show()
        grpList = arrayListOf()
        database = FirebaseDatabase.getInstance().getReference()
        storage = FirebaseStorage.getInstance().getReference()
        usersUid = intent?.getStringExtra("UsersUid").toString()
        selectedImage = intent?.getStringExtra("Post").toString().toUri()
        postCaption = intent?.getStringExtra("PostCaption").toString()
        rand = database.push().key.toString()
        database.child("Profile").child(usersUid.toString()).child("Groups")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount.equals(null)) {
                        Toast.makeText(context, "You Are Not in any Group", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        grpList.clear()
                        for (grpSnapshot in snapshot.children) {
                            var grp = grpSnapshot.key.toString()
                            grpList.add(grp)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        storage = FirebaseStorage.getInstance().getReference().child("Posts")
            .child(usersUid.toString()).child(rand)
        storage.putFile(selectedImage!!).addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful) {
                storage.downloadUrl.addOnSuccessListener {
                    var post = Post(
                        rand,
                        it.toString(),
                        postCaption,
                        usersUid,
                        0,
                        0,
                        System.currentTimeMillis().toString()
                    )
                    for (grp in grpList) {
                        if (grpList.size == 0) {
                            Toast.makeText(context, "No Group", Toast.LENGTH_SHORT).show()
                        }
                        database.child("Profile").child(usersUid.toString()).child("Posts")
                            .child(rand).setValue(post)
                            .addOnSuccessListener {
                                database.child("Colleges").child(grp).child("Posts")
                                    .child(rand).setValue(post)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Status Updated",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                            }
                    }
                }
            }
        })

    }

}