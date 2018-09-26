package com.cozo.cozomvp.usercart

import com.cozo.cozomvp.networkapi.NetworkModel

class CartServiceImpl : ICartService {

    private val items: MutableMap<Int, OrderModel> = mutableMapOf()

    override fun addOrder(order: OrderModel): Boolean {
        if (items.isEmpty()) {
            items[order.id] = order
            return true
        } else {
            val first = items.entries.first().value
            if (first.item.restaurantID != order.item.restaurantID) {
                return false
            }
            items[order.id] = order
            return true
        }
    }

    override fun removeOrder(id: Int): Boolean {
        if (items.contains(id)){
            items.remove(id)
            return true
        }
        return false
    }

    override fun getOrderByID(id: Int): OrderModel? {
        return items[id]
    }

    override fun updateOrderQuantity(id: Int, quantity: Int) {
        val order = items[id]
        if (order?.quantity!! + quantity < 1) {
            order?.quantity = 1
        } else {
           order?.quantity = order?.quantity!! + quantity
        }
    }

    override fun updateOrderNote(id: Int, note: String) {
        val order = items[id]
        order?.notes = note
    }

    override fun applyDiscountCode(code: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOrders(): List<OrderModel> {
        val list = mutableListOf<OrderModel>()
        items.forEach { _, orderModel ->
            list[list.size] = orderModel
        }
        return list
    }

    companion object {

        private var COUNT = 1

        fun createOrder (item: NetworkModel.MenuMetadata, quantity: Int, note: String): OrderModel {
            val price = item.price * quantity
            val order = OrderModel(COUNT, item, price, quantity, note)
            COUNT += 1
            return  order
        }

        fun resetCount () {
            COUNT = 1
        }
    }

}
