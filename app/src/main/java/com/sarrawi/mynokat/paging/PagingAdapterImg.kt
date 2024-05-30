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
import com.sarrawi.mynokat.databinding.ImgRowBinding
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.ui.frag.img.ImgFragmentDirections

//
//class PagingAdapterImg(val con: Context, val frag: Fragment) : PagingDataAdapter<ImgsNokatModel, PagingAdapterImg.ViewHolder>(COMPARATOR) {
//
//    private var isInternetConnected: Boolean = true
//    private var isImageVisible = true
//    private var currentFlippedPosition: Int? = null
//
//    inner class ViewHolder(private val binding: ImgRowBinding) : RecyclerView.ViewHolder(binding.root) {
//
//        init {
//            setupListeners()
//        }
//
//        fun bind(imgModel: ImgsNokatModel?, isInternetConnected: Boolean) {
//            if (isInternetConnected) {
//                val requestOptions = RequestOptions()
//                    .placeholder(R.drawable.ic_baseline_autorenew_24)
//                    .error(R.drawable.error_a)
//                    .format(DecodeFormat.PREFER_RGB_565)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .skipMemoryCache(true)
//
//                Glide.with(con)
//                    .load(imgModel?.image_url)
//                    .apply(requestOptions)
//                    .circleCrop()
//                    .centerCrop()
//                    .into(binding.imageView)
//            } else {
//                Glide.with(con)
//                    .load(R.drawable.nonet)
//                    .into(binding.imageView)
//                binding.imageView.visibility = View.GONE
//                binding.lyNoInternet.visibility = View.VISIBLE
//            }
//
//            binding.imageView.setOnClickListener {
//                val imgModel = getItem(bindingAdapterPosition)
//                imgModel?.let {
//                    val directions = ImgFragmentDirections.actionImgFragmentToImgFullFragment(it)
//                    frag.findNavController().navigate(directions)
//                }
//            }
//        }
//        private fun setupListeners() {
//            binding.root.setOnLongClickListener {
//                handleItemClick(bindingAdapterPosition)
//                true
//            }
//
//            binding.imageView.setOnLongClickListener {
//                handleItemClick(bindingAdapterPosition)
//                true
//            }
//        }
//
//        private fun handleItemClick(position: Int) {
//            val previousPosition = currentFlippedPosition
//
//            if (previousPosition != null && previousPosition != position) {
//                resetItemState(previousPosition)
//            }
//
//            currentFlippedPosition = position
//            val randomNumber = (1..2).random()
//            val imageUrl = getItem(position)?.image_url
//            flipTheCoin(imageUrl ?: "button", if (randomNumber == 1) "gfgf" else "gdfgdfg")
//        }
//
//        private fun resetItemState(position: Int) {
//            val holder = binding.imageView.findViewHolderForAdapterPosition(position) as? ViewHolder
//            holder?.resetView()
//        }
//
//        fun flipTheCoin(imageId: String, coinSide: String) {
//            binding.root.animate().apply {
//                duration = 1000
//                rotationYBy(360f)
//                binding.imageView.isClickable = false
//            }.withEndAction {
//                if (binding.imageView.visibility == View.VISIBLE) {
//                    binding.imageView.visibility = View.GONE
//                    binding.btncopy.visibility = View.VISIBLE
//                    binding.btncshare.visibility = View.VISIBLE
//                } else {
//                    binding.imageView.visibility = View.VISIBLE
//                    binding.btncopy.visibility = View.GONE
//                    binding.btncshare.visibility = View.GONE
//                }
//                Toast.makeText(con, coinSide, Toast.LENGTH_SHORT).show()
//                binding.imageView.isClickable = true
//            }.start()
//        }
//
//        fun resetView() {
//            binding.root.clearAnimation()
//            binding.imageView.visibility = View.VISIBLE
//            binding.btncopy.visibility = View.GONE
//            binding.btncshare.visibility = View.GONE
//        }
//
//
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(getItem(position), isInternetConnected)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ImgRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//
//    companion object {
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
//    fun updateInternetStatus(isConnected: Boolean) {
//        isInternetConnected = isConnected
//        notifyDataSetChanged()
//    }
//}
//
class PagingAdapterImg(val con: Context, val frag: Fragment) : PagingDataAdapter<ImgsNokatModel, PagingAdapterImg.ViewHolder>(COMPARATOR) {

    private var isInternetConnected: Boolean = true
    private var isImageVisible = true
    private var currentFlippedPosition: Int? = null

    inner class ViewHolder(private val binding: ImgRowBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            setupListeners()
        }

        fun bind(imgModel: ImgsNokatModel?, isInternetConnected: Boolean) {
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
                    .circleCrop()
                    .centerCrop()
                    .into(binding.imageView)
            } else {
                Glide.with(con)
                    .load(R.drawable.nonet)
                    .into(binding.imageView)
                binding.imageView.visibility = View.GONE
                binding.lyNoInternet.visibility = View.VISIBLE
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
        }

        private fun handleItemClick(position: Int) {
            val previousPosition = currentFlippedPosition

            if (previousPosition != null && previousPosition != position) {
                resetItemState(previousPosition)
            }

            currentFlippedPosition = position
            val randomNumber = (1..2).random()
            val imageUrl = getItem(position)?.image_url
            flipTheCoin(imageUrl ?: "button", if (randomNumber == 1) "gfgf" else "gdfgdfg")
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
                    binding.btncopy.visibility = View.VISIBLE
                    binding.btncshare.visibility = View.VISIBLE
                } else {
                    binding.imageView.visibility = View.VISIBLE
                    binding.btncopy.visibility = View.GONE
                    binding.btncshare.visibility = View.GONE
                }
                Toast.makeText(con, coinSide, Toast.LENGTH_SHORT).show()
                binding.imageView.isClickable = true
            }.start()
        }

        fun resetView() {
            binding.root.clearAnimation()
            binding.imageView.visibility = View.VISIBLE
            binding.btncopy.visibility = View.GONE
            binding.btncshare.visibility = View.GONE
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
