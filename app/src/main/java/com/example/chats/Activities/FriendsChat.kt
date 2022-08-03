package com.example.chats.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import com.example.chats.R
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chats.Adapter.GroupMembers
import com.example.chats.Models.PushNotification
import com.example.chats.Adapter.messageAdapter
import com.example.chats.Models.Message
import com.example.chats.Models.NotificationData
import com.example.chats.Models.ProfileMain
import com.example.chats.Models.Call
import com.example.chats.Services.RetrofitInstance
import com.example.chats.Services.FirebaseServices
import com.example.chats.VideoCall.CallActivity
import com.example.chats.databinding.ActivityFriendsChatBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.shain.messenger.MessageSwipeController
import com.vanniktech.emoji.EmojiPopup
import kotlinx.android.synthetic.main.activity_friends_chat.*
import kotlinx.android.synthetic.main.activity_message_main_group.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import org.checkerframework.checker.units.qual.s
import android.view.*
import android.widget.*


const val TOPIC = "/topics/myTopic"

class FriendsChat : AppCompatActivity() {
    private lateinit var binding: ActivityFriendsChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var database2: DatabaseReference
    private lateinit var fauth: FirebaseAuth
    private var isTyping: Boolean = false
    private var requestCode: Int = 1
    private lateinit var permissions: Array<String>
    private lateinit var handler: Handler
    private lateinit var messages: ArrayList<Message>
    private lateinit var memberAdapter: GroupMembers
    private lateinit var membersList: ArrayList<ProfileMain>
    private lateinit var searchmessages: ArrayList<Message>
    private lateinit var msgAdapter: messageAdapter
    private var repliedTo: String? = null
    private var galleryType:Int=0
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    private var date: DateFormat = SimpleDateFormat("hh:mm a")
    private var selectedImage: Uri? = null
    private var image: String? = null
    private lateinit var phone: String
    private val requestCall = 1
    private var caption: String? = null
    private final var delay: Long = 1500
    private lateinit var message: Message
    private lateinit var rand: String
    private lateinit var clgUid: String
    private lateinit var usersUid: String
    private lateinit var friendUid: String
    private lateinit var friendImage: String
    private lateinit var friendName: String
    private var friendsToken: String? = null
    private var isValue: Boolean = false
    private var value: String = ""
    private var str: String = ""
    private var TAG = "FriendsChat"
    private lateinit var isChecked: ArrayList<Boolean>
    private lateinit var checked: MutableSet<Long>
    private lateinit var msgIdList: ArrayList<String>
    private var posOfAnd: Int = -1
    lateinit var linearLayoutManager: LinearLayoutManager

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseServices.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        binding.friendChatSearchMessage.invalidate()
        database2 = FirebaseDatabase.getInstance().reference
        fauth = FirebaseAuth.getInstance()
        getExtraFromIntent()
        Toast.makeText(this, friendName, Toast.LENGTH_SHORT).show()
        friendsToken()
        phone = fauth.currentUser!!.phoneNumber.toString()
        database2.child("Online_Status").child(friendUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isOnline = snapshot.value
                    if (isOnline != null) {
                        binding.friendOnlineStatus.visibility = View.VISIBLE

                        if (isOnline.toString() != "Online") {
                            var secs =
                                (System.currentTimeMillis() - isOnline.toString().toLong()) / 1000
                            if (secs >= 86400) {
                                binding.friendOnlineStatus.setText("Last seen ${secs / 86400} days ago")
                            } else {
                                binding.friendOnlineStatus.setText(
                                    "Last seen at " + date.format(
                                        isOnline
                                    ).toString()
                                )
                            }
                        } else {
                            binding.friendOnlineStatus.setText(isOnline.toString())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        database2.child("Call").child(usersUid).child(friendUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isCall = snapshot.getValue(Call::class.java)
                    if (isCall != null) {
                        binding.callView.visibility = View.VISIBLE
                        binding.acceptCall.setOnClickListener {
                            val intent = Intent(this@FriendsChat, CallActivity::class.java)
                            intent.putExtra("ClgUid", clgUid)
                            intent.putExtra("UsersUid", usersUid)
                            intent.putExtra("FriendUid", friendUid)
                            intent.putExtra("CalledBy", friendUid)
                            intent.putExtra("CallId", isCall.callId)
                            startActivity(intent)

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        checked = mutableSetOf()
        messages = arrayListOf()
        msgIdList = arrayListOf()
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        binding.friendChatView.layoutManager = linearLayoutManager
        msgAdapter = messageAdapter(this, messages, usersUid, msgIdList)
        binding.friendChatView.adapter = msgAdapter
        setDefaultActionBar()
        permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_CONTACTS
        )
        binding.toolbar.setOnClickListener {
            val intent = Intent(this, FriendProfile::class.java)
            intent.putExtra("ClgUid", clgUid)
            intent.putExtra("UsersUid", usersUid)
            intent.putExtra("FriendUid", friendUid)
            intent.putExtra("FriendImage", friendImage)
            intent.putExtra("FriendName", friendName)
            startActivity(intent)
        }
        isChecked = arrayListOf()
        database =
            FirebaseDatabase.getInstance().reference.child("Colleges").child(clgUid).child("Users")
                .child(usersUid).child("Friends").child(friendUid).child("messages")
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                isChecked.clear()
                msgIdList.clear()
                for (dataSnapshot in snapshot.children) {
                    val message = dataSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                        msgIdList.add(message.msgId.toString())
                        isChecked.add(false)
                        if (!message.senderId.equals(usersUid))
                            database.child(message.msgId.toString()).child("seen").setValue(true)
                    }
                }
                msgAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FriendsChat, "No Message sent", Toast.LENGTH_SHORT).show()
            }
        })
        val messageSwipeController = MessageSwipeController(this, object : SwipeControllerActions {
            override fun showReplyUI(position: Int) {
                showQuotedMessage(messages[position])
                txtQuotedMsg.text = decryptMessage(messages[position].msg.toString())
                reply_layout.visibility = View.VISIBLE
            }
        })
        val itemTouchHelper = ItemTouchHelper(messageSwipeController)
        itemTouchHelper.attachToRecyclerView(binding.friendChatView)

        binding.cancelButton.setOnClickListener {
            hideReplyLayout()
        }

        msgAdapter.setOnItemClickListener(object : messageAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                if (!isChecked[position]) {
                    isChecked[position] = !isChecked[position]
                    checked.add(position.toLong())
                    binding.toolbar.visibility = View.GONE
                    binding.toolbar2.visibility = View.VISIBLE
                    setActionBar()
                } else {
                    isChecked[position] = !isChecked[position]
                    checked.remove(position.toLong())
                    if (checked.size == 0) {
                        binding.toolbar2.visibility = View.GONE
                        binding.toolbar.visibility = View.VISIBLE
                        setDefaultActionBar()
                    } else {
                        binding.toolbar.visibility = View.GONE
                        binding.toolbar2.visibility = View.VISIBLE
                    }
                }
            }
        })
        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (intent != null) {
                        selectedImage = intent.getStringExtra("SelectedImage").toString().toUri()
                        caption = intent.getStringExtra("ImageCaption").toString()
                        rand = database.push().key.toString()
                        storage =
                            FirebaseStorage.getInstance().reference.child(usersUid).child("Chats")
                                .child(rand)
                        storage.putFile(selectedImage!!)
                            .addOnCompleteListener(OnCompleteListener { it1 ->

                                if (it1.isSuccessful) {
                                    storage.downloadUrl.addOnSuccessListener {
                                        val message = Message(
                                            repliedTo,
                                            it.toString(),
                                            caption,
                                            usersUid,
                                            System.currentTimeMillis().toString(),
                                            false,
                                            rand
                                        )
                                        messages.add(message)
                                        msgAdapter.notifyDataSetChanged()
                                        binding.friendChatView.smoothScrollToPosition(messages.size - 1)
                                        database2.child("Colleges").child(clgUid).child("Users")
                                            .child(usersUid).child("Friends").child(friendUid)
                                            .child("messages")
                                            .child(rand).setValue(message).addOnSuccessListener {
                                                database2.child("Colleges").child(clgUid)
                                                    .child("Users").child(friendUid)
                                                    .child("Friends").child(usersUid)
                                                    .child("messages")
                                                    .child(rand).setValue(message)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            this,
                                                            "Message sent",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }
                                    }
                                } else {
                                    Toast.makeText(this, "Problem Occurring", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                    }
                }
            }
        binding.friendChatFileSendBtn.setOnClickListener {
//
            val popupMenu: PopupMenu = PopupMenu(this,binding.friendChatFileSendBtn)
            popupMenu.menuInflater.inflate(R.menu.popup,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.openCamera ->{
                        galleryType=0
                        val intent = Intent(this, ImageSelect::class.java)
                        intent.putExtra("UsersUid", usersUid)
                        intent.putExtra("GalleryType",galleryType)
                        startForResult.launch(intent)
                    }
                    R.id.openFiles -> {
                        galleryType = 1
                        val intent = Intent(this, ImageSelect::class.java)
                        intent.putExtra("UsersUid", usersUid)
                        intent.putExtra("GalleryType",galleryType)
                        startForResult.launch(intent)
                    }
                    R.id.openDocs-> {
                        galleryType = 2
                        val intent = Intent(this, ImageSelect::class.java)
                        intent.putExtra("UsersUid", usersUid)
                        intent.putExtra("GalleryType",galleryType)
                        startForResult.launch(intent)
                    }
                    R.id.openImages-> {
                        galleryType = 3
                        val intent = Intent(this, ImageSelect::class.java)
                        intent.putExtra("UsersUid", usersUid)
                        intent.putExtra("GalleryType",galleryType)
                        startForResult.launch(intent)
                    }
                    R.id.openVideos->{
                        galleryType=4
                        val intent = Intent(this, ImageSelect::class.java)
                        intent.putExtra("UsersUid", usersUid)
                        intent.putExtra("GalleryType",galleryType)
                        startForResult.launch(intent)
                    }
                }

                true
            })
            popupMenu.show()

        }

        membersList = arrayListOf()
        memberAdapter = GroupMembers(this, membersList, usersUid, clgUid)
        binding.groupMembers.layoutManager = LinearLayoutManager(this)
        binding.groupMembers.adapter = memberAdapter
        database2.child("Colleges").child(clgUid).child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    membersList.clear()
                    for (memberSnapshot in snapshot.children) {
                        val user = memberSnapshot.getValue(ProfileMain::class.java)
                        if (user != null) {
                            membersList.add(user)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        binding.friendMsgBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                database2.child("Colleges").child(clgUid).child("Users")
                    .child(usersUid).child("Friends").child(friendUid).child("IsTyping")
                    .setValue("typing..")
//                Toast.makeText(
//                    this@FriendsChat,
//                    s.toString() + " " + start + " " + before + " " + count,
//                    Toast.LENGTH_SHORT
//                ).show()
//                if (s.toString() != "") {
//                    str = s.toString()
//                    if (str.get(str.length - 1).toString().equals("&")) {
//                        binding.groupMembers.visibility = View.VISIBLE
//                        memberAdapter.notifyDataSetChanged()
//                    }
//                    else{
//                        binding.groupMembers.visibility = View.GONE
//                    }
//                }
//                else{
//                    binding.groupMembers.visibility = View.GONE
//                }
//                if (s.toString() != "") {
//                    str = s.toString()
//                    if (start < str.length || (posOfAnd<str.length && posOfAnd>=0) ) {
//                        if (str.get(start).toString().equals("&")) {
//                            posOfAnd=start
//                            Toast.makeText(
//                                this@FriendsChat,
//                                "Last Position $start",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            binding.groupMembers.visibility = View.VISIBLE
//                        }
//                        else if(str.get(posOfAnd).toString().equals("&") ){
//                            Toast.makeText(
//                                this@FriendsChat,
//                                "Last Position $posOfAnd",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }else {
//                            binding.groupMembers.visibility = View.GONE
//                        }
//                    } else {
//                        binding.groupMembers.visibility = View.GONE
//                    }
//
//                } else {
//                    binding.groupMembers.visibility = View.GONE
//                }
            }

            var timer = Timer()
            override fun afterTextChanged(s: Editable?) {
                if (!isTyping) {
                    isTyping = true
                }
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            isTyping = false
                            database2.child("Colleges").child(clgUid).child("Users")
                                .child(usersUid).child("Friends").child(friendUid).child("IsTyping")
                                .setValue(null)
                        }
                    },
                    delay
                )
            }
        })
        database2.child("Colleges").child(clgUid).child("Users")
            .child(friendUid).child("Friends").child(usersUid).child("IsTyping")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.friendOnlineStatus.setText("typing")
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        val popup = EmojiPopup.Builder.fromRootView(binding.root).build(binding.friendMsgBox)
        binding.stickersBtn.setOnClickListener {
            popup.toggle()
        }
        binding.friendChatMsgSentBtn.setOnClickListener {
            val newMsg = binding.friendMsgBox.text.toString().trim()
            Toast.makeText(this, encryptMessage(newMsg), Toast.LENGTH_SHORT).show()
            rand = database.push().key.toString()
            if (newMsg == "") {
                Toast.makeText(this, "Type something", Toast.LENGTH_SHORT).show()
            } else {
                binding.friendMsgBox.setText("")

                val rand = database.push().key.toString()
                message = Message(
                    repliedTo,
                    null,
                    encryptMessage(newMsg),
                    usersUid,
                    System.currentTimeMillis().toString(),
                    false,
                    rand
                )
                hideReplyLayout()
                messages.add(message)
                msgAdapter.notifyDataSetChanged()
                binding.friendChatView.smoothScrollToPosition(messages.size - 1)
                database2.child("Colleges").child(clgUid).child("Users").child(usersUid)
                    .child("Friends").child(friendUid).child("messages")
                    .child(rand).setValue(message).addOnSuccessListener {
                        database2.child("Colleges").child(clgUid).child("Users").child(usersUid)
                            .child("Friends").child(friendUid).child("lastMsgTime")
                            .setValue(System.currentTimeMillis()).addOnSuccessListener {
                                database2.child("Colleges").child(clgUid).child("Users")
                                    .child(friendUid).child("Friends").child(usersUid)
                                    .child("lastMsgTime").setValue(System.currentTimeMillis())
                                    .addOnSuccessListener {
                                        database2.child("Colleges").child(clgUid).child("Users")
                                            .child(friendUid).child("Friends").child(usersUid)
                                            .child("messages")
                                            .child(rand).setValue(message)
                                            .addOnSuccessListener {
                                            }
                                    }
                                if (friendsToken != null) {
                                    PushNotification(
                                        NotificationData(
                                            "1",
                                            usersUid,
                                            message.msg.toString(),
                                            friendUid,
                                            ""
                                        ),
                                        friendsToken.toString()
                                    ).also { sendNotification(it) }
                                } else {
                                    friendsToken()
                                }

                                Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
                            }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Message Not sent", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    private fun encryptMessage(s:String):String
    {
        val ini = "11111111"
        var cu = 0
        val arr = IntArray(11111111)
        for (i in s.indices) {
            arr[i] = s[i].code
            cu++
        }
        var res = ""
        val bin = IntArray(11111)
        var idx = 0
        for (i1 in 0 until cu) {
            var temp = arr[i1]
            for (j in 0 until cu) bin[j] = 0
            idx = 0
            while (temp > 0) {
                bin[idx++] = temp % 2
                temp = temp / 2
            }
            var dig = ""
            var temps: String
            for (j in 0..6) {
                temps = Integer.toString(bin[j])
                dig = dig + temps
            }
            var revs = ""
            for (j in dig.length - 1 downTo 0) {
                val ca = dig[j]
                revs = revs + ca.toString()
            }
            res = res + revs
        }
        res = ini + res
        return res
    }
    public fun decryptMessage(s:String):String
    {
        val invalid = "Invalid Code"

        // create the same initial
        // string as in encode class

        // create the same initial
        // string as in encode class
        val ini = "11111111"
        var flag = true

        // run a loop of size 8

        // run a loop of size 8
        for (i in 0..7) {
            // check if the initial value is same
            if (ini[i] != s[i]) {
                flag = false
                break
            }
        }
        var `val` = ""

        // reverse the encrypted code

        // reverse the encrypted code
        for (i in 8 until s.length) {
            val ch: Char = s[i]
            `val` = `val` + ch.toString()
        }

        // create a 2 dimensional array

        // create a 2 dimensional array
        val arr = Array(11101) { IntArray(8) }
        var ind1 = -1
        var ind2 = 0

        // run a loop of size of the encrypted code

        // run a loop of size of the encrypted code
        for (i in 0 until `val`.length) {

            // check if the position of the
            // string if divisible by 7
            if (i % 7 == 0) {
                // start the value in other
                // column of the 2D array
                ind1++
                ind2 = 0
                val ch = `val`[i]
                arr[ind1][ind2] = ch - '0'
                ind2++
            } else {
                // otherwise store the value
                // in the same column
                val ch = `val`[i]
                arr[ind1][ind2] = ch - '0'
                ind2++
            }
        }
        // create an array
        // create an array
        val num = IntArray(11111)
        var nind = 0
        var tem = 0
        var cu = 0

        // run a loop of size of the column

        // run a loop of size of the column
        for (i in 0..ind1) {
            cu = 0
            tem = 0
            // convert binary to decimal and add them
            // from each column and store in the array
            for (j in 6 downTo 0) {
                val tem1 = Math.pow(2.0, cu.toDouble()).toInt()
                tem += arr[i][j] * tem1
                cu++
            }
            num[nind++] = tem
        }
        var ret = ""
        var ch: Char
        // convert the decimal ascii number to its
        // char value and add them to form a decrypted
        // string using conception function
        // convert the decimal ascii number to its
        // char value and add them to form a decrypted
        // string using conception function
        for (i in 0 until nind) {
            ch = num[i].toChar()
            ret = ret + ch.toString()
        }
        Log.e("dec", "text 11 - $ret")

        // check if the encrypted code was
        // generated for this algorithm

        // check if the encrypted code was
        // generated for this algorithm
        return if (`val`.length % 7 == 0 && flag == true) {
            // return the decrypted code
            ret
        } else {
            // otherwise return an invalid message
            invalid
        }
    }

    fun getExtraFromIntent() {
        clgUid = intent.getStringExtra("ClgUid").toString()
        usersUid = intent.getStringExtra("UsersUid").toString()
        friendUid = intent.getStringExtra("FriendUid").toString()
        friendImage = intent.getStringExtra("FriendImage").toString()
        friendName = intent.getStringExtra("FriendName").toString()
    }

    fun setActionBar() {
        setSupportActionBar(binding.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = null
        }
    }

    private fun hideReplyLayout() {
        binding.replyLayout.visibility = View.GONE
        repliedTo = null
    }

    private fun showQuotedMessage(message: Message) {
        repliedTo = message.msgId
        binding.friendMsgBox.requestFocus()
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(
            binding.friendMsgBox,
            InputMethodManager.SHOW_IMPLICIT
        )//Write whatever to want to do after delay specified (1 sec)

    }

    fun setDefaultActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = null
            Glide.with(this@FriendsChat)
                .load(friendImage)
                .override(120, 120)
                .circleCrop()
                .into(friendChatImage)
            binding.friendChatName.text = friendName
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (checked.size == 0)
            menuInflater.inflate(R.menu.message, menu)
        else menuInflater.inflate(R.menu.message2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.call -> {
                var callId = System.currentTimeMillis().toString()
                val room: HashMap<String, Any> = HashMap()
                room["calledBy"] = usersUid
                room["isAvailable"] = true
                room["callId"] = callId
                database2.child("Call").child(friendUid).child(usersUid).setValue(room)
                    .addOnSuccessListener {
                        if (friendsToken != null) {
                            PushNotification(
                                NotificationData(
                                    "2",
                                    usersUid,
                                    usersUid + " is calling you..",
                                    friendUid,
                                    callId
                                ),
                                friendsToken.toString()
                            ).also { sendNotification(it) }
                        } else {
                            friendsToken()
                        }
                        var intent = Intent(this, CallActivity::class.java)
                        intent.putExtra("ClgUid", clgUid)
                        intent.putExtra("UsersUid", usersUid)
                        intent.putExtra("FriendUid", friendUid)
                        intent.putExtra("CalledBy", usersUid)
                        intent.putExtra("CallId", callId)
                        startActivity(intent)

                    }
                return true
            }
            R.id.mediaBox -> {
                var intent = Intent(this, SharedFilesActivity::class.java)
                intent.putExtra("ClgUid", clgUid)
                intent.putExtra("UsersUid", usersUid)
                intent.putExtra("FriendUid", friendUid)
                startActivity(intent)
                return true
            }

            R.id.clear -> {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("Do you want to Clear all chats?")
                    .setCancelable(false)
                    .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
                        database2.child("Colleges").child(clgUid).child("Users").child(usersUid)
                            .child("Friends").child(friendUid)
                            .child("PreviousMessages").setValue(messages).addOnSuccessListener {
                                database2.child("Colleges").child(clgUid).child("Users")
                                    .child(usersUid).child("Friends").child(friendUid)
                                    .child("messages").removeValue().addOnSuccessListener {
                                        Toast.makeText(this, "Chats Clear", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                        Toast.makeText(this, " No Chats Clear", Toast.LENGTH_SHORT).show()
                    })

                val alert = dialogBuilder.create()
                alert.show()
                return true
            }
            R.id.messageRequest -> {
                val intent = Intent(this, MessageRequests::class.java)
                intent.putExtra("UsersUid", usersUid)
                intent.putExtra("friendUid", friendUid)
                intent.putExtra("ClgUid", clgUid)
                startActivity(intent)
                return true
            }
            R.id.search -> {
                supportActionBar?.hide()
                binding.friendChatSearchMessage.visibility = View.VISIBLE
                binding.friendChatSearchMessage.isSubmitButtonEnabled = true
                performSearch()
                return true
            }
            R.id.AddToFavorite -> {
                for (i in checked) {
                    val selectedMessageUid = messages[i.toInt()].msgId.toString()
                    database2.child("Colleges").child(clgUid).child("Users").child(usersUid)
                        .child("Starred Messages").child(selectedMessageUid)
                        .setValue(messages[i.toInt()])
                }
                checked.clear()
                Toast.makeText(this, "Messages Starred", Toast.LENGTH_SHORT).show()
                binding.toolbar2.visibility = View.GONE
                binding.toolbar.visibility = View.VISIBLE
                setDefaultActionBar()

                return true
            }
            R.id.deleteMessage -> {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("Do you want to Clear all chats?")
                    .setCancelable(false)
                    .setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, id ->
                        for (i in checked) {
                            val deletedMessageUid = messages[i.toInt()].msgId.toString()
                            database.child(deletedMessageUid).setValue(null)
                        }
                        checked.clear()
                        Toast.makeText(this, "Messages Deleted", Toast.LENGTH_SHORT).show()
                        binding.toolbar2.visibility = View.GONE
                        binding.toolbar.visibility = View.VISIBLE
                        setDefaultActionBar()
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                        Toast.makeText(this, "No Message Deleted", Toast.LENGTH_SHORT).show()
                    })

                val alert = dialogBuilder.create()
                alert.show()
                return true
            }
            R.id.mute -> {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("Do you want to mute Notifications from user")
                    .setCancelable(false)
                    .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
                        var sharedPref =
                            getSharedPreferences("Mute Notifications", Context.MODE_PRIVATE)
                        var editor = sharedPref.edit()
                        editor.putBoolean(friendUid, true)
                        editor.apply()
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                        Toast.makeText(this, "Not Muted", Toast.LENGTH_SHORT).show()
                    })

                val alert = dialogBuilder.create()
                alert.show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performSearch() {
        binding.friendChatSearchMessage.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText)
                return true
            }
        })
    }

    private fun search(text: String?) {
        searchmessages = arrayListOf()

        text?.let {
            messages.forEach { message ->
                if (message.msg?.contains(text, true) == true) {
                    searchmessages.add(message)
                }
            }
            if (searchmessages.isEmpty()) {
                Toast.makeText(this, "No match found!", Toast.LENGTH_SHORT).show()
            }
            updateRecyclerView()
        }
    }

    private fun makePhoneCall() {
        if (true) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    requestCall
                )
            } else {
                val dial = "tel:$phone"
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            }
        } else {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCall) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.friendChatView.apply {
            msgAdapter.msgList = searchmessages
            msgAdapter.notifyDataSetChanged()
        }
    }

    fun Activity.hideSoftKeyboard(editText: EditText) {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private fun isPermissionGranted(): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d(TAG, "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e(TAG, response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

    private fun friendsToken() {
        database2.child("Profile").child(friendUid).child("token")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friendsToken = snapshot.value.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}

