package com.example.chats.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chats.Activities.FirstActivity
import com.example.chats.Adapter.NotificationAdapter
import com.example.chats.Models.Notification
import com.example.chats.R
import com.example.chats.databinding.FragmentNotificationBinding
import com.google.firebase.database.*

class NotificationFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    private lateinit var binding:FragmentNotificationBinding
    private lateinit var database:DatabaseReference
    private lateinit var usersUid:String
    private lateinit var clgUid:String
    private lateinit var notificationsList:ArrayList<Notification>
    private lateinit var notificationAdapter:NotificationAdapter
    private var x:Long=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentNotificationBinding.inflate(inflater,container,false)
        database=FirebaseDatabase.getInstance().reference
        usersUid=(activity as FirstActivity).userUid.toString()
        clgUid=(activity as FirstActivity).clgUid.toString()
        notificationsList= arrayListOf()
        notificationAdapter= NotificationAdapter(requireContext(),notificationsList,usersUid,clgUid)
        binding.notificationListView.layoutManager=LinearLayoutManager(context)
        binding.notificationListView.adapter=notificationAdapter
        database.child("Colleges").child(clgUid).child("Users")
            .child(usersUid).child("Notifications").limitToLast(20).addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.childrenCount==x){
                        Toast.makeText(context, "No Current Notification", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        notificationsList.clear()
                        for(notificationSnapshot in snapshot.children){
                            var notification=notificationSnapshot.getValue(Notification::class.java)
                            if(notification!=null){
                                notificationsList.add(notification)
                            }
                        }
                        notificationsList.reverse()
                        notificationAdapter.notifyDataSetChanged()

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })





        return binding.root
    }

}