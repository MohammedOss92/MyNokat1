package com.sarrawi.mynokat.paging

import android.content.Context
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
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.ImageFullBinding
import com.sarrawi.mynokat.databinding.ImgRowBinding
import com.sarrawi.mynokat.model.ImgsNokatModel

class PagingAdapterFullImg(val con: Context, val frag: Fragment) : PagingDataAdapter<ImgsNokatModel, PagingAdapterFullImg.ViewHolder>(COMPARATOR) {
    private var isInternetConnected: Boolean = true
    var onItemClick: (( ImgsNokatModel, Int) -> Unit)? = null

    inner class ViewHolder(private val binding: ImageFullBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.favBtn.setOnClickListener {
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
        }

        fun bind(imgModel: ImgsNokatModel?, isInternetConnected: Boolean) {

            binding.apply {
                if(imgModel!!.is_fav){
                    favBtn.setImageResource(R.drawable.baseline_favorite_true)
                }

                else{
                    favBtn.setImageResource(R.drawable.baseline_favorite_border_false)
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

