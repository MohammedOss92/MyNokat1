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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.ImageFullBinding
import com.sarrawi.mynokat.databinding.ImgRowBinding
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.ui.frag.tabs.ImgFragmentDirections

class PagingAdapterFullImg(val con: Context, val frag: Fragment) : PagingDataAdapter<ImgsNokatModel, PagingAdapterFullImg.ViewHolder>(COMPARATOR) {
    var onItemClick: ((Unit) -> Unit)? = null
    private var isInternetConnected: Boolean = true


    inner class ViewHolder(private val binding: ImageFullBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

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

