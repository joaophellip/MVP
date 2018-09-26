package com.cozo.cozomvp.cartactivity

import android.os.Bundle
import com.cozo.cozomvp.usercart.OrderModel
import com.hannesdorfmann.mosby3.mvp.MvpActivity

class CartActivity: MvpActivity<CartView, CartPresenter>(), CartView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createPresenter(): CartPresenter {
        return CartPresenter()
    }

    override fun updateViewData(orders: List<OrderModel>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun returnToParentActivity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun editOrder(order: OrderModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}