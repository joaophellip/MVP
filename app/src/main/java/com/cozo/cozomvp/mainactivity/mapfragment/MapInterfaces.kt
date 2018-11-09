package com.cozo.cozomvp.mainactivity.mapfragment

import com.cozo.cozomvp.networkapi.NetworkModel

interface MapInterfaces {

    interface Presenter {
        // triggered when user location data is available
        fun onUserLocationDataAvailable(location: NetworkModel.Location)

        // triggered when delivery partner locations and routes are available.
        fun onPartLocationDataAvailable(locations: MutableMap<String, NetworkModel.Location>, encodedPolylines: Map<String, String>)

        // triggered when restaurant card view has been clicked
        fun onRestaurantCardViewClicked(restID: String)

        // triggered when delivery partner card view has been clicked
        fun onPartnerCardViewClicked(partnerID: String)

        // triggered when back button has been pressed
        fun onBackPressed()

    }

}