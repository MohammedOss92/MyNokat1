package com.sarrawi.mynokat.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList


data class Model2(
    val imgNokatList: List<ImgsNokatModel>? // إضافة قائمة من ImgsNokatModel
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(ImgsNokatModel)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(imgNokatList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Model2> {
        override fun createFromParcel(parcel: Parcel): Model2 {
            return Model2(parcel)
        }

        override fun newArray(size: Int): Array<Model2?> {
            return arrayOfNulls(size)
        }
    }
}
