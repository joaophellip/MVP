package com.cozo.cozomvp.networkAPI

import android.graphics.Bitmap

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

    // data model for APIServices.restaurantsNearestMenu
    data class ListNearestRestaurantMenu(val objects: List<RestMenuObjects>)
    data class RestMenuObjects(
            val id: String,
            val metadata: MenuMetadata)
    data class MenuMetadata(
            val ingredients: String,
            val name: String,
            val prepTime: Int,
            val pictureRefID: String,
            val price: Float,
            val rating: Float,
            val ratedBy: Int)

    // data model for SocketIO onListAvailable
    data class ListDeliveryPartnersInfo(val objects: List<PartInfoObjects>)
    data class PartInfoObjects(
            val id: String,
            val metadata: PartnerMetadata)
    data class PartnerMetadata(
            val location: Location,
            val name: String,
            val pricePerKm: Float,
            val totalPrice: Float,
            val route: List<Leg>    //https://developers.google.com/maps/documentation/directions/intro#Legs
    )
    data class Leg(
            val distance: Distance,
            val duration: Duration,
            val end_address: String,
            val end_location: LatLngLocation,
            val start_address: String,
            val start_location: LatLngLocation,
            val steps: List<Steps>,
            val traffic_speed_entry: List<Any>,
            val via_waypoint: List<Any>)
    data class Distance(
            val text: String,
            val value: Long)
    data class Duration(
            val text: String,
            val value: Long)
    data class Steps(
            val distance: Any,
            val duration: Any,
            val end_location: LatLngLocation,
            val html_instructions: String,
            val maneuver: String,
            val polyline: Any,
            val start_location: LatLngLocation,
            val travel_mode: String)
    data class LatLngLocation(
            val lat: Double,
            val lng: Double)

    // returned data model for APIServices.restaurantsMetadata
    data class ListRestaurantsMetadata(val objects: List<RestMetadataObjects>)
    data class RestMetadataObjects(
            val id: String,
            val metadata: RestMetadata)
    data class RestMetadata(
            val name: String,
            val relativePrice: Int,
            val rating: Float,
            val ratedBy: Int)
}

//Check the diff btwn object->dataclass vs dataclass only

/**
 * This is the model for storing the mapping inside fragment of map
 */
data class MapPresenterData(
        val restID: String,
        val location: NetworkModel.Location)

/**
 * This is the model for storing the restID/data mapping inside fragment of list
 */
data class RestListPresenterData(
        val restID: String,
        val cardMenu: CardMenuData)

/**
 * This is the model for storing the partnerID/data mapping inside fragment of list
 */
data class PartnerListPresenterData(
        val partnerID: String,
        val cardInfo: CardInfoData)

data class CardMenuData(
        var image: Bitmap?,
        var menu: NetworkModel.MenuMetadata?)
data class CardInfoData(
        var image: Bitmap?,
        var info: NetworkModel.PartnerMetadata?)