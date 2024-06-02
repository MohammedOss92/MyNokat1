package com.sarrawi.mynokat.model

interface Bindable {
    fun bind(imgModel: ImgsNokatModel, isInternetConnected: Boolean)
    fun bind(favImgModel: FavImgModel)
}
