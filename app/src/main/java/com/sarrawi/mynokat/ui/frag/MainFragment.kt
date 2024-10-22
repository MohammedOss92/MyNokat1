package com.sarrawi.mynokat.ui.frag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.sarrawi.mynokat.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.AdRequest

import com.sarrawi.mynokat.api.ApiService

import com.sarrawi.mynokat.databinding.FragmentMainBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyVMFactory
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatVM
import com.sarrawi.mynokat.viewModel.NokatViewModel
import com.sarrawi.mynokat.workmanager.FetchDataWorker
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!
    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null
    private val ID_Type_id=0
    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService, LocaleSource(requireContext()),
        PostDatabase.getInstance(requireContext())) }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(mainRepository, requireContext(), PostDatabase.getInstance(requireContext()))
    }

    private val nokatVM: NokatVM by viewModels {
        MyVMFactory(mainRepository, requireContext(), PostDatabase.getInstance(requireContext()),ID_Type_id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val adapter = ViewPagerAdapter(childFragmentManager)
//
//        adapter.addFragment(NokatFragment(), "الكلمات")
//
//        adapter.addFragment(ImgFragment(),"الصور")
//
//        binding.vpager.adapter=adapter
//        binding.tblayout.setupWithViewPager(binding.vpager)


        InterstitialAd_fun()
        loadInterstitialAd()

        val fetchDataRequest = OneTimeWorkRequest.Builder(FetchDataWorker::class.java).build()
        WorkManager.getInstance(requireContext()).enqueue(fetchDataRequest)
        binding.img.setOnClickListener {

            clickCount++
            if (clickCount >= 1) {
// بمجرد أن يصل clickCount إلى 4، اعرض الإعلان
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                    loadInterstitialAd()
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
                clickCount = 0 // اعيد قيمة المتغير clickCount إلى الصفر بعد عرض الإعلان

            }
            it.animate().apply {
                duration = 1000  // مدة الرسوم المتحركة بالمللي ثانية
                rotationYBy(360f)  // يدور العنصر حول المحور Y بمقدار 360 درجة
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        // الانتقال إلى ImgFragment بعد انتهاء الرسوم المتحركة
                        val directions = MainFragmentDirections.actionMainFragment2ToImgFragment()
                        findNavController().navigate(directions)
                    }
                })
            }.start()
        }



        binding.words.setOnClickListener {

            clickCount++
            if (clickCount >= 1) {
// بمجرد أن يصل clickCount إلى 4، اعرض الإعلان
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                    loadInterstitialAd()
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
                clickCount = 0 // اعيد قيمة المتغير clickCount إلى الصفر بعد عرض الإعلان

            }
            it.animate().apply {

                duration = 1000  // مدة الرسوم المتحركة بالمللي ثانية
                rotationYBy(360f)  // يدور العنصر حول المحور Y بمقدار 360 درجة
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        // الانتقال إلى ImgFragment بعد انتهاء الرسوم المتحركة
                        val directions = MainFragmentDirections.actionMainFragment2ToNokatTypesFragment()
                        findNavController().navigate(directions)
                    }
                })
            }.start()
        }

        lifecycleScope.launch {

            nokatViewModel.countLiveDataa.observe(viewLifecycleOwner, Observer { count ->
                // تحديث TextView بالعدد الإجمالي للصور
                binding.imageCountTextView.setText("عدد الصور: "+count.toString())
                // تسجيل العدد الإجمالي للصور في السجل
                Log.d("YourFragment", "Total image count: $count")
            })
        }
        // Trigger the data load
        nokatViewModel.fetchImageCount()


        nokatViewModel.countLiveDatanokat.observe(viewLifecycleOwner, Observer { count ->
            binding.wordCountTextView.setText("عدد النكت: "+ count.toString())
        })







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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}