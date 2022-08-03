package com.example.chats.Activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chats.Adapter.AdminCollegeAdapter
import com.example.chats.Adapter.CollegeAdapter
import com.example.chats.Adapter.CommentAdapter
import com.example.chats.databinding.ActivityCollegeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CollegeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCollegeBinding

    //    private lateinit var database:DatabaseReference
//    private lateinit var userUid:String
//    private lateinit var grpList:ArrayList<College>
//    private lateinit var clgAdapter: CollegeAdapter
//    private lateinit var dialog: ProgressDialog
//    private lateinit var timer: CountDownTimer
    private var x: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCollegeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        database=FirebaseDatabase.getInstance().getReference()
//       userUid=intent.getStringExtra("UsersUid").toString()
//                grpList = arrayListOf()
//                clgAdapter = CollegeAdapter(this@CollegeActivity,grpList,userUid)
//                binding.clgShowView.adapter = clgAdapter
//                binding.clgShowView.layoutManager = LinearLayoutManager(this@CollegeActivity)
//                Toast.makeText(this@CollegeActivity,userUid, Toast.LENGTH_SHORT).show()
//                database.child("Profile").child(userUid.toString()).child("Groups").addValueEventListener(object :ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if(snapshot.childrenCount==x){
//                            Toast.makeText(this@CollegeActivity,"Not added in any group",Toast.LENGTH_SHORT).show()
//                        }
//                        else
//                        {
//                            grpList.clear()
//
//                            for(clgSnapshot in snapshot.children)
//                            {
//                                var clg=clgSnapshot.getValue(College::class.java)
//                                if(clg!=null){
//                                    grpList.add(clg)
//                                }
//                            }
//                            Toast.makeText(this@CollegeActivity,grpList.size.toString(),Toast.LENGTH_SHORT).show()
//                            clgAdapter.notifyDataSetChanged()
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//
//                })
//                database.child("Online_Status").child(userUid.toString()).setValue("Online")
//                database.child("Online_Status").child(userUid.toString()).onDisconnect()
//                    .setValue(System.currentTimeMillis())


    }

}