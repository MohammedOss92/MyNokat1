package com.sarrawi.mynokat.model

sealed class ItemModel {
    data class ImgsItem(val imgsNokatModel: ImgsNokatModel ) : ItemModel()
    data class AnotherItem(val favImgModel: FavImgModel ) : ItemModel()
}


sealed class ItemModela {
    data class ImgsItem(val imgsNokatModel: ImgsNokatModel = ImgsNokatModel()) : ItemModel()
    data class AnotherItem(val favImgModel: FavImgModel = FavImgModel()) : ItemModel()
}


