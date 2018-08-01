package com.cozo.cozomvp.listFragment

import com.cozo.cozomvp.networkAPI.NetworkModel

interface ListInterfaces {

    interface Presenter {

        // triggered when user location data is available.
        fun onUserLocationDataAvailable(location: NetworkModel.Location)

        // triggered when chosen restaurant location data is available.
        fun onRestLocationDataAvailable(location: NetworkModel.Location)
    }

}