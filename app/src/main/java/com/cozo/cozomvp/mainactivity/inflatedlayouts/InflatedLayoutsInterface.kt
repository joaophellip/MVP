package com.cozo.cozomvp.mainactivity.inflatedlayouts

import com.cozo.cozomvp.usercart.OrderModel

/*
Interface InflatedLayoutsInterface contains the abstract methods implemented by MainActivity.kt and which are seen by ItemDetailsMenu.
 */
interface InflatedLayoutsInterface {

    interface MainActivityListener {

        /*
        Informs main activity that addItemToCart button was clicked by user in ItemDetailsMenu.
         */
        fun onItemAddedToCart(order: OrderModel)

        fun onReviewCartChooseDeliveryPartnerButtonClicked()

        fun onItemRemovedFromCart()
    }

}