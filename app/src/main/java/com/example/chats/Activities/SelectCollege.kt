package com.example.chats.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chats.Adapter.AdminCollegeAdapter
import com.example.chats.Models.College
import com.example.chats.R
import com.example.chats.databinding.ActivitySelectCollegeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SelectCollege : AppCompatActivity() {
    private lateinit var binding: ActivitySelectCollegeBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var collegeName: String? = null
    private var userUid: String? = null
    private lateinit var groupList: ArrayList<College>
    private lateinit var groupsAdapter: AdminCollegeAdapter
    private lateinit var searchedGroups: ArrayList<College>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCollegeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        database = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()
        userUid = intent.getStringExtra("UsersUid").toString()
        groupList = arrayListOf()
        groupsAdapter = AdminCollegeAdapter(this, groupList, userUid.toString())
        binding.collegeViewSearch.layoutManager = LinearLayoutManager(this)
        binding.collegeViewSearch.adapter = groupsAdapter
        Toast.makeText(this, userUid, Toast.LENGTH_SHORT).show()
        database.child("Colleges").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList.clear()
                for (grpSnapshot in snapshot.children) {
                    var grp = grpSnapshot.getValue(College::class.java)
                    if (grp != null)
                        groupList.add(grp)
                }
                groupsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.searchCollege.isSubmitButtonEnabled = true
        performSearch()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.college, menu)
        return true
    }

    private fun performSearch() {
        binding.searchCollege.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        searchedGroups = arrayListOf()
        text?.trim()
        text?.let {
            groupList.forEach { message ->
                if ((message.collegeUid!!.contains(text, true)) || (message.CollegeName!!.contains(
                        text,
                        true
                    ))
                ) {
                    searchedGroups.add(message)
                }
            }
            if (searchedGroups.isEmpty()) {
                Toast.makeText(this, "No College found!", Toast.LENGTH_SHORT).show()
            }
            updateRecyclerView()
        }
    }

    private fun updateRecyclerView() {
        binding.collegeViewSearch.apply {
            groupsAdapter.collegeRequests = searchedGroups
            groupsAdapter.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.addCollege -> {
                var intent = Intent(this, FormCollege::class.java)
                intent.putExtra("UsersUid", userUid)
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}