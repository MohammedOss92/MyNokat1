package com.sarrawi.mynokat.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.repository.NokatRepo

class SharedViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

