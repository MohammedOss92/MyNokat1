package com.sarrawi.mynokat.bcr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope

import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.NokatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch





class InstallReceiver : BroadcastReceiver() {

    private lateinit var viewModel: NokatViewModel

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("InstallReceiver", "Received broadcast")
        if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
            val packageName = intent.data?.schemeSpecificPart
            if (packageName == context.packageName) {
                // تهيئة الاعتماديات
                val retrofitService = ApiService.provideRetrofitInstance()
                val mainRepository = NokatRepo(retrofitService, LocaleSource(context), PostDatabase.getInstance(context))

                // تهيئة ViewModel
                viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as android.app.Application)
                    .create(NokatViewModel::class.java)

                // تنفيذ العملية في نطاق كوروتين
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.refreshNokats()
                }

                Log.d("InstallReceiver", "Received broadcast")
            }
        }
    }
}





