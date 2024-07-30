package com.sarrawi.mynokat.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.paging.ImagePaging
import kotlinx.coroutines.flow.Flow

class SharedViewModel(private val apiService: ApiService) : ViewModel() {

    //بهذا الشكل، سيتم تحديث SharedViewModel بالبيانات عند تحميلها بنجاح، وسيتمكن Fragment الآخر من الوصول إلى البيانات المحملة دون الحاجة إلى إعادة تحميلها.
    private val _pagingData = MutableLiveData<PagingData<ImgsNokatModel>>()
    val pagingData: LiveData<PagingData<ImgsNokatModel>> get() = _pagingData

    fun updatePagingData(newPagingData: PagingData<ImgsNokatModel>) {
        _pagingData.value = newPagingData
    }

    val pagingDataFlow: Flow<PagingData<ImgsNokatModel>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { ImagePaging(apiService) }
    ).flow.cachedIn(viewModelScope)




    }




