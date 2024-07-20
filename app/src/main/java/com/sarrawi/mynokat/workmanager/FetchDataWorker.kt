package com.sarrawi.mynokat.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.model.NokatRespone
import com.sarrawi.mynokat.model.NokatTypeModel
import com.sarrawi.mynokat.model.NokatTypeResponse
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class FetchDataWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val apiService = ApiService.provideRetrofitInstance()
        val database = PostDatabase.getInstance(applicationContext)

        return try {
            fetchData(apiService, database)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun fetchData(apiService: ApiService, database: PostDatabase) {
        var page = 1
        var nokatTypeList: List<NokatTypeModel>

        try {
            do {
                val response: Response<NokatTypeResponse> = apiService.getAllNokatTypes(page)
                if (response.isSuccessful) {
                    nokatTypeList = response.body()?.results?.NokatTypeModel ?: emptyList()
                    database.nokatTypesDao().insert_Nokat_type(nokatTypeList)
                    page++
                    for (nokatType in nokatTypeList) {
                        refreshNokatswithID(apiService, database, nokatType.id)
                    }
                } else {
                    nokatTypeList = emptyList()
                }
            } while (nokatTypeList.isNotEmpty())
        } catch (e: IOException) {
            throw IOException("Network error", e)
        } catch (e: HttpException) {
            // معالجة الأخطاء أو تسجيلها
        }
    }

    private suspend fun refreshNokatswithID(apiService: ApiService, database: PostDatabase, ID_Type_id: Int) {
        var page = 1
        var nokatList: List<NokatModel>

        try {
            do {
                val response: Response<NokatRespone> = apiService.getAllNokatbyID(ID_Type_id, page)
                if (response.isSuccessful) {
                    nokatList = response.body()?.results?.NokatModel ?: emptyList()
                    database.nokatDao().insert_Nokat(nokatList)
                    page++
                } else {
                    nokatList = emptyList()
                }
            } while (nokatList.isNotEmpty())
        } catch (e: IOException) {
            throw IOException("Network error", e)
        } catch (e: HttpException) {
            // معالجة الأخطاء أو تسجيلها
        }
    }
}

/*class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // جدولة FetchDataWorker عند تثبيت التطبيق
        val fetchDataRequest = OneTimeWorkRequest.Builder(FetchDataWorker::class.java).build()
        WorkManager.getInstance(this).enqueue(fetchDataRequest)

        // جدولة FetchDataWorker بشكل دوري
        val periodicFetchDataRequest = PeriodicWorkRequest.Builder(FetchDataWorker::class.java, 1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "FetchDataWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicFetchDataRequest
        )
    }
}
*/
