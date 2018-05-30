package com.cozo.cozomvp.MapFragment

import com.cozo.cozomvp.MapDataProvider
import com.cozo.cozomvp.MapFragmentView
import com.cozo.cozomvp.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class MapFragmentPresenter : MvpBasePresenter<MapFragmentView>() {

    private var mDataProvider: MapDataProvider? = null

    // retrieves user geo location (latitude, longitude) from Provider and sends it to the view.
    fun provideUserLocation(){
        mDataProvider = MapDataProvider(object : MapDataProvider.MapDataLoadListener {
            override fun onMapDataGenerated(location: NetworkModel.Location) {
                ifViewAttached {
                    it.addUserMarkerToMap(location)
                    it.readyToReceiveRestaurantsLocations(location)
                }
            }
            override fun onRestaurantDataGenerated(locations: List<NetworkModel.Location>) {}
        })
        // call function to retrieve userID (here or in BL?)
        mDataProvider?.provideUserLatLng("UserFoo")
    }

    // retrieves locations for nearby restaurants from Provider and sends them to the view.
    fun provideRestaurantsLocations(location: NetworkModel.Location){
        mDataProvider = MapDataProvider(object : MapDataProvider.MapDataLoadListener {
            override fun onMapDataGenerated(location: NetworkModel.Location) {}
            override fun onRestaurantDataGenerated(locations: List<NetworkModel.Location>) {
                ifViewAttached {
                    it.addRestaurantMarkersToMap(locations)
                }
            }
        })
        //call function to retrieve radius (here or in BL)
        mDataProvider?.provideRestaurantsLatLng(location,2000)
    }
}