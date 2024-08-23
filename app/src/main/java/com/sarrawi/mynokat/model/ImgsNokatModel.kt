package com.sarrawi.mynokat.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "ImgsNokat_tb")
data class ImgsNokatModel(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @SerializedName("new_img")
    var new_img: Int = 0,
    @SerializedName("pic")
    var pic: String? = "",
    @SerializedName("image_url")
    var image_url: String? = "",
    var is_fav: Boolean = false,

) : Parcelable {
    constructor() : this(0, 0, "", "", false) // منشئ افتراضي

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(new_img)
        parcel.writeString(pic)
        parcel.writeString(image_url)
        parcel.writeByte(if (is_fav) 1 else 0)
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
