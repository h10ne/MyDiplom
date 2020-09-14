package ru.rksi.mydiplom.Notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.karn.notify.Notify
import kotlinx.serialization.json.Json
import ru.rksi.mydiplom.APIClasses.UserInfo
import ru.rksi.mydiplom.MainActivity
import ru.rksi.mydiplom.R
import java.io.File
import java.io.FileInputStream

class FirebaseNotifications : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            if (remoteMessage.notification == null)
                return
            sendNotification(it)

            //showNotification(remoteMessage.notification!!)
        }
    }

    private fun showNotification(notification: RemoteMessage.Notification) {
        val userFile = File(applicationContext.filesDir, "User.dat")
        if(!userFile.exists())
            return
        Notify.with(applicationContext).content {
            title = notification.title
            text = notification.body
        }.header {
            icon = R.drawable.ic_new_notification
        }.show()

//        val notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val r = RingtoneManager.getRingtone(applicationContext, notify)
//        Notify.with(applicationContext).content {
//            title = notification.title
//            text = notification.body
//        }.alerting(""){
//            sound = notify
//        }
//            .show()
//        r.play()
//        NotificationManager.
    }

    private fun sendNotification(notification: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "PNT"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_app_icon)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Уведомления",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}