package com.example.chats.Activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chats.databinding.ActivitySetUpProfileBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SetUpProfile : AppCompatActivity() {
    private lateinit var binding: ActivitySetUpProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var status: String? = null
    private var description: String? = null
    private var selectedImage: Uri? = null
    private lateinit var reference: StorageReference
    private lateinit var user: Map<String, String>
    private var image: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference()
        database.child("UsersUid").child(fAuth.currentUser!!.uid).get().addOnSuccessListener {
            val userUid = it.value

            if (userUid != null) {
                Toast.makeText(this, userUid.toString(), Toast.LENGTH_SHORT).show()
                var sharedPref = getSharedPreferences("User Info", Context.MODE_PRIVATE)
                var editor = sharedPref.edit()
                editor.putString("UsersUid", userUid.toString())
                editor.apply()
                var intent = Intent(this, MainScreen::class.java)
                intent.putExtra("UsersUid", userUid.toString())
                startActivity(intent)
                finish()
            } else {
                binding.userImage.setOnClickListener {
                    launchGallery()
                }
                Toast.makeText(this, "Button", Toast.LENGTH_SHORT).show()
                binding.updateBtn.setOnClickListener {
                    var userUid = binding.userName.text.toString()
                    database.child("UsersUid").child(userUid.toString()).get()
                        .addOnSuccessListener {
                            var ifUid = it.value
                            Toast.makeText(this, ifUid.toString(), Toast.LENGTH_SHORT).show()
                            if (ifUid == userUid) {
                                binding.userName.setError("Username already Taken")
                                binding.userName.setText("")
                                return@addOnSuccessListener
                            } else {
                                if (selectedImage != null) {

                                    reference = FirebaseStorage.getInstance().getReference()
                                        .child("Profile")
                                        .child(fAuth.currentUser!!.uid)
                                    reference.putFile(selectedImage!!)
                                        .addOnCompleteListener(OnCompleteListener {
                                            if (it.isSuccessful) {
                                                reference.downloadUrl.addOnSuccessListener {
                                                    user = mapOf<String, String>(
                                                        "usersUid" to userUid.toString().trim(),
                                                        "status" to binding.status.text.toString()
                                                            .trim(),
                                                        "description" to binding.description.text.toString()
                                                            .trim(),
                                                        "name" to binding.Name.text.toString()
                                                            .trim(),
                                                        "image" to it.toString()
                                                    )
                                                    database.child("UsersUid")
                                                        .child(fAuth.currentUser!!.uid)
                                                        .setValue(userUid.toString())
                                                    database.child("UsersUid")
                                                        .child(userUid.toString())
                                                        .setValue(userUid.toString())
                                                    database.child("Profile")
                                                        .child(userUid.toString().trim())
                                                        .updateChildren(user).addOnSuccessListener {
                                                        Toast.makeText(
                                                            this,
                                                            "Status Uploaded",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                            var sharedPref = getSharedPreferences("User Info", Context.MODE_PRIVATE)
                                                            var editor = sharedPref.edit()
                                                            editor.putString("UsersUid", userUid)
                                                            editor.apply()
                                                        var intent =
                                                            Intent(this, MainScreen::class.java)
                                                        intent.putExtra("UserUid", userUid)
                                                        startActivity(intent)
                                                        finish()
                                                    }.addOnFailureListener {
                                                        Toast.makeText(
                                                            this,
                                                            "Error In Updating ",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                            .show()
                                                    }
                                                }
                                            }
                                        })
                                } else {
                                    user = mapOf<String, String>(
                                        "usersUid" to userUid.toString().trim(),
                                        "status" to binding.status.text.toString().trim(),
                                        "description" to binding.description.text.toString().trim(),
                                        "name" to binding.Name.text.toString().trim(),
                                    )
                                    database.child("UsersUid").child(fAuth.currentUser!!.uid)
                                        .setValue(userUid.toString())
                                    database.child("UsersUid").child(userUid.toString())
                                        .setValue(userUid.toString())
                                    database.child("Profile").child(userUid.toString().trim())
                                        .updateChildren(user).addOnSuccessListener {
                                        Toast.makeText(this, "Status Uploaded", Toast.LENGTH_SHORT)
                                            .show()
                                        var intent = Intent(this, MainScreen::class.java)
                                        intent.putExtra("UserUid", userUid)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                        }

                }

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                binding.userImage.setImageURI(data.data)
                selectedImage = data.data
            }
        }
    }

    private fun launchGallery() {
        var intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, 100)
    }
}