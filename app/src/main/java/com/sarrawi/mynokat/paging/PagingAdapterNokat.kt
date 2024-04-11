package com.sarrawi.mynokat.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sarrawi.mynokat.databinding.NokatDesignBinding
import com.sarrawi.mynokat.model.NokatModel

class PagingAdapterNokat(val con: Context): PagingDataAdapter<NokatModel, PagingAdapterNokat.ViewHolder>(COMPARATOR) {

    inner class ViewHolder(private val binding: NokatDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        init {


        }

        fun bind(nokatModel: NokatModel) {
            binding.tvTitle.text = nokatModel.NokatName

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = com.sarrawi.mynokat.databinding.NokatDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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