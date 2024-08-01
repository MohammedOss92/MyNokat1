package com.sarrawi.mynokat.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.sarrawi.mynokat.MyApplication
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.ActivityMainBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel
import com.sarrawi.mynokat.viewModel.SharedViewModel
import com.sarrawi.mynokat.viewModel.SharedViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var navController: NavController
    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService, LocaleSource(this), PostDatabase.getInstance(this)) }
    private val nokatViewModel: NokatViewModel by viewModels { MyViewModelFactory(mainRepository, this, PostDatabase.getInstance(this)) }
    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(this, SharedViewModelFactory(retrofitService, mainRepository, this, PostDatabase.getInstance(this))).get(SharedViewModel::class.java)
    }


    private lateinit var sharedViewModel2: SharedViewModel


    lateinit var viewModel: NokatViewModel
    var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val sharedViewModel: SharedViewModel by lazy {
            ViewModelProvider(this, SharedViewModelFactory(retrofitService, mainRepository, this, PostDatabase.getInstance(this))).get(SharedViewModel::class.java)
        }

//        sharedViewModel2 = (application as MyApplication).sharedViewModel

//        bottomNav = findViewById(R.id.bottomNav)

        navController = findNavController(R.id.nav_host_fragment_content_main)

//        appBarConfiguration = AppBarConfiguration(setOf(R.id.mainFragment, R.id.favFragment))

//        bottomNav.setupWithNavController(navController)

        val retrofitService = ApiService.provideRetrofitInstance()
        val mainRepository = NokatRepo(retrofitService, LocaleSource(this), PostDatabase.getInstance(this))
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepository, this, PostDatabase.getInstance(this))).get(
                NokatViewModel::class.java
            )

        FirebaseMessaging.getInstance().subscribeToTopic("alert")
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                // قم بتنفيذ العمليات المطلوبة هنا على الـ token

            }
            .addOnFailureListener { exception ->
                // قم بتنفيذ العمليات المطلوبة هنا في حالة فشل العملية
            }

        if (intent.hasExtra("targetScreen")) {
            val targetScreen = intent.getStringExtra("targetScreen")
            if ("screen1" == targetScreen) {
                navController.navigate(R.id.newNokatFragment)
            } else if ("screen2" == targetScreen) {
                navController.navigate(R.id.newImgFragment)
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}


