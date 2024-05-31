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
import com.sarrawi.mynokat.databinding.NokatdesignfavBinding
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.model.NokatModel

class PagingAdapterNokatFav (val con: Context): PagingDataAdapter<FavNokatModel, PagingAdapterNokatFav.ViewHolder>(COMPARATOR) {

    var onItemClick2: ((item:NokatModel,position:Int) -> Unit)? = null
    var onItemClick: ((fav:FavNokatModel) -> Unit)? = null

    inner class ViewHolder(private val binding: NokatdesignfavBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.favBtn.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { item ->
                         // تحديث حالة المفضلة
                        notifyItemChanged(position) // تحديث العنصر في القائمة
                        onItemClick?.invoke(item)
                        //onItemClick?.invoke(item.id ?: 0, item, position)
                    }
                }
            }
        }

        fun bind(favNokatModel: FavNokatModel) {
            binding.apply {
                tvTitle.text = favNokatModel.NokatTypes
                tvNokatN.text = favNokatModel.NokatName
                newNokat.setImageResource(R.drawable.new_msg)
                newNokat.visibility = if (favNokatModel.new_nokat == 0) View.INVISIBLE else View.VISIBLE


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NokatdesignfavBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favNokatModel = getItem(position)
        favNokatModel?.let {
            holder.bind(it)
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<FavNokatModel>() {
            override fun areItemsTheSame(oldItem: FavNokatModel, newItem: FavNokatModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FavNokatModel, newItem: FavNokatModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}