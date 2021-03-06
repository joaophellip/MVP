package com.cozo.cozomvp.networkapi

import android.graphics.Bitmap
import com.cozo.cozomvp.paymentactivity.PaymentActivity
import com.cozo.cozomvp.userprofile.Phone

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

data class UserReverseGeocodingResponse (
        val formattedAddress: String)

data class UserPreviewDeliveryInfoResponse(
        val minTime: Int,
        val maxTime: Int,
        val minPrice: Float,
        val maxPrice: Float)

data class OrderConfirmation(
        val userIdToken: String,
        val restaurantIdToken: String,
        val details : OrderDetails)

data class OrderDetails(
        val customerName: String,
        val customerPictureRefID: String,
        val customerPhone: Phone,
        val comments: String,
        val totalPrice: Float,
        val details: List<Items>)

data class Items(
        val itemId: String,
        val amount: String,
        val itemName: String)

data class OrderConfirmationResponse(
        val orderId: String)