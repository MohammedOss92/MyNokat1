package com.sarrawi.mynokat.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.model.ItemModel

class ImagePaging(private val apiService: ApiService) : PagingSource<Int, ItemModel>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    private var isLoading = false

    override fun getRefreshKey(state: PagingState<Int, ItemModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemModel> {
        if (isLoading) {
            // تجنب إعادة استدعاء load عندما تكون العملية قيد التحميل
            return LoadResult.Error(Exception("Loading is already in progress"))
        }

        try {
            isLoading = true

            val currentPage = params.key ?: STARTING_PAGE_INDEX
            val pageSize = params.loadSize

            Log.d("ImageNokatPaging", "Loading page $currentPage with pageSize $pageSize")
            val response = apiService.getAllImgNokatPa(currentPage)
            Log.i("hahahahahaha", "load: ${response.body()}")
            if (response.isSuccessful) {
                val data = response.body()?.results?.ImgsNokatModel ?: emptyList()

                // تحويل البيانات إلى ItemModel
                val itemModels = data.map { ItemModel.ImgsItem(it) }

                Log.d("ImageNokatPaging", "Loaded data: $itemModels")

                return LoadResult.Page(
                    data = itemModels,
                    prevKey = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1,
                    nextKey = if (data.isEmpty()) null else currentPage + 1
                )
            } else {
                Log.e("ImageNokatPaging", "Error loading data. Response: ${response.code()}, ${response.message()}")
                return LoadResult.Error(Exception("Error loading data. Response: ${response.code()}, ${response.message()}"))
            }

        } catch (e: Exception) {
            Log.e("ImageNokatPaging", "Exception during data loading: $e")
            Log.e("ImageNokatPaging", "Error loading data. Exception: ${e.message}")

            return LoadResult.Error(e)
        } finally {
            isLoading = false
        }
    }
}
