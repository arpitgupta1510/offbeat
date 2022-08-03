package com.example.chats.VideoCall

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.chats.Models.ProfileMain
import com.example.chats.R
import com.example.chats.databinding.ActivityVideoMainBinding
import com.google.firebase.database.*


class VideoMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoMainBinding
    private lateinit var database: DatabaseReference
    var permissions = arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    private val requestCode = 1
    private var user: ProfileMain? = null
    private lateinit var usersUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        MobileAds.initialize(this, object : OnInitializationCompleteListener() {
//            fun onInitializationComplete(initializationStatus: InitializationStatus?) {}
//        })
//        progress = KProgressHUD.create(this)
//        progress.setDimAmount(0.5f)
//        progress.show()
        database = FirebaseDatabase.getInstance().reference
        usersUid = intent.getStringExtra("UsersUid").toString()
        database.child("Profile").child(usersUid).get().addOnSuccessListener {
            var user = it.getValue(ProfileMain::class.java)

            Glide.with(this)
                .load(user!!.image)
                .override(1000, 1000)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(binding.profilePicture)
            binding.findButton.setOnClickListener {
                if (isPermissionsGranted()) {
                    val intent = Intent(this, ConnectingActivity::class.java)
                    intent.putExtra("UsersImage", user.image)
                    intent.putExtra("UsersUid", usersUid)
                    startActivity(intent)
                    //startActivity(new Intent(MainActivity.this, ConnectingActivity.class));
                } else {
                    askPermissions()
                }
            }
        }


    }

    fun askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private fun isPermissionsGranted(): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
        }
        return true
    }
}