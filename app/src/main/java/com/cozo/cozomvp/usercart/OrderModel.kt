package com.cozo.cozomvp.usercart

import com.cozo.cozomvp.networkapi.NetworkModel

data class ReviewOrderModel(
        val formattedAddress: String,
        var priceRange: PriceRange?,
        var timeRange: TimeRange?,
        var orders: List<OrderModel>)

class TimeRange(private val min: Int, private val max: Int){

    fun getFormattedMin() : String {
        val quotient : Int = min/3600
        if(quotient >= 1 ){
            val reminder = min%3600
            return "${quotient}h ${reminder}min"
        } else {
            return "${min/60}min"
        }
    }

    fun getFormattedMax() : String {
        val quotient : Int = max/3600
        if(quotient >= 1 ){
            val reminder = max%3600
            return "${quotient}h ${reminder}min"
        } else {
            return "${max/60}min"
        }
    }

}

data class PriceRange(
        val min: Float,
        val max: Float)


data class OrderModel(
        val id: Int,
        val item: NetworkModel.MenuMetadata,
        var totalPrice: Float,
        var quantity: Int,
        var notes: String
)