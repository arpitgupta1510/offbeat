package com.example.chats.Models

data class Post(
    val postsUid: String? = null,
    val image: String? = null,
    val postCaption: String? = null,
    val uploaderId: String? = null,
    val likes: Long = 0,
    val comment: Long = 0,
    val date: String? = null
)