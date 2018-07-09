package com.cozo.cozomvp.mainActivity

import com.cozo.cozomvp.listFragment.LocalListFragment
import com.cozo.cozomvp.mapFragment.LocalMapFragment
import com.cozo.cozomvp.networkapi.NetworkModel

interface MainInterfaces {

    // retrieves user geo location (latitude, longitude) from Data Provider and sends it to the view.
    fun provideUserLocation()

    // informs list fragment that user location has been retrieved
    fun relayLocationToListFragment(location: NetworkModel.Location, listFragment: LocalListFragment)

    // informs map fragment that user location has been retrieved
    fun relayLocationToMapFragment(location: NetworkModel.Location, mapFragment: LocalMapFragment)

    // informs map fragment that icon needs to be highlighted
    fun onCardViewHighlighted(restID: String, mapFragment: LocalMapFragment)

    // informs map fragment that icon needs to be highlighted
    fun onMapMarkerClicked(restID: String, listFragment: LocalListFragment)

    fun onSettingsMenuClicked()
}