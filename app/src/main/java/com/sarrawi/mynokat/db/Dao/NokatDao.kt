package com.sarrawi.mynokat.db.Dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarrawi.mynokat.model.NokatModel


@Dao
interface NokatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert_Nokat(nokatModel: List<NokatModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert_Nokat2(nokatModel: NokatModel)

    @Query("DELETE FROM Nokat_tb")
    suspend fun clearAll()


    @Query("select * from Nokat_tb order by id desc")
     fun getAllNokatsDao(): PagingSource<Int, NokatModel>

    @Query("SELECT * FROM Nokat_tb ORDER BY id desc")
    fun getAllNokatsPaging(): PagingSource<Int, NokatModel>

    @Query("SELECT * FROM Nokat_tb where new_nokat=1 ORDER BY id desc")
    fun getAllNewNokat(): PagingSource<Int, NokatModel>

    @Query("Update Nokat_tb SET is_fav = :state where id =:ID")
    suspend fun update_fav(ID:Int,state:Boolean)

//    @Query("SELECT e.*, c.MsgTypes AS typeTitle " +
//            "FROM msg_table e " +
//            "LEFT JOIN msg_types_table c ON c.id = e.ID_Type_id " +
//            "WHERE e.new_msgs = 1 " +
//            "ORDER BY e.id DESC")
//    fun getAllNewMsg(): LiveData<List<MsgModelWithTitle>>


//    @Query("delete from msg_table")
//    fun deleteAllmessage()
//
//    // update msg_table items favorite state
//    @Query("Update msg_table SET is_fav = :state where id =:ID")
//    suspend fun update_fav(ID:Int,state:Boolean)
}