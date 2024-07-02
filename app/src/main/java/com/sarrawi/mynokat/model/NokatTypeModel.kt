package com.sarrawi.mynokat.model

import com.google.gson.annotations.SerializedName

data class NokatTypeModel(

    @SerializedName("id")
    val id: Int,
    @SerializedName("NoktTypes")
    val NoktTypes: String,
    @SerializedName("new_Nokat")
    val new_Nokat: Int,
    @SerializedName("new_Nokat_show")
    val new_Nokat_show: Int

)

