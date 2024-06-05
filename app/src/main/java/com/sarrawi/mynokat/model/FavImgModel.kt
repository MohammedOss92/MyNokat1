package com.sarrawi.mynokat.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="fav_tb")
data class FavImgModel(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    @ColumnInfo("new_img")
    var new_img:Int,
    @ColumnInfo("pic")
    var pic:String?,
    @ColumnInfo("image_url")
    var image_url:String?,
    @ColumnInfo(name = "createdAt")
    var createdAt: String? = null,
    var is_fav:Boolean = true

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(new_img)
        parcel.writeString(pic)
        parcel.writeString(image_url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImgsNokatModel> {
        override fun createFromParcel(parcel: Parcel): ImgsNokatModel {
            return ImgsNokatModel(parcel)
        }

        override fun newArray(size: Int): Array<ImgsNokatModel?> {
            return arrayOfNulls(size)
        }
    }
}

