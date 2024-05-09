package com.sarrawi.mynokat.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.ImgRowBinding
import com.sarrawi.mynokat.databinding.NokatDesignBinding
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.model.NokatModel
class PagingAdapterImg(val con: Context) : PagingDataAdapter<ImgsNokatModel, PagingAdapterImg.ViewHolder>(COMPARATOR) {

    private var isInternetConnected: Boolean = true

    inner class ViewHolder(private val binding: ImgRowBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            setupListeners()
        }

        private fun setupListeners() {
            binding.imageView.setOnClickListener {
                val randomNumber = (1..2).random()
                if (randomNumber == 1) {
                    flipTheCoin(R.drawable.nonet, "gfgf")
                } else {
                    flipTheCoin(R.drawable.nonet, "gdfgdfg")
                }

            }
        }

        fun bind(imgModel: ImgsNokatModel?, isInternetConnected: Boolean) {
            if (isInternetConnected) {
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_baseline_autorenew_24)
                    .error(R.drawable.error_a)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)

                Glide.with(con)
                    .load(imgModel?.image_url)
                    .apply(requestOptions)
                    .circleCrop()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imageView)

                // Show appropriate UI elements based on the data
                // For example:
                /*binding.lyNoInternet.visibility = View.GONE
                binding.imgFave.setImageResource(if (imgModel?.is_fav == true) R.drawable.baseline_favorite_true else R.drawable.baseline_favorite_border_false)*/
            } else {
                Glide.with(con)
                    .load(R.drawable.nonet)
                    .into(binding.imageView)
                binding.imageView.visibility = View.GONE
                binding.lyNoInternet.visibility = View.VISIBLE
            }
        }

        fun flipTheCoin(imageId: Int, coinSide: String) {
            binding.imageView.animate().apply {
                duration = 1000
                rotationYBy(1800f)
                binding.imageView.isClickable = false
            }.withEndAction {
                binding.imageView.setImageResource(imageId)
                Toast.makeText(con, coinSide, Toast.LENGTH_SHORT).show()
                binding.imageView.isClickable = true
            }.start()
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
}

//
//class PagingAdapterImg(val con: Context):PagingDataAdapter<ImgsNokatModel,PagingAdapterImg.ViewHolder>(COMPARATOR) {
//
//
//    private var isInternetConnected: Boolean = true
//
//    inner class ViewHolder(private val binding:ImgRowBinding):RecyclerView.ViewHolder(binding.root) {
//
//        fun onCoinTab(){
//            binding.imageView.setOnClickListener{
//                val randomNumber=(1..2).random()
//                if (randomNumber==1){
//                    flipTheCoin(R.drawable.nonet,"gfgf")
//                }else{
//                    flipTheCoin(R.drawable.nonet,"gdfgdfg")
//                }
//            }
//        }
//
//        fun bind(position: Int, isInternetConnected: Boolean) {
//            val current_imgModel = getItem(position)
//            if (isInternetConnected) {
//                val requestOptions = RequestOptions()
//                    .placeholder(R.drawable.ic_baseline_autorenew_24)
//                    .error(R.drawable.error_a)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .skipMemoryCache(false)
//
//                Glide.with(con)
//                    .load(current_imgModel?.image_url)
//                    .apply(requestOptions)
//                    .circleCrop()
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(binding.imageView)
//
////                binding.lyNoInternet.visibility = ViewGroup.GONE
////
////                binding.apply {
////                    if (current_imgModel?.is_fav == true) {
////                        imgFave.setImageResource(R.drawable.baseline_favorite_true)
////                    } else {
////                        imgFave.setImageResource(R.drawable.baseline_favorite_border_false)
////                    }
////                }
//            } else {
//                Glide.with(con)
//                    .load(R.drawable.nonet)
//                    .into(binding.imageView)
//                binding.imageView.visibility = ViewGroup.GONE
//                binding.lyNoInternet.visibility = ViewGroup.VISIBLE
//            }
//
//
//
//    }
//        fun flipTheCoin(imageId:Int,coinSide:String) {
//            binding.imageView.animate().apply {
//                duration = 1000
//                rotationYBy(1800f)
//                binding.imageView.isClickable = false
//            }.withEndAction {
//                binding.imageView.setImageResource(imageId)
//                Toast.makeText(con,coinSide , Toast.LENGTH_SHORT).show()
//                binding.imageView.isClickable = true
//            }.start()
//        }
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(position, isInternetConnected)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ImgRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    companion object {
//
//        private val COMPARATOR = object : DiffUtil.ItemCallback<ImgsNokatModel>() {
//            override fun areItemsTheSame(oldItem: ImgsNokatModel, newItem: ImgsNokatModel): Boolean {
//                return oldItem.id == newItem.id
//            }
//
//            override fun areContentsTheSame(oldItem: ImgsNokatModel, newItem: ImgsNokatModel): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
//
//
//}