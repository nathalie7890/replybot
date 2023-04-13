package com.nathalie.replybot.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.nathalie.replybot.MainActivity
import com.nathalie.replybot.R
import com.nathalie.replybot.utils.Constants.NOTIFICATION_ID

class MyService : Service() {
    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "Reply Bot service started", Toast.LENGTH_LONG).show()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun startForegroundService() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_ID)
            .setContentTitle("ReplyBot")
            .setContentText("ReplyBot Notification Listener is active")
            .setSmallIcon(R.drawable.ic_android)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .build()

        startForeground(1, notification)
        startNotificationListenerService()
    }

    private fun notificationServiceIntent(): Intent {
        return Intent(this, NotificationService::class.java)
    }

    private fun startNotificationListenerService() {
        NotificationService.start()
//        notificationServiceIntent().also {
//            it.action = "android.service.notification.NotificationListenerService"
//            startService(it)
//        }
    }

    private fun stopNotificationListenerService() {
        NotificationService.stop()
//        notificationServiceIntent().also {
//            stopService(it)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopNotificationListenerService()
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show()
    }
}