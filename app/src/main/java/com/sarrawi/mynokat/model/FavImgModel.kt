package com.sarrawi.mynokat.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_tb")
data class FavImgModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo("new_img")
    var new_img: Int = 0,
    @ColumnInfo("pic")
    var pic: String? = "",
    @ColumnInfo("image_url")
    var image_url: String? = "",
    @ColumnInfo(name = "createdAt")
    var createdAt: String? = null,
    var is_fav: Boolean = true
) : Parcelable {
    constructor() : this(0, 0, "", "", null, true) // منشئ افتراضي

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(new_img)
        parcel.writeString(pic)
        parcel.writeString(image_url)
        parcel.writeString(createdAt)
        parcel.writeByte(if (is_fav) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavImgModel> {
        override fun createFromParcel(parcel: Parcel): FavImgModel {
            return FavImgModel(parcel)
        }

        override fun newArray(size: Int): Array<FavImgModel?> {
            return arrayOfNulls(size)
        }
    }
}
