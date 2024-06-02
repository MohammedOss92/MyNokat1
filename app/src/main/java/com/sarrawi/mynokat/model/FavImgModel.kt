package com.sarrawi.mynokat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="fav_tb")
data class FavImgModel(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    @ColumnInfo("new_img")
    var new_img:Int,
    @ColumnInfo("pic")
    var pic:String?,
    @ColumnInfo("image_url")
    var image_url:String?,
    @ColumnInfo(name = "createdAt")
    var createdAt: String? = null,
    var is_fav:Boolean = true

)
