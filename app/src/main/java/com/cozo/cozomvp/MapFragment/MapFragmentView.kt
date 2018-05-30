package com.cozo.cozomvp

import com.hannesdorfmann.mosby3.mvp.MvpView

interface MapFragmentView : MvpView {

    // adds single marker to identify user's location on map. Centers map around it.
    fun addUserMarkerToMap(location: NetworkModel.Location)

    // fired when view is ready to receive restaurants locations.
    fun readyToReceiveRestaurantsLocations(location: NetworkModel.Location)

    // adds markers to identify restaurants' locations on map.
    fun addRestaurantMarkersToMap(locations: List<NetworkModel.Location>)

    // centers map around latitude and longitude coordinates
    //fun animateMarker(latitude: Double, longitude: Double)
}