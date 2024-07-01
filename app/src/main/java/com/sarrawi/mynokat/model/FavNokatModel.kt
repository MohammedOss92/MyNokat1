package com.sarrawi.mynokat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName ="FavNokat_tb")
data class FavNokatModel (

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int,


    @ColumnInfo("new_nokat")
    @SerializedName("new_nokat")
    val new_nokat: Int,

    @ColumnInfo("NokatName")
    @SerializedName("NokatName")
    val NokatName: String,

    @ColumnInfo(name = "createdAt")
    var createdAt: String? = null
        )
