package com.sarrawi.mynokat.ui.frag.tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentImgFullBinding
import com.sarrawi.mynokat.model.ImgsNokatModel

import com.sarrawi.mynokat.paging.PagingAdapterFullImg
import com.sarrawi.mynokat.paging.PagingAdapterImg
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel


class ImgFullFragment : Fragment() {
    private lateinit var _binding: FragmentImgFullBinding

    private val binding get() = _binding

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService) }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(mainRepository, requireContext())
    }

    private val PagingAdapterImgfull by lazy { PagingAdapterFullImg(requireActivity(),this) }
    private lateinit var imgModel: ImgsNokatModel
    private var ID = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imgModel = ImgFullFragmentArgs.fromBundle(requireArguments()).fullimg

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentImgFullBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        scrollToSelectedImage()
    }

    private fun setupRecyclerView() {
        binding.rcImgFull.layoutManager = LinearLayoutManager(requireContext())
        binding.rcImgFull.adapter = PagingAdapterImgfull
        nokatViewModel.getAllImage().observe(viewLifecycleOwner) { pagingData ->
            PagingAdapterImgfull.submitData(viewLifecycleOwner.lifecycle, pagingData)
        }
    }

    private fun scrollToSelectedImage() {
        // انتظر حتى يتم تحميل البيانات في RecyclerView
        PagingAdapterImgfull.addLoadStateListener { loadStates ->
            val snapshot = PagingAdapterImgfull.snapshot()
            if (loadStates.source.append.endOfPaginationReached && snapshot.isNotEmpty()) {
                val position = snapshot.indexOfFirst { it?.id == imgModel.id }
                if (position != -1) {
                    binding.rcImgFull.scrollToPosition(position)
                } else {
                    Toast.makeText(requireContext(), "Item not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setup() {
        if (isAdded) {
            binding.rcImgFull.layoutManager = LinearLayoutManager(requireContext())
            binding.rcImgFull.adapter = PagingAdapterImgfull
            nokatViewModel.getAllImage().observe(viewLifecycleOwner) { pagingData ->
                PagingAdapterImgfull.submitData(viewLifecycleOwner.lifecycle, pagingData)
            }
        }
    }
}