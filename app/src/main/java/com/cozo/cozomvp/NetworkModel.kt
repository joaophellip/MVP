package com.cozo.cozomvp

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

/**
 * This is the model for storing the mapping inside fragment of map
 */
data class MapPresenterData(
        val restID: String,
        val location: NetworkModel.Location)

/**
 * This is the model for storing the mapping inside fragment of list
 */
data class ListPresenterData(
        val restID: String,
        val cardMenu: CardMenuData)     //Check the diff btwn object->dataclass vs dataclass only

data class CardMenuData(
        var image: Bitmap?,
        val menu: NetworkModel.MenuMetadata)