package com.kontranik.easycycle.models

import android.os.Parcel
import android.os.Parcelable
import com.kontranik.easycycle.R

class Settings(
    var showOnStart: Int = R.id.navigation_info,
    var daysOnHome: Int,
    var yearsOnStatistic: Int
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(showOnStart)
        parcel.writeInt(daysOnHome)
        parcel.writeInt(yearsOnStatistic)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Settings> {
        override fun createFromParcel(parcel: Parcel): Settings {
            return Settings(parcel)
        }

        override fun newArray(size: Int): Array<Settings?> {
            return arrayOfNulls(size)
        }
    }
}