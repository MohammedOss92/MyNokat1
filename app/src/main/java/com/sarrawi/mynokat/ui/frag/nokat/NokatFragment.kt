package com.sarrawi.mynokat.ui.frag.nokat

import android.content.ContentValues
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
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentNokatBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.paging.PagingAdapterNokat
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyVMFactory
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatVM
import com.sarrawi.mynokat.viewModel.NokatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*



class NokatFragment : Fragment() {

    private var _binding: FragmentNokatBinding? = null

    private val binding get() = _binding!!
    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null
    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService, LocaleSource(requireContext()),
        PostDatabase.getInstance(requireContext())) }
    private val ID_Type_id=0
    private var argsId = -1
    private val nokatViewModel: NokatVM by viewModels {
        MyVMFactory(mainRepository, requireContext(), PostDatabase.getInstance(requireContext()),argsId)
    }

    private val PagingAdapterNokat by lazy { PagingAdapterNokat(requireActivity()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        argsId = NokatFragmentArgs.fromBundle(requireArguments()).id
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
//        val navController = findNavController()
//        val bottomNav: BottomNavigationView = binding.bottomNavNokat
//
//        // ربط BottomNavigationView مع NavController
//        bottomNav.setupWithNavController(navController)
//        menu_item()
        setup()
        InterstitialAd_fun()
        loadInterstitialAd()

//        adapterOnClick()
    }

//    private fun adapterOnClick() {
//        PagingAdapterNokat.onItemClick = { id, item, position ->
//            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
//            val fav = FavNokatModel(item.id, item.NokatTypes, item.new_nokat, item.NokatName, item.createdAt)
//            fav.createdAt = currentTime
//
//            if (item.is_fav) {
//                nokatViewModel.update_fav(item.id, false) // update favorite item state
//                nokatViewModel.delete_fav(fav) // delete item from db
//                Toast.makeText(requireContext(), "تم الحذف من المفضلة", Toast.LENGTH_SHORT).show()
//            } else {
//                nokatViewModel.update_fav(item.id, true)
//                nokatViewModel.add_fav(fav) // add item to db
//                Toast.makeText(requireContext(), "تم الاضافة الى المفضلة", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//    }

    private fun setup() {
        if (isAdded) {
            binding.rcNokat.layoutManager = LinearLayoutManager(requireContext())

            val pagingAdapter = PagingAdapterNokat(requireContext())
            binding.rcNokat.adapter = pagingAdapter
//            lifecycleScope.launch {
//                nokatViewModel.invalidatePagingSource()
//                nokatViewModel.nokatFlow.collectLatest { pagingData ->
//                    pagingAdapter.submitData(pagingData)
//                }}
            lifecycleScope.launch {
                nokatViewModel.itemss.observe(viewLifecycleOwner) { pagingData ->
                    pagingAdapter.submitData(lifecycle, pagingData)
                }
            }

            pagingAdapter.onItemClick = { id, item, position ->
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

                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val fav = FavNokatModel(item.id, item.new_nokat, item.NokatName, item.createdAt).apply {
                    createdAt = currentTime
                }

                if (item.is_fav) {
                    nokatViewModel.update_favs(item.id, false)
                    nokatViewModel.delete_favs(fav)
                    lifecycleScope.launch {
                        val snackbar = Snackbar.make(requireView(), "تم الحذف من المفضلة", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                        pagingAdapter.notifyItemChanged(position) // Update UI after operation
                    }
                } else {
                    nokatViewModel.update_favs(item.id, true)
                    nokatViewModel.add_favs(fav)
                    lifecycleScope.launch {
                        val snackbar = Snackbar.make(requireView(), "تم الاضافة الى المفضلة", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                        pagingAdapter.notifyItemChanged(position) // Update UI after operation
                    }
                }
            }

            pagingAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
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
                nokatViewModel.itemss.observe(viewLifecycleOwner) { pagingData ->
                    PagingAdapterNokat.submitData(viewLifecycleOwner.lifecycle,pagingData)
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

//    private fun setup() {
//        if (isAdded) {
//            binding.rcNokat.layoutManager = LinearLayoutManager(requireContext())
//
//            val pagingAdapter = PagingAdapterNokat(requireContext())
//            binding.rcNokat.adapter = pagingAdapter
//
////            nokatViewModel.getAllNokat().observe(viewLifecycleOwner) { pagingData ->
////                pagingAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
////            }
//
//            lifecycleScope.launch {
//                nokatViewModel.nokatStream.collectLatest { pagingData ->
//                    pagingAdapter.submitData(pagingData)
//                    PagingAdapterNokat.notifyDataSetChanged()
//                }
//            }
//
//            PagingAdapterNokat.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
//            // يمكنك وضع scrollToPosition(0) هنا أو في مكان مناسب بالنسبة لدورة حياة مشهد الفريق
//        }
//    }


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

    fun hideprogressdialog() {
        Log.e("tesssst","entred")
        //  recreate()
        // mprogressdaialog!!.dismiss()
        binding.progressBar.visibility = View.GONE
        binding.tvLoad.visibility = View.GONE

        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(ContentValues.TAG, "Ad was dismissed.")
                    // Load the next interstitial ad.
                    loadInterstitialAd()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    Log.d(ContentValues.TAG, "Ad failed to show.")
                    // Load the next interstitial ad.
                    loadInterstitialAd()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(ContentValues.TAG, "Ad showed fullscreen content.")
                    mInterstitialAd = null
                }
            }
            mInterstitialAd?.show(requireActivity())
        } else {
            Log.d(ContentValues.TAG, "Ad wasn't loaded.")
            // Load the next interstitial ad.
            loadInterstitialAd()
        }

        mInterstitialAd?.show(requireActivity())




    }
    fun showprogressdialog() {

        binding.progressBar.visibility = View.VISIBLE
        binding.tvLoad.visibility  = View.VISIBLE


        //  mprogressdaialog = Dialog(this)
        //  mprogressdaialog!!.setCancelable(false)
        //  mprogressdaialog!!.setContentView(R.layout.progress_dialog)

        //  mprogressdaialog!!.show()
    }

    /*https://chatgpt.com/share/c05fc186-75e5-415b-99ec-ffb1a3435a14
    * https://chatgpt.com/share/9851d59c-005c-4c8d-a15b-32a29d2df7c9*/

}