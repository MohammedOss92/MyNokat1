package com.sarrawi.mynokat.db.Dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.sarrawi.mynokat.model.FavImgModel

@Dao
interface FavImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteImage(favImgModel: FavImgModel)

    @Delete
    suspend fun deleteFavoriteImage(favImgModel: FavImgModel)

    @Query("SELECT * FROM fav_tb order by createdAt desc ")
     fun getAllFavoriteImages(): PagingSource<Int,FavImgModel>

    @Query("Update fav_tb SET is_fav = :state where id =:ID")
    suspend fun update_favimg(ID:Int,state:Boolean)

    @Query("SELECT * FROM fav_tb order by createdAt  desc ")
    fun getAllFavoritea(): PagingSource<Int,FavImgModel>
}