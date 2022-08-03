package com.example.chats.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chats.Models.ProfileMain
import com.example.chats.databinding.ActivityFormCollegeBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FormCollege : AppCompatActivity() {
    private lateinit var binding: ActivityFormCollegeBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var clgUid: String? = null
    private var userUid: String? = null
    private var user: ProfileMain? = null
    private var status: String? = null
    private var description: String? = null
    private var selectedImage: Uri? = null
    private lateinit var reference: StorageReference
    private lateinit var clg: Map<String, String>
    private var image: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormCollegeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference()
        userUid = intent.getStringExtra("UsersUid")
        Toast.makeText(this, userUid, Toast.LENGTH_SHORT).show()
        binding.clgImage.setOnClickListener {
            launchGallery()
        }
        database.child("Profile").child(userUid.toString()).get().addOnSuccessListener {
            user = it.getValue(ProfileMain::class.java)
            binding.updateBtn.setOnClickListener {
                clgUid = binding.clgUid.text.toString()
                database.child("ClgUid").child(clgUid.toString()).get().addOnSuccessListener {
                    var ifUid = it.value
                    Toast.makeText(this, ifUid.toString(), Toast.LENGTH_SHORT).show()
                    if (ifUid == clgUid) {
                        binding.clgName.setError("College Uid already Taken")
                        binding.clgName.setText("")
                        return@addOnSuccessListener
                    } else {
                        if (selectedImage != null) {

                            reference =
                                FirebaseStorage.getInstance().getReference().child("Profile")
                                    .child(fAuth.currentUser!!.uid)
                            reference.putFile(selectedImage!!)
                                .addOnCompleteListener(OnCompleteListener {
                                    if (it.isSuccessful) {
                                        reference.downloadUrl.addOnSuccessListener {
                                            clg = mapOf<String, String>(
                                                "collegeUid" to clgUid.toString().trim(),
                                                "CollegeName" to binding.clgName.text.toString()
                                                    .trim(),
                                                "image" to it.toString(),
                                                "GrpAdmin" to userUid.toString()
                                            )
                                            database.child("ClgUid").child(clgUid.toString())
                                                .setValue(clgUid.toString())
                                            database.child("Colleges")
                                                .child(clgUid.toString().trim()).updateChildren(clg)
                                                .addOnSuccessListener {
                                                    database.child("Colleges")
                                                        .child(clgUid.toString().trim())
                                                        .child("Users").child(userUid.toString())
                                                        .setValue(user).addOnSuccessListener {
                                                        database.child("Profile")
                                                            .child(userUid.toString())
                                                            .child("GroupAdmin")
                                                            .child(clgUid.toString()).setValue(clg)
                                                            .addOnSuccessListener {
                                                                Toast.makeText(
                                                                    this,
                                                                    "Status Uploaded",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                var intent = Intent(
                                                                    this,
                                                                    FirstActivity::class.java
                                                                )
                                                                intent.putExtra("UsersUid", clgUid)
                                                                intent.putExtra("ClgUid", clgUid)
                                                                intent.putExtra(
                                                                    "ClgName",
                                                                    binding.clgName.text.toString()
                                                                )
                                                                intent.putExtra(
                                                                    "ClgImage",
                                                                    selectedImage
                                                                )
                                                                startActivity(intent)
                                                                finish()
                                                            }
                                                    }
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
                            clg = mapOf<String, String>(
                                "collegeUid" to clgUid.toString().trim(),
                                "CollegeName" to binding.clgName.text.toString().trim(),
                            )
                            database.child("ClgUid").child(clgUid.toString())
                                .setValue(clgUid.toString())
                            database.child("Colleges").child(clgUid.toString().trim())
                                .updateChildren(clg).addOnSuccessListener {
                                database.child("Colleges").child(clgUid.toString().trim())
                                    .child("Users").setValue(userUid).addOnSuccessListener {

                                    Toast.makeText(this, "Status Uploaded", Toast.LENGTH_SHORT)
                                        .show()
                                    var intent = Intent(this, FirstActivity::class.java)
                                    intent.putExtra("UsersUid", clgUid)
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
                binding.clgImage.setImageURI(data.data)
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