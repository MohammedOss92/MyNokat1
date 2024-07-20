package com.sarrawi.mynokat

import android.app.Application
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sarrawi.mynokat.workmanager.FetchDataWorker

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // جدولة FetchDataWorker عند تثبيت التطبيق
        val fetchDataRequest = OneTimeWorkRequest.Builder(FetchDataWorker::class.java).build()
        WorkManager.getInstance(this).enqueue(fetchDataRequest)
    }
}
