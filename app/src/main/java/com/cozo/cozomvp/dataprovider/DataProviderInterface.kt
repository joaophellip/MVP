package com.cozo.cozomvp.dataprovider

import com.cozo.cozomvp.networkapi.ListPresenterData
import com.cozo.cozomvp.networkapi.NetworkModel

interface DataProviderInterface {

    interface MainActivityListener{
        fun onUserLocationRequestCompleted(location: NetworkModel.Location)
        fun onUserLocationRequestFailed(e: Throwable)
    }

    interface MapFragmentListener{
        fun onRestLocationsRequestCompleted(locations: List<NetworkModel.RestLocationObjects>)
        fun onRestLocationsRequestFailed(e: Throwable)
    }

    interface ListFragmentListener{
        fun onRestCardDataRequestCompleted(cards: List<ListPresenterData>)
        fun onRestCardDataRequestFailed(e: Throwable)
    }

    interface Model{

        // retrieves user coordinates (latitude/longitude) and sends it back to presenter.
        fun provideUserLatLng(userID: String)

        // retrieves restaurants' coordinates (latitude/longitude) and sends them back to presenter.
        fun provideRestaurantsLatLng(location: NetworkModel.Location, radius: Int)

        // retrieves restaurants' menu data and sends them back to presenter.
        fun provideRestaurantsCardData(location: NetworkModel.Location, radius: Int)
    }
}