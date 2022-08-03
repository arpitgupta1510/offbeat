package com.example.chats.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import androidx.annotation.NonNull
import com.example.chats.Models.ProfileMain
import com.example.chats.R
import com.example.chats.databinding.ActivityVideoCallBinding

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.annotations.NotNull
import kotlinx.android.synthetic.main.activity_video_call.*
import kotlinx.android.synthetic.main.clgnames.view.*
import java.util.*


class VideoCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoCallBinding
    private var uniqueId: String = ""
    private lateinit var database: DatabaseReference
    var auth: FirebaseAuth? = null
    var username: String? = ""
    var friendsUsername: String? = ""

    var isPeerConnected = false


    var isAudio = true
    var isVideo = true
    var createdBy: String? = null

    var pageExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference().child("users")
        username = intent.getStringExtra("username")
        val incoming = intent.getStringExtra("incoming")
        createdBy = intent.getStringExtra("createdBy")

//        friendsUsername = "";
//
//        if(incoming.equalsIgnoreCase(friendsUsername))
//            friendsUsername = incoming;
        friendsUsername = incoming
        setupWebView()
        binding.micUnmuteBtn.setOnClickListener {
            isAudio = !isAudio
            callJavaScriptFunction("javascript:toggleAudio(\"$isAudio\")")
            if (isAudio) {
                binding.micBtn.setImageResource(R.drawable.mic_on)
            } else {
                binding.micBtn.setImageResource(R.drawable.mic_on)
            }
        }


        binding.videoPauseBtn.setOnClickListener {
            isVideo = !isVideo
            callJavaScriptFunction("javascript:toggleVideo(\"$isVideo\")")
            if (isVideo) {
                binding.videoBtn.setImageResource(R.drawable.mic_on)
            } else {
                binding.videoBtn.setImageResource(R.drawable.mic_on)
            }
        }


        binding.callCutBtn.setOnClickListener {
            finish()
        }


    }

    fun setupWebView() {
        binding.webView.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }
        })
        binding.webView.getSettings().setJavaScriptEnabled(true)
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false)
//        binding.webView.addJavascriptInterface(InterfaceJava(this), "Android")
        loadVideoCall()
    }

    fun loadVideoCall() {
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
        if (createdBy.equals(username, ignoreCase = true)) {
            if (pageExit) return
            database.child(username!!).child("connId").setValue(uniqueId)
            database.child(username!!).child("isAvailable").setValue(true)
            binding.connectingImage.setVisibility(View.GONE)
            binding.controls.setVisibility(View.VISIBLE)
            FirebaseDatabase.getInstance().reference
                .child("Profile")
                .child(friendsUsername!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(@NonNull @NotNull snapshot: DataSnapshot) {
                        val user = snapshot.getValue(ProfileMain::class.java)
                        Glide.with(this@VideoCallActivity)
                            .load(user!!.image)
                            .override(1000, 1000)
                            .circleCrop()
                            .placeholder(R.drawable.user)
                            .into(userCallImage)
                        binding.userCallName.setText(user.name)
                    }

                    override fun onCancelled(@NonNull @NotNull error: DatabaseError) {}
                })
        } else {
            Handler().postDelayed(Runnable {
                friendsUsername = createdBy
                FirebaseDatabase.getInstance().reference
                    .child("Profile")
                    .child(friendsUsername!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(@NonNull @NotNull snapshot: DataSnapshot) {
                            val user = snapshot.getValue(ProfileMain::class.java)
                            Glide.with(this@VideoCallActivity)
                                .load(user!!.image)
                                .override(1000, 1000)
                                .circleCrop()
                                .placeholder(R.drawable.user)
                                .into(userCallImage)
                            binding.userCallName.setText(user.name)
                        }

                        override fun onCancelled(@NonNull @NotNull error: DatabaseError) {}
                    })
                FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(friendsUsername!!)
                    .child("connId")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(@NonNull @NotNull snapshot: DataSnapshot) {
                            if (snapshot.value != null) {
                                sendCallRequest()
                            }
                        }

                        override fun onCancelled(@NonNull @NotNull error: DatabaseError) {}
                    })
            }, 3000)
        }
    }

    fun onPeerConnected() {
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

    fun listenConnId() {
        database.child(friendsUsername!!).child("connId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(@NonNull @NotNull snapshot: DataSnapshot) {
                    if (snapshot.value == null) return
                    binding.connectingImage.setVisibility(View.GONE)
                    binding.controls.setVisibility(View.VISIBLE)
                    val connId = snapshot.getValue(String::class.java)
                    callJavaScriptFunction("javascript:startCall(\"$connId\")")
                }

                override fun onCancelled(@NonNull @NotNull error: DatabaseError) {}
            })
    }

    fun callJavaScriptFunction(function: String?) {
        binding.webView.post(Runnable {
            if (function != null) {
                binding.webView.evaluateJavascript(function, null)
            }
        })
    }

    fun getUniqueId(): String {
        return UUID.randomUUID().toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        pageExit = true
        database.child(createdBy!!).setValue(null)
        finish()
    }
}
