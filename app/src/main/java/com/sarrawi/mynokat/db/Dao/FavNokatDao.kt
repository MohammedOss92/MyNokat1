package com.sarrawi.mynokat.db.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sarrawi.mynokat.model.FavNokatModel

@Dao
interface FavNokatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add_fav(fav: FavNokatModel)

    //    @Query("Select * from Favorite_table")
    @Query("select * from FavNokat_tb order by createdAt DESC")
    fun getAllFav(): LiveData<List<FavNokatModel>>

    // delete favorite item from db
    @Delete
    suspend fun deletefav(item:FavNokatModel)
}