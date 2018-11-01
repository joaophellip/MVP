package com.cozo.cozomvp.cartactivity

import com.cozo.cozomvp.usercart.CartServiceImpl
import com.cozo.cozomvp.usercart.OrderModel
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class CartPresenter: MvpBasePresenter<CartView>(), ICartPresenter {

    override fun onFragmentReady() {
        ifViewAttached { it ->
            it.updateViewData(CartServiceImpl.myInstance.getOrders())
        }
    }

    override fun onOrderUpdate(orderID: Int, quantity: Int, notes: String) {
        CartServiceImpl.myInstance.updateOrderNote(orderID, notes)
        CartServiceImpl.myInstance.updateOrderQuantity(orderID, quantity)
        ifViewAttached { it ->
            it.updateViewData(CartServiceImpl.myInstance.getOrders())
        }
    }

    override fun onOrderDeleted(order: OrderModel) {
        CartServiceImpl.myInstance.removeOrder(order.id)
        ifViewAttached { it ->
            it.updateViewData(CartServiceImpl.myInstance.getOrders())
        }
    }

    override fun onConfirmOrderButtonClicked() {
        // open mainActivity partner select window
        ifViewAttached { it ->
            it.returnToParentActivity()
        }
    }

    override fun onDiscountCodeSet(discountCode: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}