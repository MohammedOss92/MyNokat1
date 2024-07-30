package com.sarrawi.mynokat.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.repository.NokatRepo

class SharedViewModelFactory constructor(val apiService: ApiService,private val repository: NokatRepo,val context: Context,val database:PostDatabase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(apiService,this.repository,context,database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

