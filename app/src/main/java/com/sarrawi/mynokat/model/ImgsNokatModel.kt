package com.sarrawi.mynokat.model

import com.google.gson.annotations.SerializedName

data class ImgsNokatModel(
                @SerializedName("id")
                var id:Int,
                @SerializedName("new_img")
                var new_img:Int,
                @SerializedName("pic")
                var pic:String,
                @SerializedName("image_url")
                var image_url:String
)
