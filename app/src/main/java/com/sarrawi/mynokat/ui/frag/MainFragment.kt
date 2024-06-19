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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

import com.sarrawi.mynokat.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!
    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null


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

        val img:Button=view.findViewById(R.id.img)
        val words:Button=view.findViewById(R.id.words)
        InterstitialAd_fun()
        loadInterstitialAd()

        img.setOnClickListener {

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



        words.setOnClickListener {

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
                        val directions = MainFragmentDirections.actionMainFragment2ToNokatFragment()
                        findNavController().navigate(directions)
                    }
                })
            }.start()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}