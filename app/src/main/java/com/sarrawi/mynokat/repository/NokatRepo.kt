package com.sarrawi.mynokat.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.paging.NokatPaging

class NokatRepo constructor(private val apiService: ApiService) {

    suspend fun getAllNokatSer() = apiService.getAllNokat()

    suspend fun getAllNokatSerpa(): LiveData<PagingData<NokatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 12,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NokatPaging(apiService) }
        ).liveData
    }



}