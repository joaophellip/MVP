package com.cozo.cozomvp.cartactivity.listfragment

import com.cozo.cozomvp.usercart.OrderModel
import com.hannesdorfmann.mosby3.mvp.MvpView

interface ListView: MvpView {

    interface CartActivityListener{
        fun onCompleteListFragment(fragment: ListFragment)
        fun onUpdateCartOrder(orderID: Int, quantity: Int, notes: String)
        fun onDeleteCartOrder(order: OrderModel)
    }

}