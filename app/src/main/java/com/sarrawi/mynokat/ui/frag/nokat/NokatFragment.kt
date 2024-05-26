package com.sarrawi.mynokat.ui.frag.nokat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentMainBinding
import com.sarrawi.mynokat.databinding.FragmentNokatBinding
import com.sarrawi.mynokat.paging.PagingAdapterNokat
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel


class NokatFragment : Fragment() {

    private var _binding: FragmentNokatBinding? = null

    private val binding get() = _binding!!

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService) }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(mainRepository, requireContext())
    }

    private val PagingAdapterNokat by lazy { PagingAdapterNokat(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNokatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val bottomNav: BottomNavigationView = binding.bottomNavNokat

        // ربط BottomNavigationView مع NavController
        bottomNav.setupWithNavController(navController)
        setup()
    }

    private fun setUpRv() {
        if (isAdded) {
            binding.rcNokat.layoutManager = LinearLayoutManager(requireContext())

            binding.rcNokat.adapter = PagingAdapterNokat


            nokatViewModel.getAllNokat().observe(viewLifecycleOwner) {

                PagingAdapterNokat.submitData(viewLifecycleOwner.lifecycle, it)
                PagingAdapterNokat.notifyDataSetChanged()


            }
            PagingAdapterNokat.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            binding.rcNokat.scrollToPosition(0)

        }


    }

    private fun setup() {
        if (isAdded) {
            binding.rcNokat.layoutManager = LinearLayoutManager(requireContext())

            val pagingAdapter = PagingAdapterNokat(requireContext())
            binding.rcNokat.adapter = pagingAdapter

            nokatViewModel.getAllNokat().observe(viewLifecycleOwner) { pagingData ->
                pagingAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
            }

            PagingAdapterNokat.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            // يمكنك وضع scrollToPosition(0) هنا أو في مكان مناسب بالنسبة لدورة حياة مشهد الفريق
        }
    }

}