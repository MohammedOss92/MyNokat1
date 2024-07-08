package com.sarrawi.mynokat.paging

import androidx.annotation.RequiresApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.NokatModel
import retrofit2.HttpException
import java.io.IOException

class NokatPaging(
    private val apiService: ApiService,
    private val database: PostDatabase,
    private val ID_Type_id:Int,
) : PagingSource<Int, NokatModel>() {
    @RequiresApi(34)
    override suspend fun load(params: LoadParams<Int> ): LoadResult<Int, NokatModel> {
        val page = params.key ?: 1

        return try {
            val response = apiService.getAllNokatbyID(ID_Type_id,page)
            if (response.isSuccessful) {
                val nokatList = response.body()?.results?.NokatModel ?: emptyList()

                // إدخال البيانات في قاعدة البيانات
//                database.withTransaction {
////                    if (page == 1) {
////                        database.nokatDao().clearAll()
////                    }
//                    database.nokatDao().insert_Nokat(nokatList)
//                }

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



    override fun getRefreshKey(state: PagingState<Int, NokatModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}