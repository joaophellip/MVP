package com.cozo.cozomvp.mapFragment

import com.cozo.cozomvp.networkapi.NetworkModel

interface MapInterfaces {

    interface Presenter {
        // triggered when user location data is available.
        fun onLocationDataAvailable(location: NetworkModel.Location)
    }

}