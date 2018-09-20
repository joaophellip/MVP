package com.cozo.cozomvp.usercart

interface ICartService {

    fun addOrder(order: OrderModel): Boolean

    fun removeOrder(id: Int): Boolean

    fun getOrderByID(id: Int): OrderModel?

    fun getOrders(): List<OrderModel>

    fun updateOrderQuantity(id: Int, quantity: Int)

    fun updateOrderNote(id: Int, note:String)

    fun applyDiscountCode(code:String)
}