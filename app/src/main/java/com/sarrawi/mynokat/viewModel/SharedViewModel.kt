package com.sarrawi.mynokat.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.paging.ImagePaging
import com.sarrawi.mynokat.repository.NokatRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SharedViewModel constructor(private val apiService: ApiService, private val nokatRepo: NokatRepo, val context: Context,val database: PostDatabase,) : ViewModel() {

    private val retrofitService = ApiService.provideRetrofitInstance()


    //بهذا الشكل، سيتم تحديث SharedViewModel بالبيانات عند تحميلها بنجاح، وسيتمكن Fragment الآخر من الوصول إلى البيانات المحملة دون الحاجة إلى إعادة تحميلها.
    private val _pagingData = MutableLiveData<PagingData<ImgsNokatModel>>()
    val pagingData: LiveData<PagingData<ImgsNokatModel>> get() = _pagingData

    fun updatePagingData(newPagingData: PagingData<ImgsNokatModel>) {
        _pagingData.value = newPagingData
    }

    val pagingDataFlow: Flow<PagingData<ImgsNokatModel>> = Pager(
        config = PagingConfig(pageSize = 12, enablePlaceholders = false),
        pagingSourceFactory = { ImagePaging(apiService) }
    ).flow.cachedIn(viewModelScope)


    fun getAllImage(): LiveData<PagingData<ImgsNokatModel>> {

        var _response = MutableLiveData<PagingData<ImgsNokatModel>>()
        viewModelScope.launch {
            try {
                val response = nokatRepo.getAllImgsNokatSerPag()
                _response = response as MutableLiveData<PagingData<ImgsNokatModel>>
            } catch (e: Exception) {
                Log.e("Test", "getAllNokat: Error: ${e.message}")
            }
        }

        return _response
    }

    fun update_favs_img(id: Int, state: Boolean) = viewModelScope.launch {
        nokatRepo.update_favs_img(id, state)
    }

    fun add_favs_img(fav: FavImgModel) = viewModelScope.launch {
        nokatRepo.add_favs_img(fav)
    }

    fun delete_favs_img(fav: FavImgModel) = viewModelScope.launch {
        nokatRepo.delete_favs_img(fav)
    }

    val favImg: LiveData<List<FavImgModel>> = nokatRepo.getAllFavImg()
    val favImgFlow: Flow<List<FavImgModel>> = nokatRepo.getAllFavImgflow()
    }




