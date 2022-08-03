package com.example.chats.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chats.databinding.ActivityImageSelectBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.properties.Delegates

class ImageSelect : AppCompatActivity() {
    var selectedImage: Uri? = null
    var caption: String? = null

    private lateinit var binding: ActivityImageSelectBinding
    private lateinit var database: DatabaseReference
    private lateinit var fauth: FirebaseAuth
    private lateinit var usersUid: String
    private var galleryType:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().getReference()
        fauth = FirebaseAuth.getInstance()
        usersUid = intent.getStringExtra("UsersUid").toString()
        galleryType=intent.getStringExtra("GalleryType")!!.toInt()
//        if(galleryType==0)
//            launchCamera()
//        else if(galleryType==1)
//            launchGalleryDocuments()
//        else if(galleryType==3)
//            launchGalleryImages()
//        else if(galleryType==4)
//            launchGalleryVideos()

        launchCamera()
        binding.finalImageSendBtn.setOnClickListener {
            caption = binding.ImageCaption.text.toString()
            var intent = Intent()
            intent.putExtra("SelectedImage", selectedImage.toString())
            intent.putExtra("ImageCaption", caption.toString())
            setResult(RESULT_OK, intent)
            Toast.makeText(this, selectedImage.toString(), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                binding.selectedImage.setImageURI(data.data)
                selectedImage = data.data!!
            }
        }
    }

    private fun launchGalleryImages() {
        val intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("images/*")
        startActivityForResult(intent, 100)
    }
    private fun launchGalleryVideos() {
        var intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("videos/*")
        startActivityForResult(intent, 100)
    }
    private fun launchGalleryDocuments() {
        var intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("files/*")
        startActivityForResult(intent, 100)
    }
    private fun launchCamera() {
        var intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("files/*")
        startActivityForResult(intent, 100)
    }
}