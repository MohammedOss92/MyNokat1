package com.sarrawi.img.utils

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.ClipboardManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class Utils {

    companion object {


        fun shareApp(con: Context){
            val sendIntent = Intent()
            sendIntent.action=Intent.ACTION_SEND
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT," مشاركة التطبيق\n\n https://play.google.com/store/apps/details?id=com.abdallah.sarrawi.mymsgs")
            con.startActivity(
                Intent.createChooser(
                    sendIntent,"choose one"
                )
            )
        }

        fun shareImageWhatsApp(con: Context,imageView: ImageView, text: String,rootView: View) {
            val drawable: BitmapDrawable = imageView.drawable as BitmapDrawable
            val bitmap: Bitmap = drawable.bitmap

            val bitmapPath: String = MediaStore.Images.Media.insertImage(
                con.contentResolver,
                bitmap,
                "title",
                null
            )

            val uri: Uri = Uri.parse(bitmapPath)

            val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                setPackage("com.whatsapp")
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, text)
            }

            try {
                con.startActivity(whatsappIntent)
            } catch (ex: ActivityNotFoundException) {
                Snackbar.make(rootView, "Whatsapp is not installed.", Snackbar.LENGTH_SHORT).show()
            }
        }

        fun shareImgMessenger(con: Context,imageView: ImageView,rootView: View){
            val drawable: BitmapDrawable = imageView.drawable as BitmapDrawable
            val bitmap: Bitmap = drawable.bitmap

            val bitmapPath: String = MediaStore.Images.Media.insertImage(
                con.contentResolver,
                bitmap,
                "title",
                null
            )

            val uri: Uri = Uri.parse(bitmapPath)

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/png"

            val whatsappIntent = Intent(Intent.ACTION_SEND)
            whatsappIntent.type = "text/plain"
            whatsappIntent.setPackage("com.facebook.orca")
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri)
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share")
            try {
                con.startActivity(whatsappIntent)
            } catch (ex: ActivityNotFoundException) {
                Snackbar.make(rootView, "لم يتم تثبيت تطبيق Facebook Messenger.", Snackbar.LENGTH_SHORT).show()

            }
        }

        fun ImgShare(con: Context,imageView: ImageView,rootView: View){
            val drawable: BitmapDrawable = imageView.drawable as BitmapDrawable
            val bitmap: Bitmap = drawable.bitmap

            val bitmapPath: String = MediaStore.Images.Media.insertImage(
                con.contentResolver,
                bitmap,
                "title",
                null
            )

            val uri: Uri = Uri.parse(bitmapPath)

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/png"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.putExtra(Intent.EXTRA_TEXT, "Playstore Link: https://play.google.com/store")

            con.startActivity(Intent.createChooser(intent, "Share"))
        }

        fun ShareText(con: Context, dialog_Heder_text: String, heder: String, Msg: String) {
            try {

                var sharingIntent = Intent("android.intent.action.SEND")
                sharingIntent.type = "text/plain"
//                sharingIntent.putExtra("android.intent.extra.TEXT", out9.getText().toString())
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, heder)
                sharingIntent.putExtra(Intent.EXTRA_TEXT, Msg)
                con.startActivity(
                    Intent.createChooser(
                        sharingIntent,dialog_Heder_text
                    )
                )



            } catch (e: Exception) {
                Log.d("error in share", e.toString())
            }
        }

        fun CopyTxt(con: Context,textView: TextView){
            val stringYouExtracted: String = textView.text.toString()

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                val clipboard =
                    con.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.text = stringYouExtracted
            } else {
                val clipboard =
                    con.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = ClipData
                    .newPlainText(stringYouExtracted, stringYouExtracted)
                clipboard.setPrimaryClip(clip)
            }
            Toast.makeText(con, "تم نسخ النص", Toast.LENGTH_SHORT).show()
        }

        fun shareTextMessenger(con: Context, text: String, rootView: View) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.facebook.orca")
            intent.putExtra(Intent.EXTRA_TEXT, text)
            try {
                con.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Snackbar.make(rootView, "لم يتم تثبيت تطبيق Facebook Messenger.", Snackbar.LENGTH_SHORT).show()
            }
        }

        fun shareTextWhatsApp(con: Context, text: String, rootView: View) {
            val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.whatsapp")
                putExtra(Intent.EXTRA_TEXT, text)
            }

            try {
                con.startActivity(whatsappIntent)
            } catch (ex: ActivityNotFoundException) {
                Snackbar.make(rootView, "لم يتم تثبيت تطبيق WhatsApp.", Snackbar.LENGTH_SHORT).show()
            }
        }


    }







}

