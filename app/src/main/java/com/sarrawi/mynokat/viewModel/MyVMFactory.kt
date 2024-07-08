package com.sarrawi.mynokat.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.repository.NokatRepo

class MyVMFactory constructor(private val repository: NokatRepo, val context: Context, val database: PostDatabase, private val ID_Type_id:Int): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NokatVM::class.java)) {
            return  NokatVM(this.repository,context,database,ID_Type_id) as T
        }else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}