package com.sarrawi.mynokat.db

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.sarrawi.mynokat.db.Dao.FavNokatDao
import com.sarrawi.mynokat.db.Dao.NokatDao
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.model.NokatModel
import kotlinx.coroutines.flow.collectLatest

class LocaleSource(context: Context) {

//    private var nokatDao:NokatDao?
    private val database = PostDatabase.getInstance(context)
    var nokatDao = database.nokatDao()
    var favoriteDao: FavNokatDao?
    init {
        val dataBase = PostDatabase.getInstance(context.applicationContext)
        nokatDao = dataBase.nokatDao()
        favoriteDao = dataBase.favNokatDao()
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


    suspend fun update_fav(id: Int, state:Boolean){
        return nokatDao.update_fav(id,state)
    }

    suspend fun add_fav(fav: FavNokatModel){
        favoriteDao?.add_fav(fav)
    }

    fun getAllFav(): PagingSource<Int, FavNokatModel> {
        Log.e("tessst","entred666")
        return favoriteDao?.getAllFav()!!
    }

    // delete favorite item from db
    suspend fun delete_fav(fav:FavNokatModel) {
        favoriteDao?.deletefav(fav)!!
    }

//    suspend fun getAllNokatsDao(): List<NokatModel>{
//        return nokatDao?.getAllNokatsDao()!!
//    }

    fun getAllNokatsDao(): PagingSource<Int, NokatModel> {
        return nokatDao.getAllNokatsPaging()
    }



}