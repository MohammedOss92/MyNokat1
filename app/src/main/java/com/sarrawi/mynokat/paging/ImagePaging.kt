package com.sarrawi.mynokat.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.model.ImgsNokatModel

class ImagePaging(private val apiService: ApiService) : PagingSource<Int, ImgsNokatModel>() {

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
        val currentPage = params.key ?: STARTING_PAGE_INDEX

        return try {
            Log.d("ImagePaging", "Loading page $currentPage")

            // تحميل البيانات من API
            val response = apiService.getAllImgNokatPa(currentPage)
            if (response.isSuccessful) {
                val data = response.body()?.results?.ImgsNokatModel ?: emptyList()

                // تحديد المفاتيح للصفحات السابقة والتالية
                val prevKey = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1
                val nextKey = if (data.isEmpty()) null else currentPage + 1

                LoadResult.Page(
                    data = data,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else {
                Log.e("ImagePaging", "Error loading data: ${response.code()} - ${response.message()}")
                LoadResult.Error(Exception("Error loading data: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("ImagePaging", "Exception during data loading: $e")
            LoadResult.Error(e)
        }
    }
}
