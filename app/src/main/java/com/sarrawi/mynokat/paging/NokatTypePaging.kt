package com.sarrawi.mynokat.paging

import androidx.annotation.RequiresApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.model.NokatTypeModel
import retrofit2.HttpException
import java.io.IOException

class NokatTypePaging(private val apiService: ApiService,
                      private val database: PostDatabase
) : PagingSource<Int, NokatTypeModel>() {

    @RequiresApi(34)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NokatTypeModel> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getAllNokatTypes(page)
            if (response.isSuccessful) {
                val nokatList = response.body()?.results?.NokatTypeModel ?: emptyList()



                LoadResult.Page(
                    data = nokatList,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (nokatList.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("Error loading data. Response: ${response.code()}, ${response.message()}"))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }



    override fun getRefreshKey(state: PagingState<Int, NokatTypeModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}