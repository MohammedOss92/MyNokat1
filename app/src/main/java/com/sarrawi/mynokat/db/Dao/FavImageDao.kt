package com.sarrawi.mynokat.db.Dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.sarrawi.mynokat.model.FavImgModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FavImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteImage(favImgModel: FavImgModel)

    @Delete
    suspend fun deleteFavoriteImage(favImgModel: FavImgModel)

    @Query("SELECT * FROM fav_tb order by createdAt desc ")
//     fun getAllFavoriteImages(): PagingSource<Int,FavImgModel>
     fun getAllFavoriteImages(): LiveData<List<FavImgModel>>

    @Query("SELECT * FROM fav_tb order by createdAt desc ")
    fun getAllFavoriteImagesflow(): Flow<List<FavImgModel>>

    @Query("Update fav_tb SET is_fav = :state where id =:ID")
    suspend fun update_favimg(ID:Int,state:Boolean)

    @Query("SELECT * FROM fav_tb order by createdAt  desc ")
    fun getAllFavoritea(): PagingSource<Int,FavImgModel>
}