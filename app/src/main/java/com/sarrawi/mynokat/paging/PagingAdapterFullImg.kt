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

    private var isImageVisible = true
    private var rotationDirection = 1
    inner class ViewHolder(private val binding: ImageFullBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            setupListeners()
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

        private fun setupListeners() {
            binding.root.setOnLongClickListener {
                val randomNumber = (1..2).random()
                val imageUrl = getItem(bindingAdapterPosition)?.image_url
                if (randomNumber == 1 && imageUrl != null) {
                    flipTheCoin(imageUrl, "gfgf")
                } else {
                    flipTheCoin("button", "gdfgdfg")
                }
                true
            }
            binding.imageViewfull.setOnLongClickListener {
                val randomNumber = (1..2).random()
                val imageUrl = getItem(bindingAdapterPosition)?.image_url
                if (randomNumber == 1 && imageUrl != null) {
                    flipTheCoin(imageUrl, "gfgf")
                } else {
                    flipTheCoin("button", "gdfgdfg")
                }
                true
            }
        }

        fun flipTheCoin(imageId: String, coinSide: String) {
            binding.root.animate().apply {
                duration = 1000
                rotationYBy(if (isImageVisible) 360f else -360f)
                binding.imageViewfull.isClickable = false
            }.withEndAction {
                if (isImageVisible) {
                    binding.imageViewfull.visibility = View.GONE
                    binding.btncopyfull.visibility = View.VISIBLE
                    binding.btncsharefull.visibility = View.VISIBLE
                    isImageVisible = false
                } else {
                    binding.imageViewfull.visibility = View.VISIBLE
                    binding.btncopyfull.visibility = View.GONE
                    binding.btncsharefull.visibility = View.GONE
                    isImageVisible = true
                }
                Toast.makeText(con, coinSide, Toast.LENGTH_SHORT).show()
                binding.imageViewfull.isClickable = true
            }.start()
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

