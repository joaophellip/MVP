package com.cozo.cozomvp.cartactivity

import com.cozo.cozomvp.usercart.OrderModel
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class CartPresenter: MvpBasePresenter<CartView>(), ICartPresenter {

    override fun onViewCreated() {
        ifViewAttached { it ->
            it.updateViewData(listOf())
        }
    }

    override fun onOrderUpdate() {
        // Update total price
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onOrderDeleted(order: OrderModel) {
        ifViewAttached { it ->
            it.updateViewData(listOf())
        }
    }

    override fun onPartnerSelectClicked() {
        // open mainActivity partner select window
        ifViewAttached { it ->
            it.returnToParentActivity()
        }
    }

    override fun onDiscountCodeSet(discountCode: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}