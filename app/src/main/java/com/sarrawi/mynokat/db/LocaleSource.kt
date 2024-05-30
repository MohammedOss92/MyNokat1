package com.sarrawi.mynokat.db

import android.content.Context
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.sarrawi.mynokat.db.Dao.NokatDao
import com.sarrawi.mynokat.model.NokatModel
import kotlinx.coroutines.flow.collectLatest

class LocaleSource(context: Context) {

//    private var nokatDao:NokatDao?
    private val database = PostDatabase.getInstance(context)
    var nokatDao = database.nokatDao()
    init {
        val dataBase = PostDatabase.getInstance(context.applicationContext)
        nokatDao = dataBase.nokatDao()
    }

    companion object {
        private var sourceConcreteClass: LocaleSource? = null
        fun getInstance(context: Context): LocaleSource {
            if (sourceConcreteClass == null)
                sourceConcreteClass = LocaleSource(context)
            return sourceConcreteClass as LocaleSource
        }
    }


    suspend fun insert_Nokat(nokatModel: List<NokatModel>){
        return nokatDao.insert_Nokat(nokatModel)
    }

    suspend fun insert_Nokat2(nokatModel: NokatModel){
        return nokatDao.insert_Nokat2(nokatModel)
    }



//    suspend fun getAllNokatsDao(): List<NokatModel>{
//        return nokatDao?.getAllNokatsDao()!!
//    }

    fun getAllNokatsDao(): PagingSource<Int, NokatModel> {
        return nokatDao.getAllNokatsPaging()
    }



}