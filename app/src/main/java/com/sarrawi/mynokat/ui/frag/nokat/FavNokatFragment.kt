package com.sarrawi.mynokat.ui.frag.nokat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentFavNokatBinding
import com.sarrawi.mynokat.databinding.FragmentNokatBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.paging.PagingAdapterNokat
import com.sarrawi.mynokat.paging.PagingAdapterNokatFav
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavNokatFragment : Fragment() {

    private var _binding: FragmentFavNokatBinding? = null
    private val binding get() = _binding!!

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService, LocaleSource(requireContext()),
        PostDatabase.getInstance(requireContext())) }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(mainRepository, requireContext(), PostDatabase.getInstance(requireContext()))
    }

    private val pagingAdapterNokatFav by lazy { PagingAdapterNokatFav(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavNokatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val bottomNav: BottomNavigationView = binding.bottomNavNokat

        // ربط BottomNavigationView مع NavController
        bottomNav.setupWithNavController(navController)
        setup()
        adapterOnClick()
    }

    private fun setup() {
        if (isAdded) {
            binding.rcNokatFav.layoutManager = LinearLayoutManager(requireContext())
            binding.rcNokatFav.adapter = pagingAdapterNokatFav

            nokatViewModel.favNokat.observe(viewLifecycleOwner) { pagingData ->
                pagingAdapterNokatFav.submitData(lifecycle, pagingData)
            }

            pagingAdapterNokatFav.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        }
    }


    private fun adapterOnClick() {

        pagingAdapterNokatFav.onItemClick = { favNokatModel ->
            nokatViewModel.viewModelScope.launch {
                nokatViewModel.update_fav(favNokatModel.id, false)
                val result = mainRepository.deleteFav(favNokatModel)
                Toast.makeText(requireContext(), "تم الحذف من المفضلة", Toast.LENGTH_SHORT).show()
                // يمكنك تحديث القائمة هنا إذا لزم الأمر
            }
        }


    }
    }


