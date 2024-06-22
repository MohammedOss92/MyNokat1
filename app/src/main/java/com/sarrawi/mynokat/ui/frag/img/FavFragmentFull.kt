package com.sarrawi.mynokat.ui.frag.img

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentFavBinding
import com.sarrawi.mynokat.databinding.FragmentImgFullBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.paging.PagingAdapterImgFav
import com.sarrawi.mynokat.paging.PagingAdapterImgFavFull
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel

class FavFragmentFull : Fragment() {
    private var _binding: FragmentImgFullBinding? = null
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
    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favimgModel= FavFragmentFullArgs.fromBundle(requireArguments()).full
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImgFullBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setuprc()
        adapterOnClick()
        InterstitialAd_fun()
        loadInterstitialAd()
    }

    fun setuprc() {
        if (isAdded) {
            binding.rcImgFull.layoutManager =
                LinearLayoutManager(requireContext())

            // تحميل البيانات باستخدام Paging
            nokatViewModel.favImg2.observe(viewLifecycleOwner) { pagingData ->
                // قم بتقديم البيانات إلى ال Adapter

                pagingAdapterImgFavFull.submitData(viewLifecycleOwner.lifecycle, pagingData)
                scrollToSelectedImage()

                if (pagingAdapterImgFavFull.snapshot().isEmpty()) {
                    findNavController().navigate(R.id.imgFragment)
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                } else {
                    scrollToSelectedImage()
                }
            }

            // تحديث RecyclerView عندما تأتي بيانات جديدة
            pagingAdapterImgFavFull.addLoadStateListener { loadState ->
                if (loadState.refresh is LoadState.NotLoading) {
                    nokatViewModel.getFav().observe(viewLifecycleOwner) { favoriteImages ->
                        // قم بتحديث حالة كل صورة
//                        favPagingAdapterImg.refresh(favoriteImages)
                        pagingAdapterImgFavFull.notifyDataSetChanged()
                        // إذا لم يتم تعيين Adapter بعد، قم بتعيينه
                        if (binding.rcImgFull.adapter == null) {
                            binding.rcImgFull.adapter = pagingAdapterImgFavFull
                        }
                    }
                }
            }
        }
    }

    private fun scrollToSelectedImage() {
        binding.rcImgFull.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
//                override fun onPreDraw(): Boolean {
//                    if (pagingAdapterImg.itemCount > 0) {
//                        val snapshot = pagingAdapterImg.snapshot()
//                        val position = snapshot.indexOfFirst { item ->
//                            (item is ItemModel.ImgsItem) && (item.imgsNokatModel.id == imgModel?.id)
//                        }
//                        if (position != -1) {
//                            binding.rcImgFull.scrollToPosition(position)
//                            Toast.makeText(requireContext(), "Item found", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(requireContext(), "Item not found", Toast.LENGTH_SHORT).show()
//                        }
//                        binding.rcImgFull.viewTreeObserver.removeOnPreDrawListener(this)
//                        return true
//                    }
//                    return false
//                }




                override fun onPreDraw(): Boolean {
                    if (pagingAdapterImgFavFull.itemCount > 0) {
                        val snapshot = pagingAdapterImgFavFull.snapshot()
                        val position = snapshot.indexOfFirst { it?.id == favimgModel.id }
                        if (position != -1) {
                            binding.rcImgFull.scrollToPosition(position)
//                            Toast.makeText(requireContext(), "Item found", Toast.LENGTH_SHORT).show()
                        } else {
//                            Toast.makeText(requireContext(), "Item not found", Toast.LENGTH_SHORT).show()
                        }
                        binding.rcImgFull.viewTreeObserver.removeOnPreDrawListener(this) ///
                        return true
                    }
                    return false
                }
            }
        )
    }

    private fun adapterOnClick() {

        pagingAdapterImgFavFull.onbtnclick = { id, item, position ->
            item.is_fav = false
            nokatViewModel.update_favs_img(id, false)
            nokatViewModel.delete_favs_img(
                FavImgModel(
                    item.id!!,
                    item.new_img,
                    item.pic,
                    item.image_url,
                    item.createdAt
                )
            )

            val snackbar = Snackbar.make(requireView(), "تم الحذف", Snackbar.LENGTH_SHORT)
            snackbar.show()
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
    fun showInterstitial(){
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
    }

}