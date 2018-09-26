package com.cozo.cozomvp.usercart

import com.cozo.cozomvp.networkapi.NetworkModel

class CartServiceImpl : ICartService {

    private val items: MutableMap<Int, OrderModel> = mutableMapOf()

    fun clear() {
        items.clear()
    }

    override fun addOrder(order: OrderModel): Boolean {
        if (items.isEmpty()) {
            items.put(order.id, order)
            return true
        } else {
            val first = items.entries.first().value
            if (first.item.restaurantID != order.item.restaurantID) {
                return false
            }
            items.put(order.id, order)
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
        order!!.quantity = quantity
    }

    override fun updateOrderNote(id: Int, note: String) {
        val order = items[id]
        order!!.notes = note
    }

    override fun applyDiscountCode(code: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOrders(): List<OrderModel> {
        val list = mutableListOf<OrderModel>()
        for (order in items.values)
            list.add(order)

        return list
    }

    companion object {

        private var COUNT = 1

        val myInstance = CartServiceImpl()

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