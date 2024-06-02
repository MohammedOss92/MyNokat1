package com.sarrawi.mynokat.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.NokatDesignBinding
import com.sarrawi.mynokat.model.NokatModel

class PagingAdapterNokat(val con: Context): PagingDataAdapter<NokatModel, PagingAdapterNokat.ViewHolder>(COMPARATOR) {

    var onItemClick2: ((item:NokatModel,position:Int) -> Unit)? = null
    var onItemClick: ((Int, NokatModel, Int) -> Unit)? = null

    inner class ViewHolder(private val binding: NokatDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.favBtn.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { item ->
                        onItemClick?.invoke(item.id ?: 0, item, position)
                    }
                }
            }
        }

        fun bind(nokatModel: NokatModel) {
            binding.apply {
                tvTitle.text = nokatModel.NokatTypes
                tvNokatN.text = nokatModel.NokatName
                newNokat.setImageResource(R.drawable.new_msg)
                newNokat.visibility = if (nokatModel.new_nokat == 0) View.INVISIBLE else View.VISIBLE

                if (nokatModel.is_fav) {
                    favBtn.setImageResource(R.drawable.baseline_favorite_true)
                } else {
                    favBtn.setImageResource(R.drawable.baseline_favorite_border_false)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NokatDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nokatModel = getItem(position)
        nokatModel?.let {
            holder.bind(it)
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<NokatModel>() {
            override fun areItemsTheSame(oldItem: NokatModel, newItem: NokatModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NokatModel, newItem: NokatModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}

//package com.sarrawi.mynokat.paging
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.paging.PagingDataAdapter
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.sarrawi.mynokat.R
//import com.sarrawi.mynokat.databinding.NokatDesignBinding
//import com.sarrawi.mynokat.model.NokatModel
//
//class PagingAdapterNokat(val con: Context): PagingDataAdapter<NokatModel, PagingAdapterNokat.ViewHolder>(COMPARATOR) {
//
//    var onItemClick2: ((item:NokatModel,position:Int) -> Unit)? = null
//    var onItemClick: ((Int,NokatModel, Int) -> Unit)? = null
//
//    inner class ViewHolder(private val binding: NokatDesignBinding) : RecyclerView.ViewHolder(binding.root) {
//        init {
//
//
//        }
//
//        fun bind(nokatModel: NokatModel) {
//            binding.apply {
//                tvTitle.text = nokatModel.NokatTypes
//                tvNokatN.text = nokatModel.NokatName
//                newNokat.setImageResource(R.drawable.new_msg)
//                if (nokatModel.new_nokat == 0) {
//                    newNokat.setVisibility(View.INVISIBLE)
//                } else {
//                    newNokat.setVisibility(View.VISIBLE)
//                }
//
//                // check if the item is favorite or not
//                if (nokatModel.is_fav) {
//                    favBtn.setImageResource(R.drawable.baseline_favorite_true)
//                } else {
//                    favBtn.setImageResource(R.drawable.baseline_favorite_border_false)
//                }
//            }
//
//            binding.favBtn.setOnClickListener {
//                val position = bindingAdapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    getItem(position)?.let { item ->
//                        onItemClick2?.invoke(item , position)
//
//                    }
//                }
//            }
//        }
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = NokatDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val nokatModel = getItem(position)
//        nokatModel?.let {
//            holder.bind(it)
//        }
//
//    }
//
//
//
//
//
//
//    companion object {
//
//        private val COMPARATOR = object : DiffUtil.ItemCallback<NokatModel>() {
//            override fun areItemsTheSame(oldItem: NokatModel, newItem: NokatModel): Boolean {
//                return oldItem.id == newItem.id
//            }
//
//            override fun areContentsTheSame(oldItem: NokatModel, newItem: NokatModel): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
//}