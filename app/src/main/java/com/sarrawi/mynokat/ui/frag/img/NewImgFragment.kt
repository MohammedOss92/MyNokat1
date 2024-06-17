package com.sarrawi.mynokat.ui.frag.img

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentImgFullBinding
import com.sarrawi.mynokat.databinding.FragmentNewImgBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.paging.PagingAdapterImg
import com.sarrawi.mynokat.paging.PagingAdapterImgFavFull
import com.sarrawi.mynokat.paging.PagingAdpterNewImg
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class NewImgFragment : Fragment() {
    private var _binding: FragmentNewImgBinding? = null
    private val binding get() = _binding!!

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy {
        NokatRepo(
            retrofitService, LocaleSource(requireContext()),
            PostDatabase.getInstance(requireContext())
        )
    }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(
            mainRepository,
            requireContext(),
            PostDatabase.getInstance(requireContext())
        )
    }
    private val pagingAdapterImg by lazy { PagingAdpterNewImg(requireActivity(),this) }

    private lateinit var favimgModel: FavImgModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentNewImgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val bottomNav: BottomNavigationView = binding.bottomNavImg
//        val adapter = Adapter(requireContext(), this, ITEM_TYPE_IMG)
//        val adapter = Adapter(requireContext(), this, bind.ITEM_TYPE_ANOTHER)

        // ربط BottomNavigationView مع NavController
        bottomNav.setupWithNavController(navController)

        nokatViewModel.isConnected.observe(requireActivity()) { isConnected ->
            if (isConnected) {
                setup()
                pagingAdapterImg.updateInternetStatus(isConnected)
                binding.lyNoInternet.visibility = View.GONE
            } else {
                binding.lyNoInternet.visibility = View.VISIBLE
                binding.rcImgNokatNew.visibility = View.GONE
                pagingAdapterImg.updateInternetStatus(isConnected)
            }
        }

        setup()
        nokatViewModel.checkNetworkConnection(requireContext())



    }


    fun setup() {
        if (isAdded) {
            // تعيين إعدادات RecyclerView
            binding.rcImgNokatNew.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

            // تعيين Adapter لـ RecyclerView
            binding.rcImgNokatNew.adapter = pagingAdapterImg

            // مراقبة تغييرات البيانات في ViewModel وتقديم البيانات إلى ال Adapter
            nokatViewModel.getAllImageNew().observe(viewLifecycleOwner) { pagingData ->
                lifecycleScope.launch {
                    pagingAdapterImg.submitData(pagingData)
                }

                nokatViewModel.favImg.observe(viewLifecycleOwner) { favoriteImages ->
                    // تحويل قائمة الصور المفضلة إلى قائمة من IDs
                    val favoriteImageIds = favoriteImages.map { it.id }

                    lifecycleScope.launch {
                        pagingAdapterImg.loadStateFlow.collect { loadStates ->
                            if (loadStates.refresh is LoadState.NotLoading) {
                                // التحقق من الصور المفضلة وتحديث حالة الصور في PagingData
                                pagingAdapterImg.snapshot().items.forEach { image ->
                                    image?.let {
                                        it.is_fav = favoriteImageIds.contains(it.id as? Int ?: 0)
// تحقق مما إذا كانت الصورة مفضلة
                                    }
                                }

                                // تحديث واجهة المستخدم بعد تحديث البيانات
                                pagingAdapterImg.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }




        }





        // تحديد الإجراء الذي يتم تنفيذه عند النقر على عنصر في RecyclerView
        pagingAdapterImg.onItemClick = { item, position ->
            val currentTime =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val fav = FavImgModel(item.id, item.new_img, item.pic, item.image_url).apply {
                createdAt = currentTime
            }

            if (item.is_fav) {
                nokatViewModel.update_favs_img(item.id, false)
                nokatViewModel.delete_favs_img(fav)
                lifecycleScope.launch {
                    val snackbar = Snackbar.make(requireView(), "تم الحذف من المفضلة", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                    pagingAdapterImg.notifyItemChanged(position) // تحديث واجهة المستخدم بعد العملية
                }
            } else {
                nokatViewModel.update_favs_img(item.id, true)
                nokatViewModel.add_favs_img(fav)
                lifecycleScope.launch {
                    val snackbar = Snackbar.make(requireView(), "تم الاضافة الى المفضلة", Snackbar.LENGTH_SHORT)
                    snackbar.show()

                    pagingAdapterImg.notifyItemChanged(position) // تحديث واجهة المستخدم بعد العملية
                }
            }
            lifecycleScope.launch {
                pagingAdapterImg.notifyItemChanged(position)
            }
            // في دالة onItemClick داخل setup()
            if (item.is_fav) {
                Log.d("TAG", "Item is now favorite")
            } else {
                Log.d("TAG", "Item is not favorite")
            }



//
            //            }
        }
    }

}