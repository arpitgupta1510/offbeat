package com.example.chats.utils

import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService

object AndroidUtils {
    private var density = 1f

    public fun dp(value: Float, context: Context): Int {
        if (density == 1f) {
            checkDisplaySize(context)
        }
        return if (value == 0f) {
            0
        } else Math.ceil((density * value).toDouble()).toInt()
    }



    private fun checkDisplaySize(context: Context) {
        try {
            density = context.resources.displayMetrics.density
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}