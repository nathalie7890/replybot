package com.nathalie.replybot.service

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.nathalie.replybot.data.model.Rule
import com.nathalie.replybot.data.model.WearableNotification
import com.nathalie.replybot.data.repository.FireStoreRuleRepository
import com.nathalie.replybot.utils.Constants
import com.nathalie.replybot.utils.Constants.DEBUG
import com.nathalie.replybot.utils.NotificationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationService : NotificationListenerService() {
    private lateinit var intent: Intent
    lateinit var bundle: Bundle
    private lateinit var title: String
    private lateinit var wNotification: WearableNotification
    private lateinit var msg: String
    private lateinit var replyText: String
    private lateinit var repo: FireStoreRuleRepository

    override fun onCreate() {
        super.onCreate()
        start()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        Log.d(DEBUG, "Found a notification")

        wNotification = NotificationUtils.getWearableNotification(sbn) ?: return
        title = wNotification.bundle?.getString("android.title") ?: "Empty"

        Log.d(DEBUG, wNotification.name)
        Log.d(DEBUG, "hello $title $isRunning")

        if (!isRunning) return
        if (!checkTitle()) return

        checkMsg()
        createIntentBundle()
        wNotificationPendingIntent(sbn)
    }

    private fun checkTitle(): Boolean {
//        if (title.contains("You") || title == "Empty") return false
        if (title.contains(
                Regex(
                    "caaron|ching|justin|yan|xiang|vikram|khayrul|601606",
                    RegexOption.IGNORE_CASE
                )
            )
        ) {
            return true
        }
        return false
    }

    private fun checkMsg() {
        msg = wNotification.bundle?.getString("android.text") ?: "Empty"
        val rules = getRules()

//        val rules = rules.filter((!disabled))
//        val appName = wNotification.name
//
//        if(msg === rule.keyword) {
//            if(rule.whatsapp && "com.whatsapp" == appName) {
//                reply
//            }
//        }

        Log.d(DEBUG, "Title: $title\nBody: $msg")
        replyText = "This is a bot"

        if (msg.contains(Regex("hi|hello", RegexOption.IGNORE_CASE))) {
            replyText = "Hello $title"
        }
    }

    fun getRules(): List<Rule> {
        var rules: List<Rule> = listOf()
        CoroutineScope(Dispatchers.Default).launch {
            rules = repo.getAllRules()
        }

        return rules
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
                CoroutineScope(Dispatchers.Default).launch {
                    isRunning = false
                    cancelNotification(sbn?.key)

                    it.send(this@NotificationService, 0, intent)
                    delay(1000)
                    isRunning = true
                }
            }
        } catch (e: Exception) {
            isRunning = true
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        Log.d(DEBUG, "Destroyed")
        super.onDestroy()
    }

    companion object {
        private var isRunning: Boolean = false
        fun start() {
            isRunning = true
        }

        fun stop() {
            isRunning = false
        }
    }
}