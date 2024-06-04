package com.sarrawi.mynokat.ui.frag.img

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentImgBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.paging.PagingAdapterImg
import com.sarrawi.mynokat.paging.PagingAdapterNokat
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class ImgFragment : Fragment() {

    private lateinit var _binding: FragmentImgBinding

    private val binding get() = _binding

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService, LocaleSource(requireContext()),
        PostDatabase.getInstance(requireContext())) }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(mainRepository, requireContext(), PostDatabase.getInstance(requireContext()))
    }

    private val pagingAdapterImg by lazy { PagingAdapterImg(requireActivity(),this) }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNav : BottomNavigationView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val bottomNav: BottomNavigationView = binding.bottomNavImg

        // ربط BottomNavigationView مع NavController
        bottomNav.setupWithNavController(navController)

        nokatViewModel.isConnected.observe(requireActivity()) { isConnected ->
            if (isConnected) {
                setup()
                pagingAdapterImg.updateInternetStatus(isConnected)
                binding.lyNoInternet.visibility = View.GONE
            } else {
                binding.lyNoInternet.visibility = View.VISIBLE
                binding.rcImgNokat.visibility = View.GONE
                pagingAdapterImg.updateInternetStatus(isConnected)
            }
        }


        nokatViewModel.checkNetworkConnection(requireContext())

    }





    fun setup() {
        if (isAdded) {
            // تعيين إعدادات RecyclerView
            binding.rcImgNokat.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

            // تعيين Adapter لـ RecyclerView
            binding.rcImgNokat.adapter = pagingAdapterImg

            // مراقبة تغييرات البيانات في ViewModel وتقديم البيانات إلى ال Adapter
            nokatViewModel.getAllImage().observe(viewLifecycleOwner) { pagingData ->
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



//    private fun setup() {
//        if (isAdded) {
//            binding.rcImgNokat.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
//
//            val pagingAdapter = PagingAdapterImg(requireContext(), this)
//            binding.rcImgNokat.adapter = pagingAdapter
//
//            lifecycleScope.launch {
//                nokatViewModel.ImageStream.collectLatest { pagingData ->
//                    pagingAdapter.submitData(pagingData)
//                }
//            }
//        }
//    }

    /*private fun setupInitialData() {
        // هنا يمكنك الحصول على البيانات مباشرة من الـ Adapter وعرضها في الـ Fragment
        val initialData = adapter.snapshot() // استخدام snapshot للحصول على البيانات الحالية في الـ Adapter

        // التحقق من البيانات وعرضها في الـ Fragment
        initialData.forEach { item ->
            if (item is ItemModel.ImgsItem) {
                val imgModel = item.imgsNokatModel
                // هنا يمكنك استخدام imgModel لعرض البيانات في الـ Fragment
                // مثلاً:
                // imageView.setImageURI(Uri.parse(imgModel.image_url))
            }
        }*/


    override fun onDestroyView() {
        super.onDestroyView()

    }



}
/*
            * val mutableListShows = listShows.toMutableList()
            // إضافة عنصر "مرحبا" إلى القائمة
            mutableListShows.add("مرحبا")
            showSnackbar("لا يوجد بيانات")
            // تحديث القائمة بعد الإضافة
            listShows = mutableListShows.toList()*/

//    fun adapterOnClick(){
//        pagingAdapterImg.onItemClick = {
//            val directions = ImgFragmentDirections.actionImgFragmentToImgFullFragment()
//            findNavController().navigate(directions)
//        }
//    }