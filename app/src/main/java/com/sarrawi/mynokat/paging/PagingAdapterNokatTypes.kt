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
import com.sarrawi.mynokat.databinding.NokattypeDesignBinding
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.model.NokatTypeModel


class PagingAdapterNokatTypes(val con: Context): PagingDataAdapter<NokatTypeModel, PagingAdapterNokatTypes.ViewHolder>(COMPARATOR) {

    var onItemClick: ((Int) -> Unit)? = null

    inner class ViewHolder(private val binding: NokattypeDesignBinding): RecyclerView.ViewHolder(binding.root) {


        fun bind(nokatTypeModel: NokatTypeModel) {
            binding.apply {
                tvTitleNokat.text = nokatTypeModel.NoktTypes

            }

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nokatTypeModel = getItem(position)
        nokatTypeModel?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NokattypeDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<NokatTypeModel>() {
            override fun areItemsTheSame(oldItem: NokatTypeModel, newItem: NokatTypeModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NokatTypeModel, newItem: NokatTypeModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}


