package com.example.chats.Activities

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chats.Adapter.messageAdapter
import com.example.chats.Models.Message
import com.example.chats.R
import com.example.chats.databinding.ActivityMessageMainGroupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_message_main_group.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class messageActivityMainGroup : AppCompatActivity() {
    private lateinit var binding: ActivityMessageMainGroupBinding
    private lateinit var database: DatabaseReference

    //    private lateinit var storage: StorageReference
    private lateinit var database2: DatabaseReference
    private lateinit var fauth: FirebaseAuth
    private var isTyping: Boolean = false

    //    private var requestCode: Int = 1
//    private lateinit var permissions: Array<String>
//    private lateinit var handler: Handler
    private lateinit var messages: ArrayList<Message>
    private lateinit var messagesId: ArrayList<String>
    //    private lateinit var requestMessages: ArrayList<Message>
//    private lateinit var searchmessages: ArrayList<Message>
    private lateinit var msgAdapter: messageAdapter

    //    private lateinit var memberAdapter:GroupMembers
//    private lateinit var membersList:ArrayList<ProfileMain>
    private val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    private var selectedImage: Uri? = null
    private var image: String? = null

    //    private lateinit var phone: String
    private var value: Int = 0
    private val requestCall = 1
    private var caption: String? = null
    private final var delay: Long = 1500
    private lateinit var message: Message
    private lateinit var rand: String
    private lateinit var usersUid: String
    private lateinit var clgUid: String
    private lateinit var clgImage: String
    private lateinit var clgName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageMainGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.searchMessage.invalidate()

        usersUid = intent.getStringExtra("UsersUid").toString()
        clgUid = intent.getStringExtra("ClgUid").toString()
        clgImage = intent.getStringExtra("ClgImage").toString()
        clgName = intent.getStringExtra("ClgName").toString()
        Toast.makeText(this, clgName, Toast.LENGTH_SHORT).show()
//        image = intent.getStringExtra("profile").toString()
//        receiverId = intent.getStringExtra("uid").toString()
//        receiverName = intent.getStringExtra("name").toString()
//        phone = intent.getStringExtra("phone").toString()
//        database2.child("Online_Status").get().addOnSuccessListener {
//            var isOnline = it.child(receiverId).value
//            if (isOnline == null) {
//                binding.onlineStatus.setText(null)
//            } else {
//                binding.onlineStatus.visibility = View.VISIBLE
//                binding.onlineStatus.setText(isOnline.toString())
//            }
//        }

//        senderRoom = senderId + receiverId
//        receiverRoom = receiverId + senderId
        messages = arrayListOf()
        messagesId = arrayListOf()
//        requestMessages = arrayListOf()
        var linearLayoutmanager = LinearLayoutManager(this)
        linearLayoutmanager.stackFromEnd = true
        binding.mainCollegeChatView.layoutManager = linearLayoutmanager
        msgAdapter = messageAdapter(this, messages, usersUid,messagesId)
        binding.mainCollegeChatView.adapter = msgAdapter
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = null
            Glide.with(this@messageActivityMainGroup)
                .load(clgImage)
                .override(120, 120)
                .circleCrop()
//                .placeholder(com.google.firebase.database.R.drawable.)
                .into(mainCollegeImage)
            binding.title.text = clgName
        }
//        permissions = arrayOf(
//            android.Manifest.permission.CAMERA,
//            android.Manifest.permission.RECORD_AUDIO,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            android.Manifest.permission.READ_CONTACTS
//        )
//        binding.toolbar.setOnClickListener {
//            var intent = Intent(this, FriendProfile::class.java)
//            intent.putExtra("uid", receiverId)
//            intent.putExtra("name", receiverName)
//            intent.putExtra("profile", image)
//            startActivity(intent)
//        }
//        Toast.makeText(this, receiverName, Toast.LENGTH_SHORT).show()
        database = FirebaseDatabase.getInstance().getReference().child("Colleges").child(clgUid)
            .child("Chats")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for (dataSnapshot in snapshot.children) {
                    var message = dataSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        messages.add(message)
//                        if (message.senderId.equals(receiverId))
//                            database.child(message.msgId.toString()).child("seen").setValue(true)
                    }
                }
                msgAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@messageActivityMainGroup, "No Message sent", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        msgAdapter.setOnItemClickListener(object : messageAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(this@messageActivityMainGroup, "1", Toast.LENGTH_SHORT).show()

            }

        })
//        val startForResult =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//                if (result.resultCode == Activity.RESULT_OK) {
//                    val intent = result.data
//                    if (intent != null) {
//                        selectedImage = intent.getStringExtra("SelectedImage").toString().toUri()
//                        caption = intent.getStringExtra("ImageCaption").toString()
//                        rand = database.push().key.toString()
//                        storage = FirebaseStorage.getInstance().getReference().child(senderRoom)
//                            .child(rand.toString())
//                        storage.putFile(selectedImage!!).addOnCompleteListener(OnCompleteListener {
//                            if (it.isSuccessful) {
//                                storage.downloadUrl.addOnSuccessListener {
//                                    val message = Message(
//                                        it.toString(),
//                                        caption,
//                                        senderId,
//                                        receiverName,
//                                        System.currentTimeMillis().toString(),
//                                        false,
//                                        rand
//                                    )
//                                    database2.child("Profile").child(senderId).child("friend")
//                                        .child(receiverId).child("messages")
//                                        .child(rand).setValue(message).addOnSuccessListener {
//                                            database2.child("Profile").child(receiverId)
//                                                .child("friend").child(senderId).child("messages")
//                                                .child(rand).setValue(message)
//                                                .addOnSuccessListener {
//                                                    Toast.makeText(
//                                                        this,
//                                                        "Message sent",
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                }
//                                        }
//                                }
//                            }
//                        })
//                    }
//                }
//            }
//        binding.mainCollegefileSendBtn.setOnClickListener {
//            startForResult.launch(Intent(this, PostSelect::class.java))
//        }
//        membersList= arrayListOf()
//        memberAdapter=GroupMembers(this,membersList,usersUid,clgUid)
//        binding.grpMembersView.layoutManager=LinearLayoutManager(this)
//        binding.grpMembersView.adapter=memberAdapter
//        binding.mainCollegeMsgBox.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
////                database.child("IsTyping").setValue("typing..")
//
//                if(s!=null)
//                {
////                    var str=s.toString()
////                    if(str.get(str.length-1).toString().equals("@"))
////                    {
//
////                        Toast.makeText(applicationContext, str.get(str.length-1).toString(), Toast.LENGTH_SHORT)
////                            .show()
////                        database.child("Colleges").child(clgUid).child("Users").addValueEventListener(object :ValueEventListener{
////                            override fun onDataChange(snapshot: DataSnapshot) {
////                                membersList.clear()
////                                for(memberSnapshot in snapshot.children){
////                                    var user=memberSnapshot.getValue(ProfileMain::class.java)
////                                    if(user!=null)
////                                    {
////                                        membersList.add(user)
////                                    }
////                                }
////                                memberAdapter.notifyDataSetChanged()
////                            }
////
////                            override fun onCancelled(error: DatabaseError) {
////
////                            }
////
////                        })
////                    }
//                }
//            }
//
//            var timer = Timer()
//
//            override fun afterTextChanged(s: Editable?) {
//                if (!isTyping) {
//                    isTyping = true
//                }
//                timer.cancel()
//                timer = Timer()
//                timer.schedule(
//                    object : TimerTask() {
//                        override fun run() {
//                            isTyping = false
//                            database.child("IsTyping").setValue(null)
//                        }
//                    },
//                    delay
//                )
//            }
//        })
//        binding.mainCollegeMsgBox.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                database2.child("Chats").child(senderRoom).child(fauth.currentUser!!.uid)
//                    .setValue("typing..")
//            }
//
//            var timer = Timer()
//
//            override fun afterTextChanged(s: Editable?) {
//                if (!isTyping) {
//                    isTyping = true
//                }
//                timer.cancel()
//                timer = Timer()
//                timer.schedule(
//                    object : TimerTask() {
//                        override fun run() {
//                            isTyping = false
//                            database2.child("Chats").child(senderRoom)
//                                .child(fauth.currentUser!!.uid).setValue(null)
//                        }
//                    },
//                    delay
//                )
//            }
//        })
//        database2.child("Chats").child(receiverRoom).get().addOnSuccessListener {
//            var typing = it.child(receiverId).value
//        }
        binding.mainGroupMsgSentBtn.setOnClickListener {
            var newMsg = binding.mainCollegeMsgBox.text
            rand = database.push().key.toString()
            if (newMsg == null) {
                Toast.makeText(this, "Type something", Toast.LENGTH_SHORT).show()
            } else {
                binding.mainCollegeMsgBox.setText("")
                var rand = database.push().key.toString()
                message = Message(
                    null,
                    null,
                    newMsg.toString().trim(),
                    usersUid,
                    System.currentTimeMillis().toString(),
                    false,
                    rand
                )
                if (rand != null) {
                    database2.child("Colleges").child(clgUid).child("Chats")
                        .child(rand).setValue(message).addOnSuccessListener {
                            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
//            R.id.call -> {
//                makePhoneCall()
//                Toast.makeText(applicationContext, "Call", Toast.LENGTH_LONG).show()
//                return true
//            }
//            R.id.video -> {
//                Toast.makeText(applicationContext, "Video", Toast.LENGTH_LONG).show()
//                if (isPermissionGranted()) {
////                    var intent = Intent(this, ::class.java)
////                    startActivity(intent)
//                } else {
//                    askPermission()
//                }
//                return false
//            }
            R.id.mediaBox -> {
//                var intent = Intent(this, friendsFragment::class.java)
//                startActivity(intent)
                return true
            }
            R.id.mute -> {
//                var intent = Intent(this, friendsFragment::class.java)
//                startActivity(intent)
                return true
            }
//            R.id.clear -> {
//                val dialogBuilder = AlertDialog.Builder(this)
//                dialogBuilder.setMessage("Do you want to Clear all chats?")
//                    .setCancelable(false)
//                    .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
//                        database2.child("Profile").child(senderId).child("friend").child(receiverId)
//                            .child("messages").setValue(null).addOnSuccessListener {
//                                Toast.makeText(this, "Chats Clear", Toast.LENGTH_SHORT).show()
//                            }
//                    })
//                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
//                        dialog.cancel()
//                        Toast.makeText(this, " No Chats Clear", Toast.LENGTH_SHORT).show()
//                    })
//
//                val alert = dialogBuilder.create()
//                alert.show()
//                return true
//            }
//            R.id.messageRequest -> {
//                var intent = Intent(this, MessageRequest::class.java)
//                intent.putExtra("recieverUid", receiverId)
//                intent.putExtra("senderId", senderId)
//                startActivity(intent)
//                return true
//            }
//            R.id.search -> {
//                supportActionBar?.hide()
//                binding.searchMessage.visibility = View.VISIBLE
//                binding.searchMessage.isSubmitButtonEnabled = true
//                performSearch()
//                return true
//            }

//            R.id.reply -> {
//                Toast.makeText(this, "Reply", Toast.LENGTH_SHORT).show()
//                return true
//            }
//            R.id.favourite -> {
//                Toast.makeText(this, "Add to Favourite", Toast.LENGTH_SHORT).show()
//                return true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }
//    private fun performSearch() {
//        binding.searchMessage.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                search(query)
//                return true
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//                search(newText)
//                return true
//            }
//        })
//    }

//    private fun search(text: String?) {
//        searchmessages = arrayListOf()
//
//        text?.let {
//            messages.forEach { message ->
//                if (message.msg?.contains(text, true) == true) {
//                    searchmessages.add(message)
//                }
//            }
//            if (searchmessages.isEmpty()) {
//                Toast.makeText(this, "No match found!", Toast.LENGTH_SHORT).show()
//            }
////            updateRecyclerView()
//        }
//    }

//    private fun makePhoneCall() {
//        if (true) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    android.Manifest.permission.CALL_PHONE
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(android.Manifest.permission.CALL_PHONE),
//                    requestCall
//                )
//            } else {
//                val dial = "tel:$phone"
//                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
//            }
//        } else {
//            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show()
//        }
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String?>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == requestCall) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                makePhoneCall()
//            } else {
//                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

//    private fun updateRecyclerView() {
//        binding.mainCollegeChatView.apply {
//            msgAdapter.msgList = searchmessages
//            msgAdapter.notifyDataSetChanged()
//        }
//    }

//    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Void? {
//        super.onActivityResult(requestCode, resultCode, data)
//        Toast.makeText(this, "Activity", Toast.LENGTH_SHORT).show()
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                selectedImage = intent.getStringExtra("SelectedImage")?.toUri()!!
//                caption = intent.getStringExtra("Caption").toString()
//                Toast.makeText(this, "sending Image", Toast.LENGTH_SHORT).show()
//                sendImage()
//            }
//        }
//        return null
//    }

//    private fun sendImage() {
//        var rand = database.push().key.toString()
//        storage =
//            FirebaseStorage.getInstance().getReference().child(senderRoom).child(rand.toString())
//        if (selectedImage != null) {
//            Toast.makeText(this, "Not null", Toast.LENGTH_SHORT).show()
//            storage.putFile(selectedImage!!).addOnCompleteListener(OnCompleteListener {
//                if (it.isSuccessful) {
//                    storage.downloadUrl.addOnSuccessListener {
//                        val message = Message(
//                            it.toString(),
//                            caption,
//                            fauth.currentUser!!.uid,
//                            fauth.currentUser!!.uid,
//                            System.currentTimeMillis().toString(),
//                            false,
//                            rand
//                        )
//                        database.child("Profile").child(senderId).child("friend").child(receiverId)
//                            .child("messages").child(rand).setValue(message).addOnSuccessListener {
//                                database.child("Profile").child(receiverId).child("friend")
//                                    .child(senderId).child("messages").child(rand).setValue(message)
//                                    .addOnSuccessListener {
//                                        val intent = Intent()
//                                        intent.putExtra("SelectedImage", selectedImage)
//                                        Toast.makeText(this, "Image Send", Toast.LENGTH_SHORT)
//                                            .show()
//                                        finish()
//                                    }
//                            }
//                    }
//                }
//            })
//        }
//    }

    fun Activity.hideSoftKeyboard(editText: EditText) {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }

//    private fun askPermission() {
//        ActivityCompat.requestPermissions(this, permissions, requestCode)
//    }

//    private fun isPermissionGranted(): Boolean {
//        for (permission in permissions) {
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    permission
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return false
//            }
//        }
//        return true
//    }
}

