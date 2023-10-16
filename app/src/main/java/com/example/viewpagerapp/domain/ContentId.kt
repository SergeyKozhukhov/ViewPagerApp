package com.example.viewpagerapp.domain

import android.os.Parcel
import android.os.Parcelable

data class ContentId(
    override val id: Int,
    override val subId: Int
) : ContentKey, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(subId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContentId> {
        override fun createFromParcel(parcel: Parcel): ContentId {
            return ContentId(parcel)
        }

        override fun newArray(size: Int): Array<ContentId?> {
            return arrayOfNulls(size)
        }
    }
}