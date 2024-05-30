package com.sarrawi.mynokat.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName ="ImgsNokat_tb")
data class ImgsNokatModel(
                @SerializedName("id")
                var id:Int,
                @SerializedName("new_img")
                var new_img:Int,
                @SerializedName("pic")
                var pic:String?,
                @SerializedName("image_url")
                var image_url:String?
):Parcelable {
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
