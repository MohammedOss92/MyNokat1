package com.sarrawi.mynokat.model

import com.google.gson.annotations.SerializedName

data class Results(@SerializedName("NokatModel")
                   val NokatModel: List<NokatModel>)
