package com.cozo.cozomvp

/**
 * These is the singleton model for storing backend responses received through HTTP
 */
object NetworkModel{

    // used as data model for data.JSON from APIServices.restaurantsNearest
    data class ListRestaurantsNearest(val objects: List<RestNearestObjects>)
    data class RestNearestObjects(
            val id: String,
            val location: Location,
            val metadata: MenuMetadata)
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