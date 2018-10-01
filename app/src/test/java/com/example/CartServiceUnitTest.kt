package com.example

import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.usercart.CartServiceImpl
import com.cozo.cozomvp.usercart.OrderModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CartServiceUnitTest {

    var ID = 1
    val item: NetworkModel.MenuMetadata = NetworkModel.MenuMetadata("pimenta",
            "itemTest",
            10,
            "null",
            10f,
            5f,
            1,
            "Kiosk da Ana",
            "abc123")

    @Before
    fun setup(){
        CartServiceImpl.myInstance.clear()
    }

    @Test
    fun cartServiceImpl_addOrder_success() {
        val order = createOrderHelper(item, 1, "no note")
        CartServiceImpl.myInstance.addOrder(order)
        Assert.assertEquals(1,CartServiceImpl.myInstance.getOrders().size)

    }

    @Test
    fun cartServiceImpl_removeOrder_success() {
        val order = createOrderHelper(item, 1, "no note")
        CartServiceImpl.myInstance.addOrder(order)
        CartServiceImpl.myInstance.removeOrder(order.id)
        Assert.assertEquals(0,CartServiceImpl.myInstance.getOrders().size)
    }

    @Test
    fun cartServiceImpl_updateQuantity_success() {
        val order = createOrderHelper(item, 1, "no note")
        CartServiceImpl.myInstance.addOrder(order)
        CartServiceImpl.myInstance.updateOrderQuantity(order.id, 5)
        Assert.assertEquals(5,order.quantity)
    }

    @Test
    fun cartServiceImpl_getOrders_notEmpty() {
        val order1 = createOrderHelper(item, 1, "no note")
        val order2 = createOrderHelper(item, 3, "no note2")
        CartServiceImpl.myInstance.addOrder(order1)
        CartServiceImpl.myInstance.addOrder(order2)
        val arr:Array<OrderModel> = arrayOf(order1, order2)
        Assert.assertArrayEquals(arr,CartServiceImpl.myInstance.getOrders().toTypedArray())
    }

    @Test
    fun cartServiceImpl_removeOrder_fail() {
        val order1 = createOrderHelper(item, 1, "no note")
        val result = CartServiceImpl.myInstance.removeOrder(order1.id)
        Assert.assertFalse("Removing an order not present in the cart should return false",result)
    }

    @Test(expected = NullPointerException::class)
    fun cartServiceImpl_updateQuantity_fail() {
        val order1 = createOrderHelper(item, 1, "no note")
        CartServiceImpl.myInstance.updateOrderQuantity(order1.id, 5)
    }

    fun createOrderHelper(item: NetworkModel.MenuMetadata, qnt: Int, notes: String) : OrderModel{
        return OrderModel(ID++,item,10f,qnt,notes)
    }
}