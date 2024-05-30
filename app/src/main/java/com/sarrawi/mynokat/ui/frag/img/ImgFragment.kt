package com.sarrawi.mynokat.ui.frag.img

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentImgBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.paging.PagingAdapterImg
import com.sarrawi.mynokat.paging.PagingAdapterNokat
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


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
            binding.rcImgNokat.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)


            binding.rcImgNokat.adapter = pagingAdapterImg

            nokatViewModel.getAllImage().observe(viewLifecycleOwner) { pagingData ->
                pagingAdapterImg.submitData(viewLifecycleOwner.lifecycle, pagingData)
            }


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