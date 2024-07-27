package com.sarrawi.mynokat.paging

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.model.ItemModel

class ImagePaging(
    private val apiService: ApiService,

) : PagingSource<Int, ImgsNokatModel>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ImgsNokatModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImgsNokatModel> {
        return try {
            val currentPage = params.key ?: STARTING_PAGE_INDEX
            val response = apiService.getAllImgNokatPa(currentPage)
            if (response.isSuccessful) {
                val data = response.body()?.results?.ImgsNokatModel ?: emptyList()

                // إضافة تتبع المعرّفات
                Log.d("PagingSource", "Page $currentPage, Loaded Image IDs: ${data.map { it.id }}")

                LoadResult.Page(
                    data = data,
                    prevKey = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1,
                    nextKey = if (data.isEmpty()) null else currentPage + 1
                )
            } else {
                LoadResult.Error(Exception("Error loading data. Response: ${response.code()}, ${response.message()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }



}
