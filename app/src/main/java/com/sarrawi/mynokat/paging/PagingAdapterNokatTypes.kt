package com.sarrawi.mynokat.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.NokatDesignBinding
import com.sarrawi.mynokat.databinding.NokattypeDesignBinding
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.model.NokatTypeModel
import com.sarrawi.mynokat.model.NokatTypeWithCount
import com.sarrawi.mynokat.ui.frag.nokat.NokatTypesFragmentDirections


class PagingAdapterNokatTypes(val con: Context,val frag: Fragment): PagingDataAdapter<NokatTypeWithCount, PagingAdapterNokatTypes.ViewHolder>(COMPARATOR) {

    var onItemClick: ((Int) -> Unit)? = null

    inner class ViewHolder(private val binding: NokattypeDesignBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { item ->
                        // تمرير المعرف إلى الاتجاه
                        val direction =
                            NokatTypesFragmentDirections.actionNokatTypesFragmentToNokatFragment(
                                item.msgTypes!!.id
                            )
                        findNavController(frag).navigate(direction)

                        // يمكنك استدعاء onItemClick إذا كنت بحاجة لذلك
                        onItemClick?.invoke(item.msgTypes!!.id)
                    }
                }


            }
        }


        fun bind(nokatTypeModel: NokatTypeWithCount) {
            binding.apply {
                tvTitleNokat.text = nokatTypeModel.msgTypes!!.NoktTypes
                tvCount.text = nokatTypeModel.subCount.toString()
                tvnewCount.text = nokatTypeModel.newNokatCount.toString()
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
        private val COMPARATOR = object : DiffUtil.ItemCallback<NokatTypeWithCount>() {
            override fun areItemsTheSame(oldItem: NokatTypeWithCount, newItem: NokatTypeWithCount): Boolean {
                return oldItem.msgTypes!!.id == newItem.msgTypes!!.id
            }

            override fun areContentsTheSame(oldItem: NokatTypeWithCount, newItem: NokatTypeWithCount): Boolean {
                return oldItem == newItem
            }
        }
    }
}


