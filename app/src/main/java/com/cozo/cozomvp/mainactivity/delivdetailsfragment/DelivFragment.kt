package com.cozo.cozomvp.mainactivity.delivdetailsfragment

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet

class DelivFragment(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs), Parcelable {
    constructor(parcel: Parcel) : this(
            TODO("context"),
            TODO("attrs")) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DelivFragment> {
        override fun createFromParcel(parcel: Parcel): DelivFragment {
            return DelivFragment(parcel)
        }

        override fun newArray(size: Int): Array<DelivFragment?> {
            return arrayOfNulls(size)
        }
    }

}