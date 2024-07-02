package com.sarrawi.mynokat.ui.frag.nokat

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.api.ApiService.Companion.retrofitService
import com.sarrawi.mynokat.databinding.FragmentNokatBinding
import com.sarrawi.mynokat.databinding.FragmentNokatTypesBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.paging.PagingAdapterNokat
import com.sarrawi.mynokat.paging.PagingAdapterNokatTypes
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class NokatTypesFragment : Fragment() {

    var _binding:FragmentNokatTypesBinding? = null

    private val binding get() = _binding!!
    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService, LocaleSource(requireContext()),
        PostDatabase.getInstance(requireContext())) }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(mainRepository, requireContext(), PostDatabase.getInstance(requireContext()))
    }

    private val pagingAdapterNokatTypes by lazy { PagingAdapterNokatTypes(requireActivity()) }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNokatTypesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val bottomNav: BottomNavigationView = binding.bottomNavNokatType

        // ربط BottomNavigationView مع NavController
        bottomNav.setupWithNavController(navController)
        menu_item()
        setup()
        adapterOnClick()
        InterstitialAd_fun()
        loadInterstitialAd()
    }

    private fun setup() {
        if (isAdded) {
            binding.rcNokatType.layoutManager = LinearLayoutManager(requireContext())

            val pagingAdapter = PagingAdapterNokatTypes(requireContext())
            binding.rcNokatType.adapter = pagingAdapter
            lifecycleScope.launch {
                nokatViewModel.invalidatePagingSourceTypes()
                nokatViewModel.nokatTypesFlow.collectLatest { pagingData ->
                    Log.d("NokatTypeFlow", "Received new paging data: $pagingData")
                    pagingAdapter.submitData(pagingData)
                }}



            pagingAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }


    private fun adapterOnClick(){

        pagingAdapterNokatTypes.onItemClick = {id ->

            clickCount++
            if (clickCount >= 2) {
// بمجرد أن يصل clickCount إلى 4، اعرض الإعلان
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
                clickCount = 0 // اعيد قيمة المتغير clickCount إلى الصفر بعد عرض الإعلان

            }

            val direction = NokatTypesFragmentDirections.actionNokatTypesFragmentToNokatFragment(id)
            findNavController().navigate(direction)
        }

    }

    fun InterstitialAd_fun (){


        MobileAds.initialize(requireActivity()) { initializationStatus ->
            // do nothing on initialization complete
        }

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireActivity(),
            "ca-app-pub-1895204889916566/1691767609",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.i("onAdLoadedL", "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.d("onAdLoadedF", loadAdError.toString())
                    mInterstitialAd = null
                }
            }
        )
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
                        lifecycleScope.launch {
                            nokatViewModel.refreshNokats(
                                ApiService.provideRetrofitInstance(),
                                PostDatabase.getInstance(requireContext()))
                        }
                        //                        if (mInterstitialAd != null) {
//                            mInterstitialAd?.show(requireActivity())
//                        } else {
//                            // Handle the case when ad is not loaded yet
//                            // You might want to load the ad again or show an alternative action
//                        }
                    }


                }
                return true
            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun loadInterstitialAd() {
        MobileAds.initialize(requireActivity()) { initializationStatus ->
            // do nothing on initialization complete
        }

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireActivity(),
            "ca-app-pub-1895204889916566/1691767609",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.i("onAdLoadedL", "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.d("onAdLoadedF", loadAdError.toString())
                    mInterstitialAd = null
                }
            }
        )
    }

}