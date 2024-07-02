package com.sarrawi.mynokat.model

import com.google.gson.annotations.SerializedName

data class Results(@SerializedName("NokatModel")
                   val NokatModel: List<NokatModel>,
                   @SerializedName("ImgsNokatModel")
                   val ImgsNokatModel: List<ImgsNokatModel>,
                    @SerializedName("NokatTypeModel")
                    val NokatTypeModel:List<NokatTypeModel>)
