package com.cozo.cozomvp.mapFragment

import com.cozo.cozomvp.DataProvider
import com.cozo.cozomvp.MapPresenterData
import com.cozo.cozomvp.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class MapFragmentPresenter : MvpBasePresenter<MapFragmentView>() {

    private var mDataProvider: DataProvider? = null
    private var mMapPresenterData : MutableList<MapPresenterData>? = null

    // triggered when user location data is available.
    fun onLocationDataAvailable(location: NetworkModel.Location){
        ifViewAttached {
            it.addUserMarkerToMap(location)
            getNearbyRestaurantsLocations(location)
        }
    }

    // retrieves locations for nearby restaurants from Provider and sends them to the map view.
    private fun getNearbyRestaurantsLocations(location: NetworkModel.Location){
        mDataProvider = DataProvider(object : DataProvider.RestaurantLocationListener {
            override fun onSuccess(locations: List<NetworkModel.RestLocationObjects>) {
                val latlngList = mutableListOf<NetworkModel.Location>()
                locations.forEach {
                    mMapPresenterData?.add(MapPresenterData(it.id,it.location))
                    latlngList.add(NetworkModel.Location(it.location.latitude,it.location.longitude))
                }
                ifViewAttached {
                    it.addRestaurantMarkersToMap(latlngList)
                }
            }
            override fun onError(e: Throwable) {
                //do something later
            }
        })
        //call function to retrieve radius (here or in BL)
        mDataProvider?.provideRestaurantsLatLng(location,2000)
    }
}