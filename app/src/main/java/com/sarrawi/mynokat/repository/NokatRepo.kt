package com.sarrawi.mynokat.repository

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import androidx.room.withTransaction
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.*
import com.sarrawi.mynokat.paging.ImagePaging
import com.sarrawi.mynokat.paging.ImgPagingNew
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class NokatRepo constructor(val apiService: ApiService, private val localeSource: LocaleSource,val database:PostDatabase) {

    suspend fun getAllNokatSer() = apiService.getAllNokat()
    suspend fun getAllNokatTypeSer() = apiService.getAllNokat()
    private val _countLiveDataa = MutableLiveData<Int>()
    val countLiveDataa: LiveData<Int> get() = _countLiveDataa

    val countLiveDataNokat: LiveData<Int> get() = localeSource.countLiveDataNokat

    suspend fun  fcount()=apiService.ImgCount()

    suspend fun getImgCount() {
        try {
            val response = withContext(Dispatchers.IO) { apiService.ImgCount() }
            if (response.isSuccessful) {
                _countLiveDataa.postValue(response.body()?.total_images)
            } else {
                _countLiveDataa.postValue(0)
            }
        } catch (e: Exception) {
            _countLiveDataa.postValue(0)
        }
    }

//    fun getAllNokatSerpa(): LiveData<PagingData<NokatModel>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 12,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = { NokatPaging(apiService) }
//        ).liveData
//    }
private val ID_Type_id=0
    fun getNokatStream(ID_Type_id:Int): Flow<PagingData<NokatModel>> {
        val pagingSourceFactory = { database.nokatDao().getAllNokatsPaging(ID_Type_id) }

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getAllNokats(ID_Type_id:Int): LiveData<PagingData<NokatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                    enablePlaceholders =  false
            ),
            pagingSourceFactory = { database.nokatDao().getAllNokatsPaging(ID_Type_id) }
        ).liveData
    }

    fun getAllNokatsTypes(): LiveData<PagingData<NokatTypeWithCount>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders =  false
            ),
            pagingSourceFactory = { database.nokatTypesDao().getAllNokatTypesWithCounts() }
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



    fun getAllNokat(): LiveData<PagingData<NokatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 12,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { database.nokatDao().getAllNokatsPaging(ID_Type_id) }
        ).liveData
    }


    private val _countLiveData = MutableLiveData<Int>()
    val countLiveData: LiveData<Int> get() = _countLiveData
//    fun getAllImgsNokatSerPag():LiveData<PagingData<ItemModel>>{
    fun getAllImgsNokatSerPag():LiveData<PagingData<ImgsNokatModel>>{

            return  Pager(
                config = PagingConfig(pageSize = 12,
                    enablePlaceholders =  false
                ),

                pagingSourceFactory = { ImagePaging(apiService,_countLiveData) }
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

        pagingSourceFactory = { ImagePaging(apiService,_countLiveData) }
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