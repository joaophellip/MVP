package com.cozo.cozomvp.mapfragment

import com.cozo.cozomvp.networkapi.NetworkModel
import com.google.android.gms.maps.model.LatLng

interface MapInterfaces {

    interface Presenter {
        // triggered when user location data is available
        fun onUserLocationDataAvailable(location: NetworkModel.Location)

        // triggered when delivery partner locations and routes are available.
        fun onPartLocationDataAvailable(locations: MutableMap<String, NetworkModel.Location>, routes: MutableMap<String, List<NetworkModel.Leg>>)

        // triggered when restaurant card view has been clicked
        fun onRestaurantCardViewClicked(restID: String)

        // triggered when delivery partner card view has been clicked
        fun onPartnerCardViewClicked(partnerID: String)

        // triggered when back button has been pressed
        fun onBackPressed()

    }

}