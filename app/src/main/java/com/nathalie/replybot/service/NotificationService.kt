package com.nathalie.replybot.service

import android.app.RemoteInput
import android.content.ComponentCallbacks
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.rpc.context.AttributeContext.Auth
import com.nathalie.replybot.data.model.Rule
import com.nathalie.replybot.data.model.WearableNotification
import com.nathalie.replybot.data.repository.FireStoreRuleRepository
import com.nathalie.replybot.utils.Constants
import com.nathalie.replybot.utils.Constants.DEBUG
import com.nathalie.replybot.utils.NotificationUtils
import kotlinx.coroutines.*

class NotificationService : NotificationListenerService() {
    private lateinit var intent: Intent
    lateinit var bundle: Bundle
    private lateinit var title: String
    private lateinit var wNotification: WearableNotification
    private lateinit var msg: String
    private var replyText: String = ""
    private lateinit var repo: FireStoreRuleRepository

    override fun onCreate() {
        super.onCreate()
        repo = FireStoreRuleRepository(Firebase.firestore.collection("rules"))
        start()

    }

    //called when notification received
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        Log.d(DEBUG, "Found a notification")

        wNotification = NotificationUtils.getWearableNotification(sbn) ?: return
        title = wNotification.bundle?.getString("android.title") ?: "Empty"
        Log.d(DEBUG, "Title: $title")

        //terminate when isRunning and checkTitle are false
        if (!isRunning) return
        if (!checkTitle()) return

        checkMsg {
            createIntentBundle()
            wNotificationPendingIntent(sbn)
        }
    }

    //check if title contains provided regex
    private fun checkTitle(): Boolean {
        if (title.contains(
                Regex(
                    "caaron|ching|justin|yan|xiang|vikram|khayrul|601606|joel|quan|Joel",
                    RegexOption.IGNORE_CASE
                )
            )
        ) {
            return true
        }
        return false
    }

    //check if notification body matches rule's keyword then reply according to rule's msg
    private fun checkMsg(callback: () -> Unit) {
        msg = wNotification.bundle?.getString("android.text") ?: "Empty"
        val rules = getRules()

        for (i in rules) {
            if (msg.contains(Regex(i.keyword, RegexOption.IGNORE_CASE))) {
                replyText = i.msg
                val notifName = wNotification.name

                if (replyIfAppIsSelected(i.whatsapp, "com.whatsapp", notifName, callback)) break
                if (replyIfAppIsSelected(i.facebook, "com.facebook.orca", notifName, callback)) break
            }
        }
    }

    //check if user's selected app option matches wNotification.name, if true then fire callback fn
    private fun replyIfAppIsSelected(
        isSelected: Boolean,
        userSelectedApp: String,
        notifName: String,
        callback: () -> Unit
    ): Boolean {
        if (isSelected && hasAppName(notifName, userSelectedApp)) {
            callback()
            return true
        }

        return false
    }

    //utilize regex to check if wNotification.name(string) contains user's selected app option
    private fun hasAppName(notifName: String, appName: String): Boolean {
        return notifName.contains(Regex(appName, RegexOption.IGNORE_CASE))
    }

    //fetch all rules that matches current user's id from FireStore
    private fun getRules(): MutableList<Rule> {
        val rules: MutableList<Rule> = mutableListOf()

        val job = CoroutineScope(Dispatchers.Default).launch {
            val res = repo.getAllRules()
            res.let {
                it.forEach { rule ->
                    rules.add(rule)
                }
            }
        }
        runBlocking {
            job.join()
        }
        return rules.filter { rule -> !rule.disabled }.toMutableList()
    }

    //create intent bundle
    private fun createIntentBundle() {
        intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        bundle = Bundle()
        bundle.putCharSequence(wNotification.remoteInputs[0].resultKey, replyText)

        RemoteInput.addResultsToIntent(wNotification.remoteInputs.toTypedArray(), intent, bundle)
    }

    //send reply
    private fun wNotificationPendingIntent(sbn: StatusBarNotification?) {
        try {
            wNotification.pendingIntent?.let {
                CoroutineScope(Dispatchers.Default).launch {
                    isRunning = false
                    cancelNotification(sbn?.key)

                    it.send(this@NotificationService, 0, intent)
                    delay(500)
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