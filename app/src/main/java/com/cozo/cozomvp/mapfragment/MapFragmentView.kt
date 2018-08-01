package com.cozo.cozomvp.mapFragment

import com.cozo.cozomvp.networkapi.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpView

interface MapFragmentView : MvpView {

    // callback for when user location is available.
    fun onLocationDataAvailable(location: NetworkModel.Location)

    // adds single marker to identify user's location on map. Centers map around it.
    fun addUserMarkerToMap(location: NetworkModel.Location)

    // adds markers to identify restaurants' locations on map.
    fun addRestaurantMarkersToMap(locations: List<NetworkModel.RestLocationObjects>)

    // highlight restaurant marker to match highlighted cardview.
    fun highlightMapMarker(restID: String)

    interface MainActivityListener{

        //
        fun onMapMarkerClicked(restID: String)
    }

}