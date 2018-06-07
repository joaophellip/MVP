package com.cozo.cozomvp.listFragment

import com.cozo.cozomvp.networkAPI.NetworkModel

interface ListInterfaces {

    interface Presenter {

        // triggered when user location data is available.
        fun onLocationDataAvailable(location: NetworkModel.Location)
    }

}