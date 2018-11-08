package com.cozo.cozomvp.mainactivity.cartfragment

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet

class CartFragment(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs), Parcelable {

    constructor(parcel: Parcel) : this(
            TODO("context"),
            TODO("attrs")) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartFragment> {
        override fun createFromParcel(parcel: Parcel): CartFragment {
            return CartFragment(parcel)
        }

        override fun newArray(size: Int): Array<CartFragment?> {
            return arrayOfNulls(size)
        }
    }

}