package com.reezkyillma.projectandroid.Notifications


import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build

class OreoNotification(base: Context) : ContextWrapper(base) {

    private var notificationManager: NotificationManager? = null

    val manager: NotificationManager?
        get() {
            if (notificationManager == null) {
                notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            return notificationManager
        }

    init {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {

        val channel = NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(false)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        manager!!.createNotificationChannel(channel)
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun getOreoNotification(title: String, body: String,
                            pendingIntent: PendingIntent, soundUri: Uri, icon: String): Notification.Builder {
        return Notification.Builder(applicationContext,"com.reezkyillma.projectandroid" )
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(soundUri)
                .setAutoCancel(true)
    }

    companion object {

        private val CHANNEL_ID = " com.reezkyillma.projectandroid"
        private val CHANNEL_NAME = "foodgram"
    }
}
