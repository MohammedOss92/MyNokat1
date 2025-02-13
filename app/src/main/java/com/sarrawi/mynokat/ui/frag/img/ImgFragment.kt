package com.sarrawi.mynokat.ui.frag.img

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentImgBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.model.bind
import com.sarrawi.mynokat.model.bind.ITEM_TYPE_IMG
import com.sarrawi.mynokat.paging.Adapter
import com.sarrawi.mynokat.paging.PagingAdapterImg
import com.sarrawi.mynokat.paging.PagingAdapterNokat
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ImgFragment : Fragment() {

    private lateinit var _binding: FragmentImgBinding

    private val binding get() = _binding

    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1 // تعريف الثابت هنا
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 2 // تعريف الثابت هنا

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService, LocaleSource(requireContext()),
        PostDatabase.getInstance(requireContext())) }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(mainRepository, requireContext(), PostDatabase.getInstance(requireContext()))
    }

    private val pagingAdapterImg by lazy { PagingAdapterImg(requireActivity(),this) }
    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNav : BottomNavigationView

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(retrofitService,mainRepository, requireContext(), PostDatabase.getInstance(requireContext())) // تأكد من تهيئة ApiService بشكل صحيح
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val bottomNav: BottomNavigationView = binding.bottomNavImg
//        val adapter = Adapter(requireContext(), this, ITEM_TYPE_IMG)
//        val adapter = Adapter(requireContext(), this, bind.ITEM_TYPE_ANOTHER)

        // ربط BottomNavigationView مع NavController
        bottomNav.setupWithNavController(navController)

        nokatViewModel.isConnected.observe(requireActivity()) { isConnected ->
            if (isConnected) {
                setup()
                pagingAdapterImg.updateInternetStatus(isConnected)
                binding.lyNoInternet.visibility = View.GONE
            } else {
                binding.lyNoInternet.visibility = View.VISIBLE
                binding.rcImgNokat.visibility = View.GONE
                pagingAdapterImg.updateInternetStatus(isConnected)
            }
        }

        setup()
        nokatViewModel.checkNetworkConnection(requireContext())
        InterstitialAd_fun()
        loadInterstitialAd()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // إذا لم يكن لديك الإذن، قم بطلبه
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
        } else {
            // تم منح الإذن، قم بإنشاء المجلد مباشرة
            createDirectory()
        }


    }





    fun setup() {
        if (isAdded) {
// إعداد RecyclerView
            binding.rcImgNokat.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            binding.rcImgNokat.adapter = pagingAdapterImg

// جمع بيانات الصور من ViewModel
            lifecycleScope.launch {
                sharedViewModel.pagingDataFlow.collectLatest { pagingData ->
                    pagingAdapterImg.submitData(viewLifecycleOwner.lifecycle, pagingData)
                }
            }

// جمع البيانات المفضلة من ViewModel
            lifecycleScope.launch {
                sharedViewModel.favImgFlow.collectLatest { favoriteImages ->
                    // تحويل قائمة الصور المفضلة إلى قائمة من IDs
                    val favoriteImageIds = favoriteImages.map { it.id }

                    // تحقق من أن البيانات قد تم تحميلها
                    lifecycleScope.launch {
                        pagingAdapterImg.loadStateFlow.collect { loadStates ->
                            if (loadStates.refresh is LoadState.NotLoading) {
                                val updatedItems = pagingAdapterImg.snapshot().items.filterNotNull().map { image ->
                                    image.apply {
                                        is_fav = favoriteImageIds.contains(this.id as? Int ?: 0)
                                    }
                                }

                                // لا تقم بإعادة تقديم البيانات بشكل يدوي، فهذا ليس ضرورياً
                                // قد تحتاج إلى إعادة تقييم الحاجة لتحديث حالة `is_fav` في مكان آخر، مثل في `PagingSource` أو `ViewModel`
//                                pagingAdapterImg.submitData(PagingData.from(updatedItems))
                                pagingAdapterImg.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }




            // تحديد الإجراء الذي يتم تنفيذه عند النقر على عنصر في RecyclerView
            pagingAdapterImg.onItemClick = { item, position ->

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

                val currentTime =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val fav = FavImgModel(item.id, item.new_img, item.pic, item.image_url).apply {
                    createdAt = currentTime
                }

                if (item.is_fav) {
                    sharedViewModel.update_favs_img(item.id, false)
                    sharedViewModel.delete_favs_img(fav)
                    lifecycleScope.launch {
                        val snackbar = Snackbar.make(requireView(), "تم الحذف من المفضلة", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                        pagingAdapterImg.notifyItemChanged(position) // تحديث واجهة المستخدم بعد العملية
                    }
                } else {
                    sharedViewModel.update_favs_img(item.id, true)
                    sharedViewModel.add_favs_img(fav)
                    lifecycleScope.launch {
                        val snackbar = Snackbar.make(requireView(), "تم الاضافة الى المفضلة", Snackbar.LENGTH_SHORT)
                        snackbar.show()

                        pagingAdapterImg.notifyItemChanged(position) // تحديث واجهة المستخدم بعد العملية
                    }
                }
                lifecycleScope.launch {
                    pagingAdapterImg.notifyItemChanged(position)
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
        }}




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

    override fun onDestroyView() {
        super.onDestroyView()

    }

    private fun createDirectory() {
        val dir = File(Environment.getExternalStorageDirectory(), "MyPics")
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // تم منح الإذن، قم بإنشاء المجلد
                    createDirectory()
                }
            }
        }
    }

}
