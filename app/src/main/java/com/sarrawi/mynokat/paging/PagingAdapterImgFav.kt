package com.sarrawi.mynokat.paging

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.sarrawi.mynokat.databinding.ImgRowBinding
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.ui.frag.img.FavFragment
import com.sarrawi.mynokat.ui.frag.img.FavFragmentDirections
import com.sarrawi.mynokat.ui.frag.img.ImgFragmentDirections
import com.sarrawi.mynokat.ui.frag.img.ImgFullFragment


class PagingAdapterImgFav (val con: Context, val frag: Fragment) : PagingDataAdapter<FavImgModel, PagingAdapterImgFav.ViewHolder>(COMPARATOR) {

    var onbtnclick: ((Int,item:FavImgModel,Int) -> Unit)? = null
    private var currentFlippedPosition: Int? = null
    inner class ViewHolder(private val binding: ImgRowBinding):RecyclerView.ViewHolder(binding.root) {

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

            setupListeners()
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

            binding.imageView.setOnClickListener {
                val imgModel = getItem(bindingAdapterPosition)
                imgModel?.let {
                    val directions = FavFragmentDirections.actionFavFragment2ToFavFragmentFull(it)
                    frag.findNavController().navigate(directions)
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
                .into(binding.imageView)


        }
        private fun setupListeners() {
            binding.root.setOnLongClickListener {
                handleItemClick(bindingAdapterPosition)
                true
            }

            binding.imageView.setOnLongClickListener {
                handleItemClick(bindingAdapterPosition)
                true
            }

            binding.roundBtn.setOnClickListener {
                handleItemClick(bindingAdapterPosition)
                true
            }

            binding.btncmessenger.setOnClickListener {
                Utils.shareImgMessenger(con,binding.imageView,binding.root)
                (frag as? FavFragment)?.showInterstitial()
            }
            binding.btnwhats.setOnClickListener {
                Utils.shareImageWhatsApp(con,binding.imageView,"",binding.root)
                (frag as? FavFragment)?.showInterstitial()
            }
            binding.btncshare.setOnClickListener {
                Utils.ImgShare(con,binding.imageView,binding.root)
                (frag as? FavFragment)?.showInterstitial()
            }
            binding.btnSave.setOnClickListener {
                SaveImg.saveBitmapToExternalStorage(con,(binding.imageView.drawable as BitmapDrawable).bitmap)
                (frag as? FavFragment)?.showInterstitial()
            }

        }


        private fun handleItemClick(position: Int) {
            val previousPosition = currentFlippedPosition

            if (previousPosition != null && previousPosition != position) {
                resetItemState(previousPosition)
            }

            currentFlippedPosition = position
            val randomNumber = (1..2).random()
            val imageUrl = getItem(position)?.image_url
            flipTheCoin(imageUrl ?: "button", if (randomNumber == 1) "" else "")
        }

        private fun resetItemState(position: Int) {
//            val holder = binding.imageView.findViewHolderForAdapterPosition(position) as? ViewHolder
//            holder?.resetView()

            val viewHolder = (frag.requireView().findViewById(R.id.rc_Img_Nokat) as RecyclerView).findViewHolderForAdapterPosition(position) as? ViewHolder
            viewHolder?.resetView()
        }

        fun flipTheCoin(imageId: String, coinSide: String) {
            binding.root.animate().apply {
                duration = 1000
                rotationYBy(360f)
                binding.imageView.isClickable = false
            }.withEndAction {
                if (binding.imageView.visibility == View.VISIBLE) {
                    binding.imageView.visibility = View.GONE
                    binding.btnSave.visibility = View.VISIBLE
                    binding.btncshare.visibility = View.VISIBLE
                    binding.btncmessenger.visibility = View.VISIBLE
                    binding.btnwhats.visibility = View.VISIBLE
                } else {
                    binding.imageView.visibility = View.VISIBLE
                    binding.btncmessenger.visibility = View.GONE
                    binding.btnwhats.visibility = View.GONE
                    binding.btnSave.visibility = View.GONE
                    binding.btncshare.visibility = View.GONE
                }
//                Toast.makeText(con, coinSide, Toast.LENGTH_SHORT).show()
                binding.imageView.isClickable = true
            }.start()
        }

        fun resetView() {
            binding.root.clearAnimation()
            binding.imageView.visibility = View.VISIBLE
            binding.btncshare.visibility = View.GONE
            binding.btnSave.visibility = View.GONE
            binding.btnwhats.visibility = View.GONE
            binding.btncmessenger.visibility = View.GONE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ImgRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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