package com.cozo.cozomvp.networkapi

import android.graphics.Bitmap
import com.cozo.cozomvp.paymentactivity.PaymentActivity

/**
 * This is the singleton model for storing backend responses received through HTTP
 */
object NetworkModel{

    // data model for APIServices.restaurantsNearestLocation
    data class ListNearestRestaurantsLocation(val objects: List<RestLocationObjects>)
    data class RestLocationObjects(
            val id: String,
            val location: Location)
    data class Location(
            val latitude: Double,
            val longitude: Double)

    data class MenuMetadata(
            val ingredients: String,
            val name: String,
            val prepTime: Int,
            val pictureRefID: String,
            val price: Float,
            val rating: Float,
            val ratedBy: Int,
            val restaurantName: String,
            val restaurantID: String)

    data class ListRestaurantItem(
            val items: List<MenuMetadata>
    )

    data class ListPartners(
            val items: List<PartnerMetadata>
    )

    // data model for SocketIO onListAvailable
    data class PartnerMetadata(
            val location: Location,
            val name: String,
            val partnerID: String,
            val pricePerKm: Float,
            val pictureRefID: String,
            val rating: Float,
            val ratedBy: Int,
            val totalPrice: Float,
            val totalDeliveryTime: Float,
            val encodedPolyline: String
    )
}

/**
 * This is the model for storing the mapping inside fragment of map
 */
data class MapPresenterData(
        val restID: String,
        val location: NetworkModel.Location)

data class CreditCardData(
        var externalAPIId: String?,
        var creditCardList: List<PaymentActivity.CardData>
)

data class SaveUserExternalIdResponse (
        val externalId: String
)

data class SaveFavoriteCreditCardResponse (
        val creditCardId: String
)

data class AuthorizationToken(
        val encryptedToken: String
)