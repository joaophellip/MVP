package com.cozo.cozomvp.cartactivity

import com.cozo.cozomvp.usercart.OrderModel

interface ICartPresenter {

    fun onViewCreated()

    fun onOrderUpdate()

    fun onOrderDeleted(order: OrderModel)

    fun onPartnerSelectClicked()

    fun onDiscountCodeSet(discountCode: String)
}