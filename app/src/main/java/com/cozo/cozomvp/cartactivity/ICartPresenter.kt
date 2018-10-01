package com.cozo.cozomvp.cartactivity

import com.cozo.cozomvp.usercart.OrderModel

interface ICartPresenter {

    fun onFragmentReady()

    fun onOrderUpdate(orderID: Int, quantity: Int, notes: String)

    fun onOrderDeleted(order: OrderModel)

    fun onPartnerSelectClicked()

    fun onDiscountCodeSet(discountCode: String)
}