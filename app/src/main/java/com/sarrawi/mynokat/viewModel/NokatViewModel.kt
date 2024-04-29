package com.sarrawi.mynokat.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.ui.MainActivity
import kotlinx.coroutines.launch

class NokatViewModel constructor(private val nokatRepo: NokatRepo,val context: Context):ViewModel() {


    private val retrofitService = ApiService.provideRetrofitInstance()

    private val _response = MutableLiveData<List<NokatModel>>()
    val responseNokat: LiveData<List<NokatModel>>
        get() = _response


    fun getAllNokat(): LiveData<PagingData<NokatModel>> {

        var _response = MutableLiveData<PagingData<NokatModel>>()
        viewModelScope.launch {
            try {
                val response = nokatRepo.getAllNokatSerpa()
                _response = response as MutableLiveData<PagingData<NokatModel>>
            } catch (e: Exception) {
                Log.e("Test", "getAllNokat: Error: ${e.message}")
            }
        }

        return _response
    }


    fun getAllImage(): LiveData<PagingData<ImgsNokatModel>> {

        var _response = MutableLiveData<PagingData<ImgsNokatModel>>()
        viewModelScope.launch {
            try {
                val response = nokatRepo.getAllImgsNokatSerPa()
                _response = response as MutableLiveData<PagingData<ImgsNokatModel>>
            } catch (e: Exception) {
                Log.e("Test", "getAllNokat: Error: ${e.message}")
            }
        }

        return _response
    }

    fun getAllNokat2(): LiveData<PagingData<NokatModel>> {
        val _response = MutableLiveData<PagingData<NokatModel>>()
        viewModelScope.launch {
            try {
                val responseLiveData = nokatRepo.getAllNokatSerpa()
                responseLiveData.observeForever { response ->
                    _response.value = response
                }
            } catch (e: Exception) {
                Log.e("Test", "getAllNokat: Error: ${e.message}")
            }
        }
        return _response
    }

}
