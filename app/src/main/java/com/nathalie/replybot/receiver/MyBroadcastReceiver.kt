package com.nathalie.replybot.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nathalie.replybot.utils.Constants.DEBUG

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(DEBUG, "Found a broadcast event")

        val message = intent?.getStringExtra("message") ?: ""

        Log.d(DEBUG, message)
    }
}