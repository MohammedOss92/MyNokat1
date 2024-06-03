package com.sarrawi.mynokat.ui.frag.img

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentImgFullBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.model.ItemModel
import com.sarrawi.mynokat.paging.PagingAdapterFullImg
import com.sarrawi.mynokat.paging.PagingAdapterImg
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel

class ImgFullFragment : Fragment() {
    private lateinit var _binding: FragmentImgFullBinding
    private val binding get() = _binding

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService, LocaleSource(requireContext()),
        PostDatabase.getInstance(requireContext())) }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(mainRepository, requireContext(), PostDatabase.getInstance(requireContext()))
    }

    private val pagingAdapterImgFull by lazy { PagingAdapterFullImg(requireActivity(), this) }
    private val pagingAdapterImg by lazy { PagingAdapterImg(requireActivity(), this) }
    private lateinit var imgModel: ImgsNokatModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imgModel = ImgFullFragmentArgs.fromBundle(requireArguments()).fullimg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImgFullBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nokatViewModel.isConnected.observe(requireActivity()) { isConnected ->
            if (isConnected) {
                setupRecyclerView()
                pagingAdapterImgFull.updateInternetStatus(isConnected)
                binding.lyNoInternet.visibility = View.GONE
            } else {
                binding.lyNoInternet.visibility = View.VISIBLE
                binding.rcImgFull.visibility = View.GONE
                pagingAdapterImgFull.updateInternetStatus(isConnected)
            }
        }


        nokatViewModel.checkNetworkConnection(requireContext())
    }

    private fun setupRecyclerView() {
        binding.rcImgFull.layoutManager = LinearLayoutManager(requireContext()).apply {
//            reverseLayout = true // // نفعل هذا الخيار
//            stackFromEnd = true // مع هذا ليتم تحريك موقع العناصر
        }
        binding.rcImgFull.adapter = pagingAdapterImg
        nokatViewModel.getAllImage().observe(viewLifecycleOwner) { pagingData ->
            pagingAdapterImg.submitData(viewLifecycleOwner.lifecycle, pagingData)
            scrollToSelectedImage()
        }
    }

    /// لازم ننتظر تتحمل البيانات بشكل كامل ويتم اظهارها على الشاشه

    private fun scrollToSelectedImage() {
        // اضفت viewTreeObserver  للتحقق من أن البيانات قد تم تحميلها بالكامل والعرض
        binding.rcImgFull.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {

                override fun onPreDraw(): Boolean {
                    if (pagingAdapterImg.itemCount > 0) {
                        val snapshot = pagingAdapterImg.snapshot()
                        val position = snapshot.indexOfFirst { item ->
                            (item is ItemModel.ImgsItem) && (item.imgsNokatModel.id == imgModel?.id)
                        }
                        if (position != -1) {
                            binding.rcImgFull.scrollToPosition(position)
                            Toast.makeText(requireContext(), "Item found", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Item not found", Toast.LENGTH_SHORT).show()
                        }
                        binding.rcImgFull.viewTreeObserver.removeOnPreDrawListener(this)
                        return true
                    }
                    return false
                }

//                override fun onPreDraw(): Boolean {
//                    if (pagingAdapterImg.itemCount > 0) {
//                        val snapshot = pagingAdapterImg.snapshot()
//                        val position = snapshot.indexOfFirst { it?.id == imgModel.id }
//                        if (position != -1) {
//                            binding.rcImgFull.scrollToPosition(position)
//                            Toast.makeText(requireContext(), "Item found", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(requireContext(), "Item not found", Toast.LENGTH_SHORT).show()
//                        }
//                        binding.rcImgFull.viewTreeObserver.removeOnPreDrawListener(this) ///
//                        return true
//                    }
//                    return false
//                }
            }
        )
    }
}