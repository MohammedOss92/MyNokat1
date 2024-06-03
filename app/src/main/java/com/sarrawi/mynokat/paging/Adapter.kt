package com.sarrawi.mynokat.paging

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import com.sarrawi.mynokat.model.ItemModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.ImageFullBinding
import com.sarrawi.mynokat.databinding.ImgRowBinding
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.model.bind.ITEM_TYPE_ANOTHER
import com.sarrawi.mynokat.model.bind.ITEM_TYPE_IMG
import com.sarrawi.mynokat.ui.frag.img.ImgFragmentDirections

class Adapter(val con: Context, val frag: Fragment) : PagingDataAdapter<ItemModel, RecyclerView.ViewHolder>(COMPARATOR) {
    private var isInternetConnected: Boolean = true
    private var isImageVisible = true
    private var currentFlippedPosition: Int? = null


    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<ItemModel>() {
            override fun areItemsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
                return when {
                    oldItem is ItemModel.ImgsItem && newItem is ItemModel.ImgsItem -> {
                        oldItem.imgsNokatModel.id == newItem.imgsNokatModel.id
                    }
                    oldItem is ItemModel.AnotherItem && newItem is ItemModel.AnotherItem -> {
                        oldItem.favImgModel.id == newItem.favImgModel.id
                    }
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ItemModel.ImgsItem -> ITEM_TYPE_IMG
            is ItemModel.AnotherItem -> ITEM_TYPE_ANOTHER
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_IMG -> {
                val binding = ImgRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ImgViewHolder(binding)
            }
            ITEM_TYPE_ANOTHER -> {
                val binding = ImageFullBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AnotherViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ImgViewHolder -> {
                val imgModel = (item as? ItemModel.ImgsItem)?.imgsNokatModel
                if (imgModel != null) {
                    holder.bind(imgModel, isInternetConnected)
                }
            }
            is AnotherViewHolder -> {
                val favImgModel = (item as? ItemModel.AnotherItem)?.favImgModel
                if (favImgModel != null) {
                    holder.bind(favImgModel)
                }
            }
        }
    }

    inner class ImgViewHolder(private val binding: ImgRowBinding) : RecyclerView.ViewHolder(binding.root) {
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

                    .asBitmap()

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
            binding.imageView.setOnClickListener {
                val itemModel = getItem(bindingAdapterPosition)
                if (itemModel is ItemModel.ImgsItem) {
                    val imgModel = itemModel.imgsNokatModel
                    val directions = ImgFragmentDirections.actionImgFragmentToImgFullFragment(imgModel)
                    frag.findNavController().navigate(directions)
                }
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

            val item = getItem(position)
            val imageUrl = if (item is ItemModel.ImgsItem) item.imgsNokatModel.image_url else null


            flipTheCoin(imageUrl ?: "button", if (randomNumber == 1) "gfgf" else "gdfgdfg")

        }

        private fun resetItemState(position: Int) {
            val recyclerView = frag.requireView().findViewById<RecyclerView>(R.id.rc_Img_Nokat)
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) as? ImgViewHolder
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

    inner class AnotherViewHolder(private val binding: ImageFullBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favImgModel: FavImgModel?) {
            // Binding logic for AnotherViewHolder
        }
    }

    fun updateInternetStatus(isConnected: Boolean) {
        isInternetConnected = isConnected
        notifyDataSetChanged()
    }

}