package com.sarrawi.mynokat.paging

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ClipData
import android.content.Context
import android.os.Build
import android.text.ClipboardManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sarrawi.img.utils.Utils
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.NokatDesignBinding
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.ui.frag.MainFragmentDirections

class PagingAdapterNokat(val con: Context): PagingDataAdapter<NokatModel, PagingAdapterNokat.ViewHolder>(COMPARATOR) {

    var onItemClick2: ((item:NokatModel,position:Int) -> Unit)? = null
    var onItemClick: ((Int, NokatModel, Int) -> Unit)? = null


    inner class ViewHolder(private val binding: NokatDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.favBtn.setOnClickListener {

                it.animate().apply {
                    duration = 1000  // مدة الرسوم المتحركة بالمللي ثانية
                    rotationYBy(360f)  // يدور العنصر حول المحور Y بمقدار 360 درجة
                    setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            // الانتقال إلى ImgFragment بعد انتهاء الرسوم المتحركة
                            val position = bindingAdapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                getItem(position)?.let { item ->
                                    onItemClick?.invoke(item.id ?: 0, item, position)
                                }
                            }
                        }
                    })
                }.start()
            }

            binding.moreBtn.setOnClickListener {
                popupMenus(it)
            }
        }

        fun bind(nokatModel: NokatModel) {
            binding.apply {
//                tvTitle.text = nokatModel.NokatTypes
                tvNokatN.text = nokatModel.NokatName
                newNokat.setImageResource(R.drawable.new_msg)
                newNokat.visibility = if (nokatModel.new_nokat == 0) View.INVISIBLE else View.VISIBLE

                if (nokatModel.is_fav) {
                    favBtn.setImageResource(R.drawable.baseline_favorite_true)
                } else {
                    favBtn.setImageResource(R.drawable.baseline_favorite_border_false)
                }

                if (nokatModel.new_nokat == 0) {
                    binding.newNokat.setVisibility(View.INVISIBLE)
                } else {
                    binding.newNokat.setVisibility(View.VISIBLE)
                }


            }

        }

        fun popupMenus(view: View) {

            val popupMenu = PopupMenu(con,view)
            popupMenu.inflate(R.menu.menu_nokat_pop)
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.share ->{

                        Utils.ShareText(con, "", "",binding.tvNokatN.text.toString() )
                        true
                    }
                    R.id.copy ->{

                        Utils.CopyTxt(con,binding.tvNokatN)
                        true
                    }
                    R.id.messenger ->{

                        Utils.shareTextMessenger(con,binding.tvNokatN.text.toString(),view)
                        true
                    }
                    R.id.whats ->{

                        Utils.shareTextWhatsApp(con,binding.tvNokatN.text.toString(),view)
                        true
                    }

                    else -> true
                }
            }
            popupMenu.show()
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