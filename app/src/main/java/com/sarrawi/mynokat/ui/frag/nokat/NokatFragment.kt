package com.sarrawi.mynokat.ui.frag.nokat

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
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
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.paging.PagingAdapterNokat
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class NokatFragment : Fragment() {

    private var _binding: FragmentNokatBinding? = null

    private val binding get() = _binding!!

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService, LocaleSource(requireContext()),
        PostDatabase.getInstance(requireContext())) }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(mainRepository, requireContext(), PostDatabase.getInstance(requireContext()))
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
        menu_item()
        setup()
        adapterOnClick()
    }

    private fun adapterOnClick() {
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        PagingAdapterNokat.onItemClick ={ i:Int,it:NokatModel,ii:Int->
            val fav = FavNokatModel(it.id,it.NokatTypes,it.new_nokat,it.NokatName,it.createdAt)
            fav.createdAt=currentTime

            if(it.is_fav){
                nokatViewModel.update_fav(it.id,false) // update favorite item state
                nokatViewModel.delete_fav(fav) //delete item from db
                Toast.makeText(requireContext(),"تم الحذف من المفضلة", Toast.LENGTH_SHORT).show()
                setUpRv()
                PagingAdapterNokat.notifyDataSetChanged()
            }
            else{
                nokatViewModel.update_fav(it.id,true)
                nokatViewModel.add_fav(fav) // add item to db
                Toast.makeText(requireContext(),"تم الاضافة الى المفضلة",Toast.LENGTH_SHORT).show()
                setUpRv()
                PagingAdapterNokat.notifyDataSetChanged()
            }
        }
    }

    private fun setUpRv() {
        if (isAdded) {
            binding.rcNokat.layoutManager = LinearLayoutManager(requireContext())

            binding.rcNokat.adapter = PagingAdapterNokat


//            nokatViewModel.getAllNokat().observe(viewLifecycleOwner) {
//
//                PagingAdapterNokat.submitData(viewLifecycleOwner.lifecycle, it)
//                PagingAdapterNokat.notifyDataSetChanged()
//
//
//            }

            lifecycleScope.launch {
                nokatViewModel.itemss.collectLatest { pagingData ->
                    PagingAdapterNokat.submitData(pagingData)
                    PagingAdapterNokat.notifyDataSetChanged()
                }
            }
            PagingAdapterNokat.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            binding.rcNokat.scrollToPosition(0)

        }

        /*viewModel.items.observe(this) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }*/

    }

    private fun setup() {
        if (isAdded) {
            binding.rcNokat.layoutManager = LinearLayoutManager(requireContext())

            val pagingAdapter = PagingAdapterNokat(requireContext())
            binding.rcNokat.adapter = pagingAdapter

//            nokatViewModel.getAllNokat().observe(viewLifecycleOwner) { pagingData ->
//                pagingAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
//            }

            lifecycleScope.launch {
                nokatViewModel.nokatStream.collectLatest { pagingData ->
                    pagingAdapter.submitData(pagingData)
                    PagingAdapterNokat.notifyDataSetChanged()
                }
            }

            PagingAdapterNokat.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            // يمكنك وضع scrollToPosition(0) هنا أو في مكان مناسب بالنسبة لدورة حياة مشهد الفريق
        }
    }


    private fun menu_item() {
        // The usage of an interface lets you inject your own implementation

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_nokat, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when(menuItem.itemId){

                    R.id.refresh ->{
                       nokatViewModel.refreshNokats()
                    }


                }
                return true
            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /*https://chatgpt.com/share/c05fc186-75e5-415b-99ec-ffb1a3435a14
    * https://chatgpt.com/share/9851d59c-005c-4c8d-a15b-32a29d2df7c9*/

}