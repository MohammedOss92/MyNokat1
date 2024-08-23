package com.sarrawi.mynokat.paging

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sarrawi.img.utils.SaveImg
import com.sarrawi.img.utils.Utils
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.ImageFullBinding
import com.sarrawi.mynokat.databinding.ImgRowBinding
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.ui.frag.img.ImgFragment
import com.sarrawi.mynokat.ui.frag.img.ImgFullFragment

class PagingAdapterFullImg(val con: Context, val frag: Fragment) : PagingDataAdapter<ImgsNokatModel, PagingAdapterFullImg.ViewHolder>(COMPARATOR) {
    private var isInternetConnected: Boolean = true
    var onItemClick: (( ImgsNokatModel, Int) -> Unit)? = null

    inner class ViewHolder(private val binding: ImageFullBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

            setUpListener()

            binding.favBtn.setOnClickListener {
                it.animate().apply {
                    duration = 1000  // مدة الرسوم المتحركة بالمللي ثانية
                    rotationYBy(360f)  // يدور العنصر حول المحور Y بمقدار 360 درجة
                    setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            // الانتقال إلى ImgFragment بعد انتهاء الرسوم المتحركة
                            val position = bindingAdapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                getItem(position)?.let { item ->
                                    onItemClick?.invoke(item, position)
                                    // تحديث حالة الزر بعد النقر
                                    if (item.is_fav) {
                                        binding.favBtn.setImageResource(R.drawable.baseline_favorite_true)
                                    } else {
                                        binding.favBtn.setImageResource(R.drawable.baseline_favorite_border_false)
                                    }
                                }
                            }

                        }
                    })
                }.start()


            }
        }

        fun bind(imgModel: ImgsNokatModel?, isInternetConnected: Boolean) {

            binding.btnnew.setImageResource(R.drawable.new_msg)

            binding.apply {
                if (imgModel!!.is_fav) {
                    favBtn.setImageResource(R.drawable.baseline_favorite_true)
                } else {
                    favBtn.setImageResource(R.drawable.baseline_favorite_border_false)
                }
            }






            if (isInternetConnected) {
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.s)
                    .error(R.drawable.error_a)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)

                Glide.with(con)
                    .load(imgModel?.image_url)
                    .apply(requestOptions)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imageViewfull)
            } else {
                Glide.with(con)
                    .load(R.drawable.nonet)
                    .into(binding.imageViewfull)
                binding.imageViewfull.visibility = View.GONE
                binding.lyNoInternetfull.visibility = View.VISIBLE
            }

            if (imgModel?.new_img == 0) {
                binding.btnnew.setVisibility(View.INVISIBLE)
            } else {
                binding.btnnew.setVisibility(View.VISIBLE)
            }
        }

        fun setUpListener(){



            binding.btncmessenger.setOnClickListener {
                Utils.shareImgMessenger(con,binding.imageViewfull,binding.root)
                (frag as? ImgFullFragment)?.showInterstitial()
            }
            binding.btnwhats.setOnClickListener {
                Utils.shareImageWhatsApp(con,binding.imageViewfull,"",binding.root)
                (frag as? ImgFullFragment)?.showInterstitial()
            }
            binding.btncshare.setOnClickListener {
                Utils.ImgShare(con,binding.imageViewfull,binding.root)
                (frag as? ImgFullFragment)?.showInterstitial()
            }
            binding.btnSave.setOnClickListener {
                SaveImg.saveBitmapToExternalStorage(con,(binding.imageViewfull.drawable as BitmapDrawable).bitmap)
                (frag as? ImgFullFragment)?.showInterstitial()
            }
        }


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), isInternetConnected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ImageFullBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun updateInternetStatus(isConnected: Boolean) {
        isInternetConnected = isConnected
        notifyDataSetChanged()
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<ImgsNokatModel>() {
            override fun areItemsTheSame(oldItem: ImgsNokatModel, newItem: ImgsNokatModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ImgsNokatModel, newItem: ImgsNokatModel): Boolean {
                return oldItem == newItem
            }
        }
    }


}

