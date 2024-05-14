package com.sarrawi.mynokat.ui.frag.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentImgBinding
import com.sarrawi.mynokat.paging.PagingAdapterImg
import com.sarrawi.mynokat.paging.PagingAdapterNokat
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel


class ImgFragment : Fragment() {

    private lateinit var _binding: FragmentImgBinding

    private val binding get() = _binding

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService) }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(mainRepository, requireContext())
    }

    private val PagingAdapterImg by lazy { PagingAdapterImg(requireActivity()) }



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
        setup()
    }





    private fun setup() {
        if (isAdded) {
            binding.rcImgNokat.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)


            binding.rcImgNokat.adapter = PagingAdapterImg

            nokatViewModel.getAllImage().observe(viewLifecycleOwner) { pagingData ->
                PagingAdapterImg.submitData(viewLifecycleOwner.lifecycle, pagingData)
            }
            /*
            * val mutableListShows = listShows.toMutableList()
            // إضافة عنصر "مرحبا" إلى القائمة
            mutableListShows.add("مرحبا")
            showSnackbar("لا يوجد بيانات")
            // تحديث القائمة بعد الإضافة
            listShows = mutableListShows.toList()*/

        }

        }

    override fun onDestroyView() {
        super.onDestroyView()

    }



}