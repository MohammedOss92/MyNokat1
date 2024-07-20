package com.sarrawi.mynokat.db

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarrawi.mynokat.db.Dao.FavImageDao
import com.sarrawi.mynokat.db.Dao.FavNokatDao
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.model.NokatModel

class LocaleSource(context: Context) {

//    private var nokatDao:NokatDao?
    private val database = PostDatabase.getInstance(context)
    var nokatDao = database.nokatDao()
    var favoriteDao: FavNokatDao?
    var favImageDao:FavImageDao

    init {
        val dataBase = PostDatabase.getInstance(context.applicationContext)
        nokatDao = dataBase.nokatDao()
        favoriteDao = dataBase.favNokatDao()
        favImageDao = dataBase.favImageDao()
    }

    companion object {
        private var sourceConcreteClass: LocaleSource? = null
        fun getInstance(context: Context): LocaleSource {
            if (sourceConcreteClass == null)
                sourceConcreteClass = LocaleSource(context)
            return sourceConcreteClass as LocaleSource
        }
    }

    val countLiveDataNokat: LiveData<Int> get() = nokatDao.getNokatCount()


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
private val ID_Type_id=0
    fun getAllNokatsDao(): PagingSource<Int, NokatModel> {
        return nokatDao.getAllNokatsPaging(ID_Type_id)
    }

    fun getAllNewNokatsDao(): PagingSource<Int, NokatModel> {
        return nokatDao.getAllNewNokat()
    }
/////////////////
    suspend fun insertFavoriteImage(favImgModel: FavImgModel){
        return favImageDao.insertFavoriteImage(favImgModel)
    }

    suspend fun deleteFavoriteImage(favImgModel: FavImgModel){
        return favImageDao.deleteFavoriteImage(favImgModel)
    }


//    suspend fun getAllFavoriteImages(): PagingSource<Int, FavImgModel>{
    suspend fun getAllFavoriteImages(): LiveData<List< FavImgModel>>{
        return favImageDao.getAllFavoriteImages()
    }

    suspend fun getAllFavoriteImagesa(): PagingSource<Int, FavImgModel>{
        return favImageDao.getAllFavoritea()
    }

    suspend fun update_favimg(ID:Int,state:Boolean){
        return favImageDao.update_favimg(ID,state)
    }



}