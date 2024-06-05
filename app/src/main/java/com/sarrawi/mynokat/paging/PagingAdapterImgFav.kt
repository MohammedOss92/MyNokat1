package com.sarrawi.mynokat.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.ImgRowBinding
import com.sarrawi.mynokat.model.FavImgModel


class PagingAdapterImgFav (val con: Context, val frag: Fragment) : PagingDataAdapter<FavImgModel, PagingAdapterImgFav.ViewHolder>(COMPARATOR) {

    var onbtnclick: ((Int,item:FavImgModel,Int) -> Unit)? = null

    inner class ViewHolder(private val binding: ImgRowBinding):RecyclerView.ViewHolder(binding.root) {

        init {
            binding.favBtn.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { item ->
                        onbtnclick?.invoke(item.id ?: 0, item, position)
                    }
                }
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
                .circleCrop()
                .centerCrop()
                .into(binding.imageView)
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