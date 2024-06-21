package com.sarrawi.mynokat.paging

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.viewModelFactory
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
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.ui.frag.img.ImgFragmentDirections
import com.sarrawi.mynokat.ui.frag.img.NewImgFragmentDirections

class PagingAdpterNewImg(val con: Context, val frag: Fragment) : PagingDataAdapter<ImgsNokatModel, PagingAdpterNewImg.ViewHolder>(COMPARATOR) {

    private var isInternetConnected: Boolean = true
    private var isImageVisible = true
    private var currentFlippedPosition: Int? = null
    var onItemClick: ((ImgsNokatModel, Int) -> Unit)? = null

    inner class ViewHolder(private val binding: ImgRowBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            setupListeners()
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

            binding.apply {
                if(imgModel!!.is_fav){
                    favBtn.setImageResource(R.drawable.baseline_favorite_true)
                }

                else{
                    favBtn.setImageResource(R.drawable.baseline_favorite_border_false)
                }

                binding.btnnew.setImageResource(R.drawable.new_msg)

            }

            binding.imageView.setOnClickListener {
                val imgModel = getItem(bindingAdapterPosition)
                imgModel?.let {
                    val directions = NewImgFragmentDirections.actionNewImgFragmentToImgFullFragment(it)
                    frag.findNavController().navigate(directions)
                }
            }
            if (isInternetConnected) {
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_baseline_autorenew_24)
                    .error(R.drawable.error_a)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)

                Glide.with(con)
                    .asBitmap()
                    .load(imgModel?.image_url)
                    .apply(requestOptions)
                    .into(binding.imageView)
            } else {
                Glide.with(con)
                    .load(R.drawable.nonet)
                    .into(binding.imageView)
                binding.imageView.visibility = View.GONE
                binding.lyNoInternet.visibility = View.VISIBLE
            }

            if (imgModel?.new_img == 0) {
                binding.btnnew.setVisibility(View.INVISIBLE)
            } else {
                binding.btnnew.setVisibility(View.VISIBLE)
            }
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
            }
            binding.btnwhats.setOnClickListener {
                Utils.shareImageWhatsApp(con,binding.imageView,"",binding.root)
            }
            binding.btncshare.setOnClickListener {
                Utils.ImgShare(con,binding.imageView,binding.root)
            }
            binding.btnSave.setOnClickListener {
                SaveImg.saveBitmapToExternalStorage(con,(binding.imageView.drawable as BitmapDrawable).bitmap)
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
                    binding.btnnew.visibility = View.VISIBLE
                } else {
                    binding.imageView.visibility = View.VISIBLE
                    binding.btncmessenger.visibility = View.GONE
                    binding.btnwhats.visibility = View.GONE
                    binding.btnSave.visibility = View.GONE
                    binding.btncshare.visibility = View.GONE
                    binding.btnnew.visibility = View.GONE
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
        holder.bind(getItem(position), isInternetConnected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ImgRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
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

    fun updateInternetStatus(isConnected: Boolean) {
        isInternetConnected = isConnected
        notifyDataSetChanged()
    }
}
