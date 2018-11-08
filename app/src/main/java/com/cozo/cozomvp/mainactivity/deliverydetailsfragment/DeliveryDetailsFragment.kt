package com.cozo.cozomvp.mainactivity.deliverydetailsfragment

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet

class DeliveryDetailsFragment(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs), Parcelable {
    constructor(parcel: Parcel) : this(
            TODO("context"),
            TODO("attrs")) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DeliveryDetailsFragment> {
        override fun createFromParcel(parcel: Parcel): DeliveryDetailsFragment {
            return DeliveryDetailsFragment(parcel)
        }

        override fun newArray(size: Int): Array<DeliveryDetailsFragment?> {
            return arrayOfNulls(size)
        }
    }

}