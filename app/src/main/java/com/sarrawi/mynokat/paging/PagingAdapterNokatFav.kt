package com.sarrawi.mynokat.paging

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sarrawi.img.utils.Utils
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.NokatDesignBinding
import com.sarrawi.mynokat.databinding.NokatdesignfavBinding
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.model.NokatModel

class PagingAdapterNokatFav (val con: Context): PagingDataAdapter<FavNokatModel, PagingAdapterNokatFav.ViewHolder>(COMPARATOR) {

    var onItemClick: ((fav:FavNokatModel) -> Unit)? = null

    inner class ViewHolder(private val binding: NokatdesignfavBinding) : RecyclerView.ViewHolder(binding.root) {
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
                                    // تحديث حالة المفضلة
                                    onItemClick?.invoke(item)
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

        fun bind(favNokatModel: FavNokatModel) {
            binding.apply {
//                tvTitle.text = favNokatModel.NokatTypes
                tvNokatN.text = favNokatModel.NokatName
                newNokat.setImageResource(R.drawable.new_msg)
                newNokat.visibility = if (favNokatModel.new_nokat == 0) View.INVISIBLE else View.VISIBLE


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