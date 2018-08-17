package com.cozo.cozomvp.dataprovider

import com.cozo.cozomvp.mainactivity.MainActivity
import com.cozo.cozomvp.networkapi.*
import com.google.android.gms.maps.model.LatLng

interface DataProviderInterface {

    interface MainActivityListener{
        fun onUserLocationRequestCompleted(location: NetworkModel.Location)
        fun onUserLocationRequestFailed(e: Throwable)
        fun getActivity() : MainActivity?
    }

    interface MapFragmentListener{
        fun onRestLocationsRequestCompleted(locations: List<NetworkModel.RestLocationObjects>)
        fun onRestLocationsRequestFailed(e: Throwable)
        fun onRouteRequestCompleted()
        fun onRouteRequestFailed(e: Throwable)
        fun getActivity() : MainActivity?
    }

    interface ListFragmentListener{
        fun onRestCardDataRequestCompleted(cards: MutableMap<String, CardMenuData>)
        fun onRestCardDataRequestFailed(e: Throwable)
        fun onPartCardDataRequestCompleted(cards: MutableMap<String, CardInfoData>)
        fun onPartCardDataRequestFailed(e: Throwable)
        fun getActivity() : MainActivity?
    }

    interface Model{

        // retrieves user coordinates (latitude/longitude) and sends it back to presenter.
        fun provideUserLatLng(idToken: String, isUserDeviceLocationNeeded: Boolean = false)

        // retrieves restaurants' coordinates (latitude/longitude) and sends them back to presenter.
        fun provideRestaurantsLatLng(location: NetworkModel.Location, radius: Int)

        // retrieves restaurants' menu data and sends it back to presenter.
        fun provideRestaurantsCardData(location: NetworkModel.Location, radius: Int)

        // retrieves delivery partners' list (card data plus pairs of latitude/longitude) and send them back to presenter.
        fun provideDeliveryPartnersList(restLocation: NetworkModel.Location, userLocation: NetworkModel.Location)

        // retrieves route and sends it back to presenter.
        fun provideRoute(from: LatLng, to: LatLng)
    }
}