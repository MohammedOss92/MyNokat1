package com.sarrawi.mynokat.ui.frag.img

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentImgFullBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.model.ItemModel
import com.sarrawi.mynokat.model.Model2
import com.sarrawi.mynokat.paging.PagingAdapterFullImg
import com.sarrawi.mynokat.paging.PagingAdapterImg
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class ImgFullFragment : Fragment() {
    private lateinit var _binding: FragmentImgFullBinding
    private val binding get() = _binding

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService, LocaleSource(requireContext()), PostDatabase.getInstance(requireContext())) }
    private val nokatViewModel: NokatViewModel by viewModels { MyViewModelFactory(mainRepository, requireContext(), PostDatabase.getInstance(requireContext())) }
    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(retrofitService) // تأكد من تهيئة ApiService بشكل صحيح
    }

    private val pagingAdapterfullImg by lazy { PagingAdapterFullImg(requireActivity(), this) }
    private lateinit var imgModel: ImgsNokatModel
    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imgModel = ImgFullFragmentArgs.fromBundle(requireArguments()).fullimg


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentImgFullBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nokatViewModel.isConnected.observe(requireActivity()) { isConnected ->
            if (isConnected) {
                setup()
                pagingAdapterfullImg.updateInternetStatus(isConnected)
                binding.lyNoInternet.visibility = View.GONE
            } else {
                binding.lyNoInternet.visibility = View.VISIBLE
                binding.rcImgFull.visibility = View.GONE
                pagingAdapterfullImg.updateInternetStatus(isConnected)
            }
        }

        nokatViewModel.checkNetworkConnection(requireContext())
        InterstitialAd_fun()
        loadInterstitialAd()
    }

    fun setup() {
        if (isAdded) {
            // تعيين إعدادات RecyclerView
            binding.rcImgFull.layoutManager = LinearLayoutManager(requireContext())

            // تعيين Adapter لـ RecyclerView
            binding.rcImgFull.adapter = pagingAdapterfullImg
            lifecycleScope.launch {
            sharedViewModel.pagingDataFlow.collectLatest { pagingData ->
                lifecycleScope.launch {
                    pagingAdapterfullImg.submitData(viewLifecycleOwner.lifecycle, pagingData)
                    scrollToSelectedImage()
                }
            }

                // مراقبة تغييرات البيانات في ViewModel وتقديم البيانات إلى ال Adapter
//            nokatViewModel.getAllImage().observe(viewLifecycleOwner) { pagingData ->
//
//                    pagingAdapterfullImg.submitData(viewLifecycleOwner.lifecycle,pagingData)
//                    scrollToSelectedImage()


                nokatViewModel.favImg.observe(viewLifecycleOwner) { favoriteImages ->
                    // تحويل قائمة الصور المفضلة إلى قائمة من IDs
                    val favoriteImageIds = favoriteImages.map { it.id }

                    lifecycleScope.launch {
                        pagingAdapterfullImg.loadStateFlow.collect { loadStates ->
                            if (loadStates.refresh is LoadState.NotLoading) {
                                // التحقق من الصور المفضلة وتحديث حالة الصور في PagingData
                                pagingAdapterfullImg.snapshot().items.forEach { image ->
                                    image?.let {
                                        it.is_fav = favoriteImageIds.contains(it.id as? Int ?: 0)
// تحقق مما إذا كانت الصورة مفضلة
                                    }
                                }

                                // تحديث واجهة المستخدم بعد تحديث البيانات
                                pagingAdapterfullImg.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }




        }





        // تحديد الإجراء الذي يتم تنفيذه عند النقر على عنصر في RecyclerView
        pagingAdapterfullImg.onItemClick = { item, position ->
            val currentTime =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val fav = FavImgModel(item.id, item.new_img, item.pic, item.image_url).apply {
                createdAt = currentTime
            }

            if (item.is_fav) {
                nokatViewModel.update_favs_img(item.id, false)
                nokatViewModel.delete_favs_img(fav)
                lifecycleScope.launch {
                    val snackbar = Snackbar.make(requireView(), "تم الحذف من المفضلة", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                    pagingAdapterfullImg.notifyItemChanged(position) // تحديث واجهة المستخدم بعد العملية
                }
            } else {
                nokatViewModel.update_favs_img(item.id, true)
                nokatViewModel.add_favs_img(fav)
                lifecycleScope.launch {
                    val snackbar = Snackbar.make(requireView(), "تم الاضافة الى المفضلة", Snackbar.LENGTH_SHORT)
                    snackbar.show()

                    pagingAdapterfullImg.notifyItemChanged(position) // تحديث واجهة المستخدم بعد العملية
                }
            }
            lifecycleScope.launch {
                pagingAdapterfullImg.notifyItemChanged(position)
            }
            // في دالة onItemClick داخل setup()
            if (item.is_fav) {
                Log.d("TAG", "Item is now favorite")
            } else {
                Log.d("TAG", "Item is not favorite")
            }

//
            //            }
        }
    }





    private fun scrollToSelectedImage() {
        binding.rcImgFull.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (pagingAdapterfullImg.itemCount > 0) {
                        val snapshot = pagingAdapterfullImg.snapshot()
                        val position = snapshot.indexOfFirst { it?.id == imgModel.id }
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