package com.sarrawi.mynokat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName ="Nokat_tb",
    foreignKeys =[ForeignKey(entity = NokatTypeModel::class, childColumns = ["ID_Type"], parentColumns = ["id"])])
data class NokatModel(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int,

    @ColumnInfo("ID_Type")
    @SerializedName("ID_Type")
    var ID_Type:Int,

    @ColumnInfo("new_nokat")
    @SerializedName("new_nokat")
    val new_nokat: Int,

    @ColumnInfo("NokatName")
    @SerializedName("NokatName")
    val NokatName: String,

    @ColumnInfo(name = "createdAt")
    var createdAt: String? = null,

    @ColumnInfo("is_fav")
    @SerializedName("is_fav")
    var is_fav: Boolean = false,
)


