package com.example.chats.Services

import android.webkit.JavascriptInterface
import com.example.chats.VideoCall.CallActivity


class InterfaceJava(var callActivity: CallActivity) {
    @JavascriptInterface
    fun onPeerConnected() {
        callActivity.onPeerConnected()
    }
}