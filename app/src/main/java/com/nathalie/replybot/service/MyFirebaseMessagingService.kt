package com.nathalie.replybot.service

import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nathalie.replybot.data.model.Token
import com.nathalie.replybot.utils.Constants.DEBUG
import com.nathalie.replybot.utils.NotificationUtils

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Firebase.firestore.collection("tokens")
            .add(Token(token)).addOnSuccessListener {
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.data["title"].toString()
        val body = message.data["body"].toString()

        if (title == "Broadcast") {
            val intent = Intent()
            intent.action = "com.replyBot.MyBroadcast"
            intent.putExtra("message", body)
            sendBroadcast(intent)
        }

        NotificationUtils.createNotification(
            this, title, body
        )
    }
}