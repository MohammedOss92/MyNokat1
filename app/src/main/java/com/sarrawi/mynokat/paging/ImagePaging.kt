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
    private val countLiveData: MutableLiveData<Int>
) : PagingSource<Int, ImgsNokatModel>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    private var isLoading = false

    override fun getRefreshKey(state: PagingState<Int, ImgsNokatModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImgsNokatModel> {
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
            Log.i("ImageNokatPaging", "load: ${response.body()}")
            if (response.isSuccessful) {
                val data = response.body()?.results?.ImgsNokatModel ?: emptyList()
                val count = response.body()?.count ?: 0  // الحصول على العدد الإجمالي للصور من الرد

                // تحديث LiveData بالعدد الإجمالي للصور
                countLiveData.postValue(count)

                // تحويل البيانات إلى ItemModel إذا كنت بحاجة لذلك
                val itemModels = data.map { ItemModel.ImgsItem(it) }

                Log.d("ImageNokatPaging", "Loaded data: $itemModels")
                Log.d("ImageNokatPaging", "Total count: $count")  // طباعة العدد الإجمالي للصور

                return LoadResult.Page(
                    data = data,
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
