package com.nathalie.replybot.service

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.nathalie.replybot.data.model.WearableNotification
import com.nathalie.replybot.utils.Constants.DEBUG
import com.nathalie.replybot.utils.NotificationUtils

class NotificationService : NotificationListenerService() {
    private lateinit var intent: Intent
    lateinit var bundle: Bundle
    private lateinit var title: String
    private lateinit var wNotification: WearableNotification
    private lateinit var msg: String
    private lateinit var replyText: String

    override fun onCreate() {
        super.onCreate()
        Log.d(DEBUG, "Running")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        Log.d(DEBUG, "Found a notification")

        wNotification = NotificationUtils.getWearableNotification(sbn) ?: return

        checkTitle()
        checkMsg()
        createIntentBundle()
        wNotificationPendingIntent(sbn)
    }

    private fun checkTitle() {
        title = wNotification.bundle?.getString("android.title") ?: "Empty"
        if (title.contains("You") || title == "Empty") return
        if (!title.contains(
                Regex(
                    "caaron|ching|nathalie|justin|yan|xiang|vikram",
                    RegexOption.IGNORE_CASE
                )
            )
        ) {
            return
        }
    }

    private fun checkMsg() {
        msg = wNotification.bundle?.getString("android.text") ?: "Empty"
        Log.d(DEBUG, "Title: $title\nBody: $msg")
        replyText = "Hello, I am ReplyBot. My owner cannot come to the phone right now."

        if (msg.contains(Regex("hi|hello", RegexOption.IGNORE_CASE))) {
            replyText = "Hello $title"
        }
    }

    private fun createIntentBundle() {
        intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        bundle = Bundle()
        bundle.putCharSequence(wNotification.remoteInputs[0].resultKey, replyText)

        RemoteInput.addResultsToIntent(wNotification.remoteInputs.toTypedArray(), intent, bundle)
    }

    private fun wNotificationPendingIntent(sbn: StatusBarNotification?) {
        try {
            wNotification.pendingIntent?.let {
                it.send(this, 0, intent)
                if (sbn?.id !== null) {
                    NotificationManagerCompat.from(this).cancel(sbn.id)
                } else {
                    cancelNotification(sbn?.key)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        Log.d(DEBUG, "Destroyed")
        super.onDestroy()
    }
}