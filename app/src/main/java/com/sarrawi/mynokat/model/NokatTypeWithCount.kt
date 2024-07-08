package com.sarrawi.mynokat.model

import androidx.room.Embedded


data class NokatTypeWithCount(
    @Embedded
    var msgTypes: NokatTypeModel? = null,
    val subCount: Int = 0,
    val newNokatCount: Int = 0
)
