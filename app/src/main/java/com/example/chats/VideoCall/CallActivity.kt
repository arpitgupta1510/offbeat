package com.example.chats.VideoCall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import android.widget.Toast

import com.google.firebase.database.FirebaseDatabase

import com.bumptech.glide.Glide

import android.webkit.WebView

import android.webkit.WebViewClient

import android.os.Build

import android.webkit.PermissionRequest

import android.webkit.WebChromeClient
import android.os.Handler
import android.view.View
import com.example.chats.*
import com.example.chats.Activities.MainScreen
import com.example.chats.Models.ProfileMain
import com.example.chats.Services.InterfaceJava
import com.example.chats.databinding.ActivityCallBinding

import kotlinx.android.synthetic.main.clgnames.view.*
import java.util.*


class CallActivity : AppCompatActivity() {
    private lateinit var binding: com.example.chats.databinding.ActivityCallBinding
    private lateinit var database: DatabaseReference
    private lateinit var userUid: String
    private lateinit var friendUid: String
    private lateinit var callId: String
    private lateinit var clgUid: String
    private var uniqueId = ""
    private lateinit var calledBy: String
    var friendsUsername: String? = ""
    var isPeerConnected = false
    var isAudio = true
    var isVideo = true
    var pageExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        database = FirebaseDatabase.getInstance().reference

        userUid = intent.getStringExtra("UsersUid").toString()
        friendUid = intent.getStringExtra("FriendUid").toString()
        calledBy = intent.getStringExtra("CalledBy").toString()
        callId = intent.getStringExtra("CallId").toString()

        friendsUsername = friendUid
        if (calledBy.equals(userUid)) {
            database.child("Call").child(friendUid).child(userUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            setupWebView()
                        } else {
                            pageExit = true
                            finish()
                            returnHomePage()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        } else {
            database.child("Call").child(userUid).child(friendUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            setupWebView()
                        } else {
                            pageExit = true
                            finish()
                            returnHomePage()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

        binding.micBtn.setOnClickListener {
            isAudio = !isAudio
            callJavaScriptFunction("javascript:toggleAudio(\"$isAudio\")")
            if (isAudio) {
                binding.micBtnn.setImageResource(R.drawable.mic_on)
            } else {
                binding.micBtnn.setImageResource(R.drawable.ic_baseline_mic_off_24)
            }
        }
        binding.videoBtn.setOnClickListener {
            isVideo = !isVideo
            callJavaScriptFunction("javascript:toggleVideo(\"$isVideo\")")
            if (isVideo) {
                binding.videoBtnn.setImageResource(R.drawable.ic_baseline_video_call_24)
            } else {
                binding.videoBtnn.setImageResource(R.drawable.ic_baseline_videocam_off_24)
            }
        }
        binding.endCall.setOnClickListener {
            returnHomePage()
        }
    }

    fun returnHomePage() {
        if (calledBy.equals(userUid)) {
            database.child("Call").child(friendUid).child(userUid).setValue(null)
                .addOnSuccessListener {
                    var intent = Intent(this, MainScreen::class.java)
                    intent.putExtra("UsersUid", userUid)
                    startActivity(intent)
                    finish()
                }

        } else {
            database.child("Call").child(userUid).child(friendUid).setValue(null)
                .addOnSuccessListener {
                    var intent = Intent(this, MainScreen::class.java)
                    intent.putExtra("UsersUid", userUid)
                    startActivity(intent)
                    finish()
                }

        }
    }

    fun deSetUpWebView() {
        binding.webView.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.deny()
                }
            }
        })
    }

    fun setupWebView() {
        Toast.makeText(this, "Web View", Toast.LENGTH_SHORT).show()
        binding.webView.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }
        })
        binding.webView.getSettings().setJavaScriptEnabled(true)
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false)
        binding.webView.addJavascriptInterface(InterfaceJava(this), "Android")
        loadVideoCall()
    }

    private fun loadVideoCall() {
        Toast.makeText(this, "Video call", Toast.LENGTH_SHORT).show()
        val filePath = "file:android_asset/call.html"
        binding.webView.loadUrl(filePath)
        binding.webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                initializePeer()
            }
        })
    }


    fun initializePeer() {
        uniqueId = getUniqueId()
        callJavaScriptFunction("javascript:init(\"$uniqueId\")")
        if (calledBy.equals(userUid, ignoreCase = true)) {
            if (pageExit) return
            Toast.makeText(this, "Peer", Toast.LENGTH_SHORT).show()
            database.child("Call").child(friendUid).child(userUid).child("connId")
                .setValue(uniqueId)
//            database.child(username!!).child("isAvailable").setValue(true)
            binding.loadingGroup.setVisibility(View.GONE)
            binding.controls.setVisibility(View.VISIBLE)
            FirebaseDatabase.getInstance().reference
                .child("Profile")
                .child(friendUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user: ProfileMain? = snapshot.getValue(ProfileMain::class.java)
                        Glide.with(this@CallActivity)
                            .load(user!!.image)
                            .override(1000, 1000)
                            .circleCrop()
                            .placeholder(R.drawable.user)
                            .into(binding.profile)
                        binding.name.setText(user.name)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } else {
            Handler().postDelayed(Runnable {
                FirebaseDatabase.getInstance().reference
                    .child("Profile")
                    .child(calledBy)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user: ProfileMain? = snapshot.getValue(ProfileMain::class.java)
                            Glide.with(this@CallActivity)
                                .load(user!!.image)
                                .override(1000, 1000)
                                .circleCrop()
                                .placeholder(R.drawable.user)
                                .into(binding.profile)
                            binding.name.setText(user.name)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                FirebaseDatabase.getInstance().reference
                    .child("Call")
                    .child(userUid)
                    .child(friendUid)
                    .child("connId")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.value != null) {
                                Toast.makeText(
                                    this@CallActivity,
                                    "Call Request",
                                    Toast.LENGTH_SHORT
                                ).show()
                                sendCallRequest()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }, 3000)
        }
    }

    fun onPeerConnected() {
        Toast.makeText(this, "Peer Connected", Toast.LENGTH_SHORT).show()
        isPeerConnected = true
    }

    fun sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(
                this,
                "You are not connected. Please check your internet.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        listenConnId()
    }

    private fun listenConnId() {
        Toast.makeText(this@CallActivity, "Connection id", Toast.LENGTH_SHORT).show()
        database.child("Call").child(userUid).child(friendUid).child("connId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) return
                    binding.loadingGroup.setVisibility(View.GONE)
                    binding.controls.setVisibility(View.VISIBLE)
                    val connId = snapshot.getValue(String::class.java)
                    callJavaScriptFunction("javascript:startCall(\"$connId\")")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun callJavaScriptFunction(function: String?) {
        binding.webView.post(Runnable {
            if (function != null) {
                binding.webView.evaluateJavascript(function, null)
            }
        })
    }

    private fun getUniqueId(): String {
        return UUID.randomUUID().toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        pageExit = true
        finish()
    }


}