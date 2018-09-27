package com.cozo.cozomvp.mainactivity.listfragment

import com.cozo.cozomvp.networkapi.NetworkModel

interface ListInterfaces {

    interface Presenter {

        // triggered when user location data is available.
        fun onUserLocationDataAvailable(location: NetworkModel.Location)

        // triggered when chosen restaurant location data is available.
        fun onRestLocationDataAvailable(location: NetworkModel.Location)
    }

}