package com.example.chats.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chats.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var usersUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()
        usersUid=readLastButtonPressed()
        if(usersUid!=null && usersUid!=""){
            var intent = Intent(this@SplashActivity, MainScreen::class.java)
            intent.putExtra("UsersUid", usersUid)
            startActivity(intent)
            finish()
        }
        else{
            var intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
        }
//        if (fAuth.currentUser == null) {
//            var intent = Intent(this@SplashActivity, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        database.child("UsersUid").child(fAuth.currentUser!!.uid)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//
//                        usersUid = snapshot.value.toString()
//
//                    } else {
//
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//
//            })
    }
    fun updateUserProfile(userUid:String,newImage:String){
        var sharedPref = getSharedPreferences("User Info", Context.MODE_PRIVATE)
        var editor = sharedPref.edit()
        editor.putString(userUid, newImage)
        editor.apply()
    }
    fun readLastButtonPressed(): String? {
        val sharedPref = getSharedPreferences("User Info", MODE_PRIVATE)
        return sharedPref.getString("UsersUid", "")
    }

}