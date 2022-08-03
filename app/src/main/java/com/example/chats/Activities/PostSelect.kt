package com.example.chats.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chats.Models.Post
import com.example.chats.Models.ProfileMain
import com.example.chats.R
import com.example.chats.databinding.ActivityPostSelectBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_post_select.*
import kotlinx.android.synthetic.main.activity_set_up_profile.*
import kotlinx.android.synthetic.main.grpfriendlist.view.*


class PostSelect : AppCompatActivity() {
    private lateinit var binding: ActivityPostSelectBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var selectedImage: Uri? = null
    private lateinit var storage: StorageReference
    private var userUid: String? = null
    private var userName: String? = null
    private var userImage: String? = null
    private lateinit var dialog: ProgressDialog
    private lateinit var timer: CountDownTimer
    private lateinit var grpList: ArrayList<String>
    private var user: ProfileMain? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        grpList = arrayListOf()
        userUid = intent.getStringExtra("UsersUid")
        userImage = intent.getStringExtra("UsersImage")
        userName = intent.getStringExtra("UsersName")
        database = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()
        setSupportActionBar(binding.toolbar3)
        title = "  "
        binding.selectedPostBtn.setOnClickListener {
            launchGallery()
        }

        binding.userPostName.setText(userName)
        binding.userPostUid.setText(userUid)
        Glide.with(this)
            .load(userImage)
            .override(1000, 1000)
            .circleCrop()
            .placeholder(R.drawable.user)
            .into(binding.userPostImage)


        database.child("Profile").child(userUid.toString()).child("Groups")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount.equals(null)) {
                        Toast.makeText(
                            this@PostSelect,
                            "You Are Not in any Group",
                            Toast.LENGTH_SHORT
                        ).show()
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
        binding.postCaption.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var desc = binding.postCaption.text.toString()
                if (desc.isNotEmpty()) {
                    binding.finalPostSendBtn.isEnabled = true
                    binding.finalPostSendBtn.setBackgroundColor(resources.getColor(R.color.skyBlue))
                } else {
                    binding.finalPostSendBtn.isEnabled = false
                    binding.finalPostSendBtn.setBackgroundColor(resources.getColor(R.color.browser_actions_bg_grey))
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.finalPostSendBtn.setOnClickListener {
            dialogShow()
            var rand = database.push().key.toString()
            if (selectedImage != null) {
                storage = FirebaseStorage.getInstance().getReference().child("Posts")
                    .child(userUid.toString()).child(rand)
                storage.putFile(selectedImage!!).addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        storage.downloadUrl.addOnSuccessListener {
                            var post = Post(
                                rand,
                                it.toString(),
                                binding.postCaption.text.toString(),
                                userUid,
                                0,
                                0,
                                System.currentTimeMillis().toString()
                            )
                            for (grp in grpList) {
                                if (grpList.size == 0) {
                                    Toast.makeText(this, "No Group", Toast.LENGTH_SHORT).show()
                                }
                                database.child("Profile").child(userUid.toString()).child("Posts")
                                    .child(rand).setValue(post)
                                    .addOnSuccessListener {
                                        database.child("Colleges").child(grp).child("Posts")
                                            .child(rand).setValue(post)
                                            .addOnSuccessListener {
                                                dialog.cancel()
                                                Toast.makeText(
                                                    this,
                                                    "Status Updated",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                                var intent = Intent(this, MainScreen::class.java)
                                                intent.putExtra("UsersUid", userUid)
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
                binding.finalPostSendBtn.isEnabled = true
                binding.finalPostSendBtn.setBackgroundColor(resources.getColor(R.color.skyBlue))
                binding.selectedPost.setImageURI(data.data!!)
                selectedImage = data.data!!
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.post_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.addPostRequest -> {
                Toast.makeText(this, "Add Posts", Toast.LENGTH_SHORT).show()
                var intent = Intent(this, PostRequest::class.java)
                intent.putExtra("UsersUid", userUid)
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun launchGallery() {
        var intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, 100)

    }

    private fun dialogShow() {
        dialog = ProgressDialog(this)
        dialog.setMessage("Uploading posts")
        dialog.setCancelable(false)
        dialog.show()
        if (dialog.isShowing) {
            timer = object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {

                }
            }
            timer.start()
        }
    }
}