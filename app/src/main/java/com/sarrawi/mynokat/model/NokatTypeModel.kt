package com.sarrawi.mynokat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName ="NokatType_tb")
data class NokatTypeModel(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int,
    @SerializedName("NoktTypes")
    val NoktTypes: String,
    @SerializedName("new_Nokat")
    val new_Nokat: Int

)

