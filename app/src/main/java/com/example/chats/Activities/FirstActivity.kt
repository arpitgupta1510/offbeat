package com.example.chats.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.chats.*
import com.example.chats.Fragments.*
import com.example.chats.Models.Post
import com.example.chats.Models.Profile
import com.example.chats.Models.ProfileMain
import com.example.chats.R
import com.example.chats.databinding.ActivityFirstBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.android.synthetic.main.fragment_chatt.*
import kotlinx.android.synthetic.main.fragment_posts.*

class FirstActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstBinding
    private lateinit var database: DatabaseReference
    private lateinit var fauth: FirebaseAuth
    private var currentFragment: Int = 1
    var userUid: String? = null
    var clgUid: String? = null
    var clgName: String? = null
    var clgImage: String? = null
    private lateinit var chattFragment: ChattFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var postsFragment: PostsFragment
    private lateinit var statusFragment: StatusFragment
    private lateinit var notificationFragment: NotificationFragment
    private lateinit var searchingList: ArrayList<ProfileMain>
    private lateinit var searchingListPost: ArrayList<Post>
    private lateinit var searchFriend: ArrayList<ProfileMain>
    private lateinit var searchPost: ArrayList<Post>
    private lateinit var permissions: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        database = FirebaseDatabase.getInstance().getReference()
        fauth = FirebaseAuth.getInstance()
        permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_CONTACTS
        )
        userUid = intent.getStringExtra("UsersUid")
        clgUid = intent.getStringExtra("ClgUid")
        clgName = intent.getStringExtra("ClgName")
        clgImage = intent.getStringExtra("ClgImage")
        chattFragment = ChattFragment()
        profileFragment = ProfileFragment()
        statusFragment = StatusFragment()
        postsFragment = PostsFragment()
        notificationFragment= NotificationFragment()
        setCurrentFragment(chattFragment)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.chats -> {
                    setCurrentFragment(chattFragment)
                    currentFragment = 1
                }
                R.id.profile -> {
                    setCurrentFragment(profileFragment)
                    currentFragment = 2
                }
                R.id.posts -> {
                    setCurrentFragment(postsFragment)
                    currentFragment = 3
                }
                R.id.status -> {
                    setCurrentFragment(statusFragment)
                    currentFragment = 4
                }
                R.id.notifications->{
                    setCurrentFragment(notificationFragment)
                    currentFragment=5
                }
            }
            true
        }
    }

    private fun performSearch() {
        binding.friendsSearch.queryHint = "Search Here.."
        binding.friendsSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (currentFragment == 1) {
                    searchingList = chattFragment.friendList
                    search(query)
                } else if (currentFragment == 3) {
                    searchingListPost = postsFragment.postLists
                    searchPost(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (currentFragment == 1) {
                    search(newText)
                } else if (currentFragment == 3) {
                    searchPost(newText)
                }
                return true
            }
        })
    }

    private fun search(text: String?) {
        searchFriend = arrayListOf()

        text?.let {
            chattFragment.friendList.forEach { message ->
                if (message.name?.contains(text, true) == true) {
                    searchFriend.add(message)
                }
            }
            if (searchFriend.isEmpty()) {
                Toast.makeText(this, "No match found!", Toast.LENGTH_SHORT).show()
            }
            updateRecyclerView()
        }
    }

    private fun updateRecyclerView() {
        chattFragment.friendsView.apply {
            chattFragment.friendAdapter.friendList = searchFriend
            chattFragment.friendAdapter.notifyDataSetChanged()
        }
    }

    private fun searchPost(text: String?) {
        searchPost = arrayListOf()
        text?.let {
            postsFragment.postLists.forEach { message ->
                if (message.uploaderId?.contains(text, true) == true) {
                    searchPost.add(message)
                }
            }
            if (searchPost.isEmpty()) {
                Toast.makeText(this, "No match found!", Toast.LENGTH_SHORT).show()
            }
            updateRecyclerViewPost()
        }
    }

    private fun updateRecyclerViewPost() {
        postsFragment.postsView.apply {
            postsFragment.postAdapter.postList = searchPost
            postsFragment.postAdapter.notifyDataSetChanged()
//            menuInflater.inflate(R.menu.home,menu)
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.firstFragment, fragment)
            commit()
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                supportActionBar?.hide()
                performSearch()
                binding.friendsSearch.visibility = View.VISIBLE
                binding.friendsSearch.isSubmitButtonEnabled = true
                return true
            }
//            R.id.newCollege -> {
//                Toast.makeText(this, userUid.toString(), Toast.LENGTH_SHORT).show()
//                var intent = Intent(this, SelectCollege::class.java)
//                intent.putExtra("UsersUid", userUid)
//                startActivity(intent)
//                return true
//            }
            R.id.pendingRequests -> {
                Toast.makeText(this, userUid.toString(), Toast.LENGTH_SHORT).show()
                var intent = Intent(this, RequestUserAccept::class.java)
                intent.putExtra("UsersUid", userUid)
                intent.putExtra("ClgUid", clgUid)
                intent.putExtra("ClgName", clgName)
                intent.putExtra("ClgImage", clgImage)
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun fetchImageFromPreferences(userId:String):String{
        val sharedPref = getSharedPreferences("Images",
            FirebaseMessagingService.MODE_PRIVATE
        )
        return sharedPref.getString(userId, "").toString()
    }
    private fun setImageToPreferences(userId:String,imageId:String){
        var sharedPref = getSharedPreferences("", Context.MODE_PRIVATE)
        var editor = sharedPref.edit()
        editor.putString(userId, imageId)
        editor.apply()
    }
}