package com.sarrawi.mynokat.ui.frag.img

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentImgFullBinding
import com.sarrawi.mynokat.databinding.FragmentNewImgBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.paging.PagingAdapterImgFavFull
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel


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
    private val pagingAdapterImgFavFull by lazy { PagingAdapterImgFavFull(requireActivity(), this) }
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


}