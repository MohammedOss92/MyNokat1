package com.sarrawi.mynokat.db.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.model.NokatTypeModel
import com.sarrawi.mynokat.model.NokatTypeWithCount

@Dao
interface NokatTypeDao {


    @Query(
        "SELECT c.*, " +
                "COUNT(e.ID_Type) AS subCount, " +
                "SUM(CASE WHEN e.new_nokat = 1 THEN 1 ELSE 0 END) AS newNokatCount " +
                "FROM NokatType_tb c " +
                "LEFT JOIN Nokat_tb e ON c.id = e.ID_Type " +
                "GROUP BY c.id"
    )
    fun getAllNokatTypesWithCounts(): PagingSource<Int,NokatTypeWithCount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert_Nokat_type(nokatModel: List<NokatTypeModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert_Nokat2_type(nokatModel: NokatTypeModel)

    @Query("DELETE FROM Nokat_tb")
    suspend fun clearAll()
}