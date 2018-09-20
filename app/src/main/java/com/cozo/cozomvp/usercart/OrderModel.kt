package com.cozo.cozomvp.usercart

import com.cozo.cozomvp.networkapi.NetworkModel

data class OrderModel(
        val id: Int,
        val item: NetworkModel.MenuMetadata,
        var totalPrice: Float,
        var quantity: Int,
        var notes: String
)