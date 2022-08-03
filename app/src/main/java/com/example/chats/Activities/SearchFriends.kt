package com.example.chats.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import com.example.chats.databinding.ActivityFirstBinding
import com.example.chats.databinding.ActivitySearchFriendsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class SearchFriends : AppCompatActivity() {
    private lateinit var binding: ActivitySearchFriendsBinding
    private lateinit var database: DatabaseReference
    private lateinit var fauth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
//    private fun performSearch() {
//        binding.searchView.queryHint = "Search Here.."
//        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                searchingList = friendsFragment.friendList
//                search(query)
//                return true
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (currentFragment == 1) {
//                    search(newText)
//                } else if (/currentFragment == 2) {
//                    searchGroup(newText)
//                } else {
//                    searchStatus(newText)
//                }
//                return true
//            }
//        })
//    }

//    private fun search(text: String?) {
//        searchFriend = arrayListOf()
//
//        text?.let {
//            friendsFragment.friendList.forEach { message ->
//                if (message.name?.contains(text, true) == true) {
//                    searchFriend.add(message)
//                }
//            }
//            if (searchFriend.isEmpty()) {
//                Toast.makeText(this, "No match found!", Toast.LENGTH_SHORT).show()
//            }
//            updateRecyclerView()
//        }
//    }
//
//    private fun updateRecyclerView() {
//        friendsFragment.friendsView.apply {
//            friendsFragment.friendAdapter.rankList = searchFriend
//            friendsFragment.friendAdapter.notifyDataSetChanged()
//        }
//    }
}