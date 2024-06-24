package com.sarrawi.mynokat.repository

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.*
import androidx.room.withTransaction
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.*
import com.sarrawi.mynokat.paging.ImagePaging
import com.sarrawi.mynokat.paging.ImgPagingNew
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class NokatRepo constructor(val apiService: ApiService, private val localeSource: LocaleSource,val database:PostDatabase) {

    suspend fun getAllNokatSer() = apiService.getAllNokat()

//    fun getAllNokatSerpa(): LiveData<PagingData<NokatModel>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 12,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = { NokatPaging(apiService) }
//        ).liveData
//    }

    fun getNokatStream(): Flow<PagingData<NokatModel>> {
        val pagingSourceFactory = { database.nokatDao().getAllNokatsPaging() }

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getAllNokats(): LiveData<PagingData<NokatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                    enablePlaceholders =  false
            ),
            pagingSourceFactory = { database.nokatDao().getAllNokatsPaging() }
        ).liveData
    }

    fun getAllNewNokats(): LiveData<PagingData<NokatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders =  false
            ),
            pagingSourceFactory = { database.nokatDao().getAllNewNokat() }
        ).liveData
    }

    suspend fun refreshNokats() {

        val response = apiService.getAllNokatPa(1) // قم بضبط الصفحة حسب الحاجة
        if (response.isSuccessful) {
            response.body()?.results?.let { nokats ->
                database.withTransaction {
//                    database.nokatDao().clearAll()
                    database.nokatDao().insert_Nokat(nokats.NokatModel)
                }
            }

        } else {
            throw Exception("Error refreshing data")
        }
    }

    fun getAllNokat(): LiveData<PagingData<NokatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 12,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { database.nokatDao().getAllNokatsPaging() }
        ).liveData
    }

//    fun getAllImgsNokatSerPag():LiveData<PagingData<ItemModel>>{
    fun getAllImgsNokatSerPag():LiveData<PagingData<ImgsNokatModel>>{

            return  Pager(
                config = PagingConfig(pageSize = 12,
                    enablePlaceholders =  false
                ),

                pagingSourceFactory = { ImagePaging(apiService) }
            ).liveData
        }

    fun getAllImgsNokatSerPagNew():LiveData<PagingData<ImgsNokatModel>>{

        return  Pager(
            config = PagingConfig(pageSize = 12,
                enablePlaceholders =  false
            ),

            pagingSourceFactory = { ImgPagingNew(apiService) }
        ).liveData
    }

    fun getAllImgsNokatSerPa():Flow<PagingData<ImgsNokatModel>>{
        return  Pager(
            config = PagingConfig(pageSize = 12,
            enablePlaceholders =  false
            ),

        pagingSourceFactory = { ImagePaging(apiService) }
        ).flow
    }

    fun getNokatRepo(): PagingSource<Int,NokatModel> {
        return localeSource.getAllNokatsDao()
    }

    fun getNokatRepo2(): LiveData<PagingData<NokatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 12,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { localeSource.getAllNokatsDao() }
        ).liveData
    }

    suspend fun insertNokatRepo (nokat:List<NokatModel>?){
        if(!nokat.isNullOrEmpty()){
            localeSource.insert_Nokat(nokat)
        }
    }

    suspend fun insertNokatRepo2(nokat:NokatModel){

            localeSource.insert_Nokat2(nokat)

    }

    

    /////
    suspend fun update_fav(id: Int,state:Boolean) {

        localeSource.update_fav(id,state)
    }

    suspend fun add_fav(fav: FavNokatModel) {

        localeSource.add_fav(fav)
    }


    // delete favorite item from db
    suspend fun deleteFav(fav: FavNokatModel) {

        localeSource.delete_fav(fav)
    }


    fun getAllFav(): LiveData<PagingData<FavNokatModel>> {
        return Pager(
            config = PagingConfig(pageSize = 12, enablePlaceholders = false),
            pagingSourceFactory = { database.favNokatDao().getAllFav() }
        ).liveData
    }

    ////
    suspend fun update_favs(id: Int, state: Boolean) {
        localeSource.favoriteDao?.update_favs(id, state)
    }

    suspend fun add_favs(fav: FavNokatModel) {
        localeSource.favoriteDao?.add_fav(fav)
    }

    suspend fun delete_favs(fav: FavNokatModel) {
        localeSource.favoriteDao?.deletefav(fav)
    }

    fun getAllFavs(): LiveData<PagingData<FavNokatModel>> {
        return Pager(
            config = PagingConfig(pageSize = 12, enablePlaceholders = false),
            pagingSourceFactory = { localeSource.favoriteDao!!.getAllFav() }
        ).liveData
    }

    ///////////////////////
    suspend fun update_favs_img(id: Int, state: Boolean) {
        localeSource.favImageDao.update_favimg(id, state)
    }

    suspend fun add_favs_img(fav: FavImgModel) {
        localeSource.favImageDao.insertFavoriteImage(fav)
    }

    suspend fun delete_favs_img(fav: FavImgModel) {
        localeSource.favImageDao.deleteFavoriteImage(fav)
    }

    fun getAllFavImge ():LiveData<PagingData<FavImgModel>> {
        return Pager(
            config = PagingConfig(pageSize = 12, enablePlaceholders = false),
            pagingSourceFactory = { localeSource.favImageDao.getAllFavoritea() }
        ).liveData
    }
    fun getAllFavImg ():LiveData<List<FavImgModel>> {

        return localeSource.favImageDao.getAllFavoriteImages()

    }

}