package com.sarrawi.mynokat.firebase

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.ui.MainActivity
import java.net.HttpURLConnection
import java.net.URL


class FirebaseMessagingservice : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)
        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body
            val imageUrl = remoteMessage.data["image"]
            val targetScreen = remoteMessage.data["targetScreen"]
            sendNotification(title, body, imageUrl, targetScreen)
        }
    }

    private fun sendNotification(
        title: String?,
        body: String?,
        imageUrl: String?,
        targetScreen: String?
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("targetScreen", targetScreen) // إضافة المفتاح targetScreen
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        if (imageUrl != null && !imageUrl.isEmpty()) {
            val imageBitmap = getBitmapFromURL(imageUrl)
            if (imageBitmap != null) {
                notificationBuilder.setStyle(
                    NotificationCompat.BigPictureStyle().bigPicture(imageBitmap)
                )
            }
        }
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getBitmapFromURL(strURL: String): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // إرسال الرمز الجديد إلى الخادم إذا لزم الأمر
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val NOTIFICATION_ID = 1 // إضافة تعريف NOTIFICATION_ID
    }
}



