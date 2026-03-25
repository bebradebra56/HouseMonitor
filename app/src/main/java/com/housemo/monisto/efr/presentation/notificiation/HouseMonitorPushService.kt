package com.housemo.monisto.efr.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.housemo.monisto.HouseMonitorActivity
import com.housemo.monisto.R
import com.housemo.monisto.efr.presentation.app.HouseMonitorApplication

private const val HOUSE_MONITOR_CHANNEL_ID = "house_monitor_notifications"
private const val HOUSE_MONITOR_CHANNEL_NAME = "HouseMonitor Notifications"
private const val HOUSE_MONITOR_NOT_TAG = "HouseMonitor"

class HouseMonitorPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                houseMonitorShowNotification(it.title ?: HOUSE_MONITOR_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                houseMonitorShowNotification(it.title ?: HOUSE_MONITOR_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            houseMonitorHandleDataPayload(remoteMessage.data)
        }
    }

    private fun houseMonitorShowNotification(title: String, message: String, data: String?) {
        val houseMonitorNotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                HOUSE_MONITOR_CHANNEL_ID,
                HOUSE_MONITOR_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            houseMonitorNotificationManager.createNotificationChannel(channel)
        }

        val houseMonitorIntent = Intent(this, HouseMonitorActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val houseMonitorPendingIntent = PendingIntent.getActivity(
            this,
            0,
            houseMonitorIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val houseMonitorNotification = NotificationCompat.Builder(this, HOUSE_MONITOR_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.house_monitor_noti_ic)
            .setAutoCancel(true)
            .setContentIntent(houseMonitorPendingIntent)
            .build()

        houseMonitorNotificationManager.notify(System.currentTimeMillis().toInt(), houseMonitorNotification)
    }

    private fun houseMonitorHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}