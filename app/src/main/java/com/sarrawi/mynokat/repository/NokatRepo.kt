package com.sarrawi.mynokat.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.*
import androidx.room.withTransaction
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.paging.ImagePaging
import com.sarrawi.mynokat.paging.NokatPaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class NokatRepo constructor(val apiService: ApiService, private val localeSource: LocaleSource,val database:PostDatabase) {

    suspend fun getAllNokatSer() = apiService.getAllNokat()

//    fun getAllNokatSerpa(): LiveData<PagingData<NokatModel>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 12,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = { NokatPaging(apiService) }
//        ).liveData
//    }

    fun getNokatStream(): Flow<PagingData<NokatModel>> {
        val pagingSourceFactory = { database.nokatDao().getAllNokatsPaging() }

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getAllNokats(): Flow<PagingData<NokatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                    enablePlaceholders =  false
            ),
            pagingSourceFactory = { database.nokatDao().getAllNokatsPaging() }
        ).flow
    }

    suspend fun refreshNokats() {
        val response = apiService.getAllNokatPa(1) // قم بضبط الصفحة حسب الحاجة
        if (response.isSuccessful) {
            response.body()?.results?.let { nokats ->
                database.withTransaction {
                    database.nokatDao().clearAll()
                    database.nokatDao().insert_Nokat(nokats.NokatModel)
                }
            }
        } else {
            throw Exception("Error refreshing data")
        }
    }

    fun getAllNokat(): LiveData<PagingData<NokatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 12,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { database.nokatDao().getAllNokatsPaging() }
        ).liveData
    }

    fun getAllImgsNokatSerPag():LiveData<PagingData<ImgsNokatModel>>{

            return  Pager(
                config = PagingConfig(pageSize = 12,
                    enablePlaceholders =  false
                ),

                pagingSourceFactory = { ImagePaging(apiService) }
            ).liveData
        }
    fun getAllImgsNokatSerPa():Flow<PagingData<ImgsNokatModel>>{
        return  Pager(
            config = PagingConfig(pageSize = 12,
            enablePlaceholders =  false
            ),

        pagingSourceFactory = { ImagePaging(apiService) }
        ).flow
    }

    fun getNokatRepo(): PagingSource<Int,NokatModel> {
        return localeSource.getAllNokatsDao()
    }

    fun getNokatRepo2(): LiveData<PagingData<NokatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 12,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { localeSource.getAllNokatsDao() }
        ).liveData
    }

    suspend fun insertNokatRepo (nokat:List<NokatModel>?){
        if(!nokat.isNullOrEmpty()){
            localeSource.insert_Nokat(nokat)
        }
    }

    suspend fun insertNokatRepo2(nokat:NokatModel){

            localeSource.insert_Nokat2(nokat)

    }

    





}