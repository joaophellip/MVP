package com.cozo.cozomvp.cartactivity.listfragment

import com.hannesdorfmann.mosby3.mvp.MvpView

interface ListView: MvpView {

    interface CartActivityListener{
        fun onCompleteListFragment(fragment: ListFragment)
    }

}