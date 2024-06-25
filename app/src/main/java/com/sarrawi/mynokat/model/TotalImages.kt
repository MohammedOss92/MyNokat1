package com.sarrawi.mynokat.model

import com.google.gson.annotations.SerializedName

data class TotalImages(
    @SerializedName("total_images")
    var total_images:Int
)
