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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentFavNokatBinding
import com.sarrawi.mynokat.databinding.FragmentNokatBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.paging.PagingAdapterNokat
import com.sarrawi.mynokat.paging.PagingAdapterNokatFav
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyVMFactory
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatVM
import com.sarrawi.mynokat.viewModel.NokatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavNokatFragment : Fragment() {

    private var _binding: FragmentFavNokatBinding? = null
    private val binding get() = _binding!!

    private val ID_Type_id=0
    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService, LocaleSource(requireContext()),
        PostDatabase.getInstance(requireContext())) }
    private val nokatViewModel: NokatVM by viewModels {
        MyVMFactory(mainRepository, requireContext(), PostDatabase.getInstance(requireContext()),ID_Type_id)
    }
    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null
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
        InterstitialAd_fun()
        loadInterstitialAd()
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

            clickCount++
            if (clickCount >= 2) {
// بمجرد أن يصل clickCount إلى 4، اعرض الإعلان
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                    loadInterstitialAd()
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
                clickCount = 0 // اعيد قيمة المتغير clickCount إلى الصفر بعد عرض الإعلان

            }
            nokatViewModel.viewModelScope.launch {
                nokatViewModel.update_fav(favNokatModel.id, false)
                val result = mainRepository.deleteFav(favNokatModel)
                val snackbar = Snackbar.make(requireView(), "تم الحذف من المفضلة", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
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


