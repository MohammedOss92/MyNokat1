package com.sarrawi.mynokat.model

import com.google.gson.annotations.SerializedName

data class NokatTypeResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("total_pages")
    val total_pages: Int,
    @SerializedName("current_page")
    val current_page: Int,
    @SerializedName("results")
    val results: Results,
    @SerializedName("NokatTypeModel")
    val NokatTypeModel: List<NokatTypeModel>
)
