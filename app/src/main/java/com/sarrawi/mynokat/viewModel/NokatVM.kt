package com.sarrawi.mynokat.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.model.NokatTypeModel
import com.sarrawi.mynokat.model.NokatTypeWithCount
import com.sarrawi.mynokat.paging.NokatPaging
import com.sarrawi.mynokat.paging.NokatTypePaging
import com.sarrawi.mynokat.repository.NokatRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class NokatVM constructor(private val nokatRepo: NokatRepo, val context: Context, val database: PostDatabase,private val ID_Type_id:Int): ViewModel() {

    private val retrofitService = ApiService.provideRetrofitInstance()

    suspend fun refreshNokatsType(apiService: ApiService, database: PostDatabase) {
        var page = 1 // البدء بالصفحة الأولى
        var nokatTypeList: List<NokatTypeModel>

        try {
            do {
                val response = apiService.getAllNokatTypes(page)
                if (response.isSuccessful) {
                    nokatTypeList = response.body()?.results?.NokatTypeModel ?: emptyList()
                    database.nokatTypesDao().insert_Nokat_type(nokatTypeList)
                    page++
                    for (nokatType in nokatTypeList) {
                        refreshNokatswithID(apiService, database, nokatType.id)
                    }
                } else {
                    nokatTypeList = emptyList() // تعيين القائمة كفارغة لإيقاف التكرار
                }
            } while (nokatTypeList.isNotEmpty())
        } catch (e: IOException) {
            throw IOException("Network error", e)
        } catch (e: HttpException) {
            // يمكنك هنا تسجيل الأخطاء أو معالجتها إذا لزم الأمر
        }
    }

    suspend fun refreshNokatswithID(apiService: ApiService, database: PostDatabase, ID_Type_id: Int) {
        var page = 1 // البدء بالصفحة الأولى
        var nokatList: List<NokatModel>

        try {
            do {
                val response = apiService.getAllNokatbyID(ID_Type_id, page)
                if (response.isSuccessful) {
                    nokatList = response.body()?.results?.NokatModel ?: emptyList()
                    database.nokatDao().insert_Nokat(nokatList)
                    page++
                } else {
                    nokatList = emptyList() // تعيين القائمة كفارغة لإيقاف التكرار
                }
            } while (nokatList.isNotEmpty())
        } catch (e: IOException) {
            throw IOException("Network error", e)
        } catch (e: HttpException) {
            // يمكنك هنا تسجيل الأخطاء أو معالجتها إذا لزم الأمر
        }
    }



    private val pagingSourceFlowTypes = MutableStateFlow(Unit)

    val nokatTypesFlow: Flow<PagingData<NokatTypeModel>> = pagingSourceFlowTypes.flatMapLatest {
        Pager(
            config = PagingConfig(pageSize = 12, enablePlaceholders = false),
            pagingSourceFactory = { NokatTypePaging(ApiService.provideRetrofitInstance(), database) }
        ).flow
            .cachedIn(viewModelScope)
    }

    fun invalidatePagingSourceTypes() {
        pagingSourceFlowTypes.value = Unit
    }

    private val pagingSourceFlow = MutableStateFlow(Unit)

    val nokatFlow: Flow<PagingData<NokatModel>> = pagingSourceFlow.flatMapLatest {
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { NokatPaging(ApiService.provideRetrofitInstance(), database,ID_Type_id) }
        ).flow
            .cachedIn(viewModelScope)
    }

    fun invalidatePagingSource() {
        pagingSourceFlow.value = Unit
    }

    val items: LiveData<PagingData<NokatModel>> = nokatRepo.getAllNokat()
        .cachedIn(viewModelScope)
    val itemss: LiveData<PagingData<NokatModel>> = nokatRepo.getAllNokats(ID_Type_id).cachedIn(viewModelScope)
    val newNokat: LiveData<PagingData<NokatModel>> = nokatRepo.getAllNewNokats().cachedIn(viewModelScope)
    val nokatType: LiveData<PagingData<NokatTypeWithCount>> = nokatRepo.getAllNokatsTypes().cachedIn(viewModelScope)




    val favNokats: LiveData<PagingData<FavNokatModel>> = nokatRepo.getAllFav().cachedIn(viewModelScope)

    fun update_favs(id: Int, state: Boolean) = viewModelScope.launch {
        nokatRepo.update_favs(id, state)
    }

    fun add_favs(fav: FavNokatModel) = viewModelScope.launch {
        nokatRepo.add_favs(fav)
    }

    fun delete_favs(fav: FavNokatModel) = viewModelScope.launch {
        nokatRepo.delete_favs(fav)
    }
    val favNokat: LiveData<PagingData<FavNokatModel>> = nokatRepo.getAllFav().cachedIn(viewModelScope)

    fun update_fav(id:Int,state:Boolean)=viewModelScope.launch {
        nokatRepo.update_fav(id,state)
    }
}