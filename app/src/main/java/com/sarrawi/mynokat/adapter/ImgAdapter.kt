package com.sarrawi.mynokat.adapter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

import com.google.android.material.snackbar.Snackbar

import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.ImgRowBinding
import com.sarrawi.mynokat.model.ImgsNokatModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImgAdapter(val con: Context): RecyclerView.Adapter<ImgAdapter.ViewHolder>() {
    var onItemClick: ((Int,ImgsNokatModel, Int) -> Unit)? = null
    var onItemClick2: ((Int,ImgsNokatModel, Int) -> Unit)? = null

    private var isInternetConnected: Boolean = true

    var onbtnClick: ((item:ImgsNokatModel,position:Int) -> Unit)? = null
    val displayMetrics = con.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    // قم بتحديد القيم المطلوبة للصورة
    val targetWidth = screenWidth / 2 // على سبيل المثال، يمكنك تحديد العرض إلى نصف عرض الشاشة
    val targetHeight = screenHeight / 2 // على سبيل المثال، يمكنك تحديد الارتفاع إلى نصف ارتفاع الشاشة



    inner class ViewHolder(val binding: ImgRowBinding):RecyclerView.ViewHolder(binding.root) {

        init {
            if(isInternetConnected) {
                binding.root.setOnClickListener {
                    //اذا كانت null سيتم استخدام 0؟
//                    onItemClick?.invoke(img_list[layoutPosition].id ?: 0,img_list[layoutPosition].image_url, layoutPosition ?: 0)
                    onItemClick?.invoke(img_list[layoutPosition].id ?: 0, img_list[layoutPosition], layoutPosition)

                }

                binding.root.setOnLongClickListener {
                    val options = arrayOf("حفظ", "مشاركة")

                    val builder = AlertDialog.Builder(itemView.context)
                    builder.setTitle("اختر الإجراء")
                    builder.setItems(options) { _, which ->
                        when (which) {
                            0 -> {
                                // إجراء "حفظ"
                                // onItemClick2?.invoke(adapterPosition, fav_img_list[adapterPosition], layoutPosition)
                                saveBitmapToExternalStorage((binding.imageView.drawable as BitmapDrawable).bitmap)

                            }
                            1 -> {
                                // إجراء "مشاركة"
                                // onItemClick2?.invoke(adapterPosition, fav_img_list[adapterPosition], layoutPosition)
                                val drawable: BitmapDrawable = binding.imageView.getDrawable() as BitmapDrawable
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
                        }
                    }
                    builder.setNegativeButton("إلغاء") { dialog, _ ->
                        dialog.dismiss() // أغلق الحوار دون تنفيذ أي إجراء
                    }
                    builder.show()

                    true // يشير إلى أن الحدث تم تناوله ولا يجب استمرار انتشاره
                }


//                binding.imgFave.setOnClickListener {
//                    onbtnClick?.invoke(img_list[position], position)
//                }

            }
            else{
                    binding.root.setOnClickListener{
//                        Toast.makeText(con,"ghghg",Toast.LENGTH_SHORT).show()
                        val snackbar = Snackbar.make(it,"لا يوجد اتصال بالإنترنت", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }

//                    binding.imgFave.setOnClickListener {
//                        val snackbar = Snackbar.make(it,"لا يوجد اتصال بالإنترنت", Snackbar.LENGTH_SHORT)
//                        snackbar.show()
//                 }

                }
            }



        fun bind(position: Int, isInternetConnected: Boolean) {
            if (isInternetConnected) {

                val current_imgModel = img_list[position]
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_baseline_autorenew_24)
                    .error(R.drawable.error_a)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)

                Glide.with(con)
                    .asBitmap()
                    .load(current_imgModel.image_url)
                    .apply(requestOptions)
                    .circleCrop()
                    .into(binding.imageView)




                binding.apply {
//                    if(current_imgModel.is_fav){
//                        imgFave.setImageResource(R.drawable.baseline_favorite_true)
//                    }else{
//                        imgFave.setImageResource(R.drawable.baseline_favorite_border_false)
//                    }

                }
            } else {
                // عند عدم وجود اتصال بالإنترنت، قم بعرض الـ lyNoInternet بدلاً من الصورة
                Glide.with(con)
                    .load(R.drawable.new_msg) // تحميل صورة nonet.jpg
                    .into(binding.imageView)
                binding.imageView.visibility = View.GONE
                binding.lyNoInternet.visibility = View.VISIBLE
            }



        }



    }

    private val diffCallback = object : DiffUtil.ItemCallback<ImgsNokatModel>(){
        override fun areItemsTheSame(oldItem: ImgsNokatModel, newItem: ImgsNokatModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImgsNokatModel, newItem: ImgsNokatModel): Boolean {
            return newItem == oldItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)
    var img_list: List<ImgsNokatModel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(ImgRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(position,isInternetConnected)

    }

    override fun getItemCount(): Int {
        return img_list.size
    }
//a
    fun updateInternetStatus(isConnected: Boolean) {
        isInternetConnected = isConnected
        notifyDataSetChanged()
    }
    fun updateData(newData: List<ImgsNokatModel>) {
        img_list = newData
        notifyDataSetChanged()
    }

    fun saveBitmapToExternalStorage(bitmap: Bitmap) {
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
            showToast("تم حفظ الصورة")

            // إظهار إشعار في لوحة الإشعارات باستخدام الصورة المحفوظة
            showNotification("تم تنزيل الصورة", imageFile)

        } catch (e: IOException) {
            e.printStackTrace()
            // يمكنك إدراج رسالة خطأ هنا إذا لزم الأمر
        }
    }

    fun showToast(message: String) {
        Toast.makeText(con, message, Toast.LENGTH_SHORT).show()
    }

    fun showNotification(title: String, imageFile: File) {
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
            .setSmallIcon(R.drawable.new_msg)
            .setContentTitle(title)
            .setContentText("تم تحميل الصورة بنجاح")
            .setLargeIcon(BitmapFactory.decodeFile(imageFile.absolutePath))
            .setAutoCancel(true)

        // إرسال الإشعار
        val notificationManager = con.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }


}