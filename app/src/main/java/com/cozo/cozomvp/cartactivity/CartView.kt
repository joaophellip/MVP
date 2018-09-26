package com.cozo.cozomvp.cartactivity

import com.cozo.cozomvp.usercart.OrderModel
import com.hannesdorfmann.mosby3.mvp.MvpView

interface CartView: MvpView {

    fun updateViewData(orders: List<OrderModel>)

    fun returnToParentActivity()

}