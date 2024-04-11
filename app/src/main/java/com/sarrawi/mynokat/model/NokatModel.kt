package com.sarrawi.mynokat.model

import com.google.gson.annotations.SerializedName

data class NokatModel(

    @SerializedName("id")
    val id: Int,
    @SerializedName("NokatTypes")
    val NokatTypes: String,
    @SerializedName("new_nokat")
    val new_nokat: Int,
    @SerializedName("NokatName")
    val NokatName: String
)


