package com.sarrawi.mynokat.paging

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.ui.frag.img.FavFragment

class PagingAdapterImgFavFull (val con: Context, val frag: Fragment) : PagingDataAdapter<FavImgModel, PagingAdapterImgFavFull.ViewHolder>(COMPARATOR) {

    var onbtnclick: ((Int, item: FavImgModel, Int) -> Unit)? = null

    inner class ViewHolder(private val binding: ImageFullBinding): RecyclerView.ViewHolder(binding.root) {

        init {
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
                                    onbtnclick?.invoke(item.id ?: 0, item, position)

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

        fun bind(favImgModel: FavImgModel?){

            binding.apply {
                if(favImgModel!!.is_fav){
                    favBtn.setImageResource(R.drawable.baseline_favorite_true)
                }

                else{
                    favBtn.setImageResource(R.drawable.baseline_favorite_border_false)
                }



            }

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_autorenew_24)
                .error(R.drawable.error_a)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)

            Glide.with(con)
                .asBitmap()
                .load(favImgModel?.image_url)
                .apply(requestOptions)
                .into(binding.imageViewfull)
        }
        private fun setupListeners() {

            binding.btncmessenger.setOnClickListener {
                Utils.shareImgMessenger(con,binding.imageViewfull,binding.root)
                (frag as? FavFragment)?.showInterstitial()
            }
            binding.btnwhats.setOnClickListener {
                Utils.shareImageWhatsApp(con,binding.imageViewfull,"",binding.root)
                (frag as? FavFragment)?.showInterstitial()
            }
            binding.btncshare.setOnClickListener {
                Utils.ImgShare(con,binding.imageViewfull,binding.root)
                (frag as? FavFragment)?.showInterstitial()
            }
            binding.btnSave.setOnClickListener {
                SaveImg.saveBitmapToExternalStorage(con,(binding.imageViewfull.drawable as BitmapDrawable).bitmap)
                (frag as? FavFragment)?.showInterstitial()
            }

        }


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ImageFullBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<FavImgModel>() {
            override fun areItemsTheSame(oldItem: FavImgModel, newItem: FavImgModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FavImgModel, newItem: FavImgModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}