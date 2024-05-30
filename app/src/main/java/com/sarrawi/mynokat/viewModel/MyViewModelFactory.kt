package com.sarrawi.mynokat.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sarrawi.mynokat.db.PostDatabase

import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.ui.MainActivity

class MyViewModelFactory constructor(private val repository: NokatRepo,val context: Context,val database:PostDatabase): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
         if (modelClass.isAssignableFrom(NokatViewModel::class.java)) {
             return  NokatViewModel(this.repository,context,database) as T
        }else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}