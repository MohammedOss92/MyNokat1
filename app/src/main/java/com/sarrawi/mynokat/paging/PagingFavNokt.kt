package com.sarrawi.mynokat.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavNokatModel

class PagingFavNokt(private val database: PostDatabase) : PagingSource<Int, FavNokatModel>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, FavNokatModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FavNokatModel> {
        return try {
            val currentPage = params.key ?: STARTING_PAGE_INDEX
            val pageSize = params.loadSize

            Log.d("PagingFavNokt", "Loading page $currentPage with pageSize $pageSize")
            val data = database.favNokatDao().getAllFav() // هذه تعيد PagingSource

            // عليك التأكد من أن طريقة `load` في PagingSource يتم تنفيذها للحصول على البيانات الفعلية.
            val result = data.load(
                PagingSource.LoadParams.Refresh(
                    key = currentPage,
                    loadSize = pageSize,
                    placeholdersEnabled = false
                )
            )

            val favNokatModels = when (result) {
                is PagingSource.LoadResult.Page -> result.data
                is PagingSource.LoadResult.Error -> throw result.throwable
                else -> emptyList()
            }

            LoadResult.Page(
                data = favNokatModels,
                prevKey = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1,
                nextKey = if (favNokatModels.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            Log.e("PagingFavNokt", "Error loading data", e)
            LoadResult.Error(e)
        }
    }
}
