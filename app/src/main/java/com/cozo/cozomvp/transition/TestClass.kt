package com.cozo.cozomvp.transition

import android.content.Context
import com.cozo.cozomvp.R

class TestClass(context: Context) {

    private var mContext = context

    fun getHelloWorldString(): String{
        return mContext.getString(R.string.app_name)
    }
}