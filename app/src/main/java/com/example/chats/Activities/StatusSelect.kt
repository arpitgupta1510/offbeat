package com.example.chats.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chats.Models.ProfileMain
import com.example.chats.Models.Status
import com.example.chats.R
import com.example.chats.databinding.ActivityStatusSelectBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList

class StatusSelect : AppCompatActivity() {
    private lateinit var binding:ActivityStatusSelectBinding
    private lateinit var fAuth:FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var storage:StorageReference
    private var userUid:String?=null
    private var clgUid:String?=null
    private var clgName:String?=null
    private var clgImage:String?=null
    private var userName:String?=null
    private lateinit var dialog:ProgressDialog
    private lateinit var timer: CountDownTimer
    private lateinit var grpList:ArrayList<String>
    private  var selectedImage: Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStatusSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fAuth= FirebaseAuth.getInstance()
        grpList= arrayListOf()
        database=FirebaseDatabase.getInstance().getReference()
        userUid=intent.getStringExtra("UsersUid")
        clgUid=intent.getStringExtra("ClgUid")
        clgName=intent.getStringExtra("ClgName")
        clgImage=intent.getStringExtra("ClgImage")
        binding.selectedStatusBtn.setOnClickListener {
            launchGallery()
        }
        database.child("Profile").child(userUid.toString()).get().addOnSuccessListener {
            var user=it.getValue(ProfileMain::class.java)
            userName=user!!.name.toString()
            binding.userStatusName.setText(user!!.name.toString())
            binding.userStatusUid.setText(user!!.usersUid.toString())
            Glide.with(this)
                .load(user.image)
                .override(1000, 1000)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(binding.userStatusImage)
        }

        database.child("Profile").child(userUid.toString()).child("Groups").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.childrenCount.equals(null))
                {
                    Toast.makeText(this@StatusSelect,"You Are Not in any Group", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    grpList.clear()
                    for(grpSnapshot in snapshot.children)
                    {
                        var grp=grpSnapshot.key.toString()
                        grpList.add(grp)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        binding.statusCaption.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var desc=binding.statusCaption.text.toString()
                if(desc.isNotEmpty()){
                    binding.finalStatusSendBtn.isEnabled=true
                    binding.finalStatusSendBtn.setBackgroundColor(resources.getColor(R.color.skyBlue))
                }
                else{
                    binding.finalStatusSendBtn.isEnabled=false
                    binding.finalStatusSendBtn.setBackgroundColor(resources.getColor(R.color.browser_actions_bg_grey))
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.finalStatusSendBtn.setOnClickListener {
            if (selectedImage != null) {
                dialogShow()
                var rand = database.push().key.toString()
                storage = FirebaseStorage.getInstance().getReference().child("Status").child(userUid.toString())
                storage.putFile(selectedImage!!).addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        storage.downloadUrl.addOnSuccessListener {
                            var status= Status(it.toString(),userName,userUid, binding.statusCaption.text.toString(),System.currentTimeMillis().toString())
                            for(grp in grpList)
                            {
                                if(grpList.size==0)
                                {
                                    Toast.makeText(this,"No Group", Toast.LENGTH_SHORT).show()
                                }
                                database.child("Profile").child(userUid.toString()).child("Status").setValue(status)
                                    .addOnSuccessListener {
                                        database.child("Colleges").child(grp).child("Status").child(userUid.toString()).setValue(status)
                                            .addOnSuccessListener {
                                                dialog.cancel()
                                                Toast.makeText(this, "Status Updated", Toast.LENGTH_SHORT)
                                                    .show()
                                                var intent = Intent(this, FirstActivity::class.java)
                                                intent.putExtra("ClgUid",clgUid)
                                                intent.putExtra("UsersUid",userUid)
                                                intent.putExtra("ClgName",clgName)
                                                intent.putExtra("ClgImage",clgImage)
                                                startActivity(intent)
                                                finish()
                                            }
                                    }

                            }
                        }
                    }
                })
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                binding.finalStatusSendBtn.isEnabled=true
                binding.finalStatusSendBtn.setBackgroundColor(resources.getColor(R.color.skyBlue))
                binding.selectedstatus.setImageURI(data.data!!)
                selectedImage = data.data!!
            }
        }
    }

    private fun launchGallery() {
        var intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, 100)
    }
    private fun dialogShow(){
        dialog= ProgressDialog(this)
        dialog.setMessage("Uploading posts")
        dialog.setCancelable(false)
        dialog.show()
        if(dialog.isShowing){
            timer = object: CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }
                override fun onFinish() {

                }
            }
            timer.start()
        }
    }
}