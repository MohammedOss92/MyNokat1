package com.sarrawi.mynokat

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sarrawi.mynokat.viewModel.SharedViewModel
import com.sarrawi.mynokat.workmanager.FetchDataWorker

class MyApplication : Application() {
    lateinit var sharedViewModel: SharedViewModel
    lateinit var sharedViewModels: SharedViewModel

    override fun onCreate() {
        super.onCreate()

        // جدولة FetchDataWorker عند تثبيت التطبيق
        val fetchDataRequest = OneTimeWorkRequest.Builder(FetchDataWorker::class.java).build()

        WorkManager.getInstance(this).enqueue(fetchDataRequest)

        //يمكنك إنشاؤه في مستوى التطبيق لتشارك البيانات بين الأنشطة:
        val sharedViewModel: SharedViewModel by lazy {
            ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(SharedViewModel::class.java)
        }

//        sharedViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(SharedViewModel::class.java)


    }
}

/*package com.sarrawi.mynokat

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.sarrawi.mynokat.workmanager.FetchDataWorker
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // جدولة FetchDataWorker عند تثبيت التطبيق
        val fetchDataRequest = OneTimeWorkRequest.Builder(FetchDataWorker::class.java).build()
        WorkManager.getInstance(this).enqueue(fetchDataRequest)

        val periodicFetchDataRequest = PeriodicWorkRequest.Builder(FetchDataWorker::class.java, 1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "FetchDataWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicFetchDataRequest
        )
    }

}
*/
