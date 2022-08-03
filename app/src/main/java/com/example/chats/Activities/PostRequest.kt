package com.example.chats.Activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import com.example.chats.R
import android.text.Editable
import android.text.TextWatcher
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chats.Models.Message
import com.example.chats.Models.ProfileMain
import com.example.chats.BroadCast.PostBroadCast
import com.example.chats.databinding.ActivityPostRequestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_form_college.*
import kotlinx.android.synthetic.main.activity_post_select.*
import kotlinx.android.synthetic.main.activity_set_up_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.grpfriendlist.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private var calendar: Calendar? = null
private var currentHour: Int? = null
private var currentMin: Int? = null
private var hour: Int? = null
private var min: Int? = null
private var time: Long? = null
private var am: String? = null
private lateinit var messages: ArrayList<Message>
private lateinit var rand: String
private var date: DateFormat = SimpleDateFormat("hh")
private var date2: DateFormat = SimpleDateFormat("mm")
private var date3: DateFormat = SimpleDateFormat("a")


class PostRequest : AppCompatActivity() {
    private lateinit var binding: ActivityPostRequestBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var selectedImage: Uri? = null
    private lateinit var storage: StorageReference
    private var userUid: String? = null
    private lateinit var dialog: ProgressDialog
    private lateinit var timer: CountDownTimer
    private lateinit var grpList: ArrayList<String>
    private var user: ProfileMain? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        grpList = arrayListOf()
        userUid = intent.getStringExtra("UsersUid")
        database = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()
        binding.selectedPostRequestBtn.setOnClickListener {
            launchGallery()
        }
        database.child("Profile").child(userUid.toString()).get().addOnSuccessListener {
            user = it.getValue(ProfileMain::class.java)
            binding.userPostRequestName.setText(user!!.name)
            binding.userPostRequestUid.setText(userUid)
            Glide.with(this)
                .load(user!!.image)
                .override(1000, 1000)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(binding.userPostRequestImage)
        }

        database.child("Profile").child(userUid.toString()).child("Groups")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount.equals(null)) {
                        Toast.makeText(
                            this@PostRequest,
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
        binding.postRequestCaption.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var desc = binding.postRequestCaption.text.toString()
                if (desc.isNotEmpty()) {
                    binding.finalPostRequestSendBtn.isEnabled = true
                    binding.finalPostRequestSendBtn.setBackgroundColor(resources.getColor(R.color.skyBlue))
                } else {
                    binding.finalPostRequestSendBtn.isEnabled = false
                    binding.finalPostRequestSendBtn.setBackgroundColor(resources.getColor(R.color.browser_actions_bg_grey))
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        rand = database.push().key.toString()
        binding.finalPostRequestTimeBtn.setOnClickListener {
            timeBtnClick()
        }
        binding.finalPostRequestSendBtn.setOnClickListener {
            currentHour = date.format(System.currentTimeMillis().toLong()).toString().toInt()
            currentMin = date2.format(System.currentTimeMillis().toLong()).toString().toInt()
            am = date3.format(System.currentTimeMillis().toLong()).toString()
            if (am == "pm")
                currentHour = currentHour!! + 12
            Toast.makeText(this, am, Toast.LENGTH_SHORT).show()
            time =
                (System.currentTimeMillis() + ((hour!! * 60 + min!!) - (currentHour!! * 60 + currentMin!!)) * 60000)
            binding.textView4.setText((hour.toString() + ":" + min.toString() + ":" + currentHour.toString() + ":" + currentMin.toString() + ":" + ((hour!! * 60 + min!!) - (currentHour!! * 60 + currentMin!!))).toString())
            var rand = database.push().key.toString()
            if (selectedImage != null) {
                var intent = Intent(this, PostBroadCast::class.java)
                intent.putExtra("UsersUid", userUid)
                intent.putExtra("PostCaption", binding.postRequestCaption.text.toString())
                intent.putExtra("Post", selectedImage.toString())
                var pendingIntent = PendingIntent.getBroadcast(this, 123124, intent, 0)
                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager[AlarmManager.RTC_WAKEUP, time.toString().toLong()] = pendingIntent
                Toast.makeText(
                    this,
                    "Message Sent",
                    Toast.LENGTH_SHORT
                )
                    .show()
                var i = Intent(this, FirstActivity::class.java)
                i.putExtra("UsersUid", userUid)
                startActivity(i)

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                binding.finalPostRequestSendBtn.isEnabled = true
                binding.finalPostRequestSendBtn.setBackgroundColor(resources.getColor(R.color.skyBlue))
                binding.selectedPostRequest.setImageURI(data.data!!)
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

    private fun timeBtnClick() {
        calendar = Calendar.getInstance()
        hour = calendar!!.get(Calendar.HOUR_OF_DAY)
        min = calendar!!.get(Calendar.MINUTE)
        var timePickerDialog = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                hour = hourOfDay
                min = minute
            }

        }, hour!!, min!!, false)
        timePickerDialog.show()
    }
}