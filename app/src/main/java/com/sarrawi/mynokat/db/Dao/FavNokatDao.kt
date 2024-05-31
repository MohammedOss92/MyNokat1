package com.sarrawi.mynokat.db.Dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.room.*
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.model.NokatModel

@Dao
interface FavNokatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add_fav(fav: FavNokatModel)

    //    @Query("Select * from Favorite_table")

    @Query("SELECT * FROM FavNokat_tb ORDER BY createdAt DESC")
    fun getAllFav(): PagingSource<Int, FavNokatModel>



    // delete favorite item from db
    @Delete
    suspend fun deletefav(item:FavNokatModel)

    //////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add_favs(fav: FavNokatModel)

    @Query("SELECT * FROM FavNokat_tb ORDER BY createdAt DESC")
    fun getAllFavs(): PagingSource<Int, FavNokatModel>

    @Delete
    suspend fun deletefavs(item: FavNokatModel)

    @Query("UPDATE Nokat_tb SET is_fav = :state WHERE id = :ID")
    suspend fun update_favs(ID: Int, state: Boolean)
}