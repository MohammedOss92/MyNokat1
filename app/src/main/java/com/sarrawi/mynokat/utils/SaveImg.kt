package com.sarrawi.img.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.sarrawi.mynokat.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SaveImg(con: Context) {
    interface OnSaveImageClickListener {
        fun onSaveImageClick(position: Int)
    }

    companion object{
    fun saveBitmapToExternalStorage(con: Context,bitmap: Bitmap) {
        val fileName = "image_${System.currentTimeMillis()}.jpg"

        try {
            // احصل على مسار التخزين الخارجي
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

            // تأكد من أن المجلد موجود، إذا لم يكن، قم بإنشاء المجلد
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            val imageFile = File(imagesDir, fileName)
            val outputStream = FileOutputStream(imageFile)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // إعلام المستخدم بأن الصورة تم حفظها
            showToast(con,"تم حفظ الصورة")

            // إظهار إشعار في لوحة الإشعارات باستخدام الصورة المحفوظة
            showNotification(con,"تم تنزيل الصورة", imageFile)

        } catch (e: IOException) {
            e.printStackTrace()
            // يمكنك إدراج رسالة خطأ هنا إذا لزم الأمر
        }
    }

    fun showToast(con: Context,message: String) {
        Toast.makeText(con, message, Toast.LENGTH_SHORT).show()
    }

    fun showNotification(con: Context,title: String, imageFile: File) {
        val channelId = "channel_id"
        val notificationId = 1

        // إنشاء قناة الإشعار إذا لم تكن موجودة (لإصدارات Android 8.0 فأعلى)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = con.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // إعداد محتوى الإشعار باستخدام الصورة المحفوظة
        val builder = NotificationCompat.Builder(con, channelId)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(title)
            .setContentText("تم تحميل الصورة بنجاح")
            .setLargeIcon(BitmapFactory.decodeFile(imageFile.absolutePath))
            .setAutoCancel(true)

        // إرسال الإشعار
        val notificationManager = con.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
    }

    //    private fun saveBitmapToExternalStorage(bitmap: Bitmap) {
//        val fileName = "image_${System.currentTimeMillis()}.jpg"
//
//        try {
//            // احصل على مسار التخزين الخارجي
//            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//
//            // تأكد من أن المجلد موجود، إذا لم يكن، قم بإنشاء المجلد
//            if (!imagesDir.exists()) {
//                imagesDir.mkdirs()
//            }
//
//            val imageFile = File(imagesDir, fileName)
//            val outputStream = FileOutputStream(imageFile)
//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//            outputStream.flush()
//            outputStream.close()
//
//            // إعلام المستخدم بأن الصورة تم حفظها
//            Toast.makeText(con, "تم حفظ الصورة", Toast.LENGTH_SHORT).show()
//        } catch (e: IOException) {
//            e.printStackTrace()
//            // يمكنك إدراج رسالة خطأ هنا إذا لزم الأمر
//        }
//    }
}