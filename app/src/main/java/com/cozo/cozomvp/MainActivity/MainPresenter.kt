package com.cozo.cozomvp.mainActivity

import com.cozo.cozomvp.*
import com.cozo.cozomvp.listFragment.LocalListFragment
import com.cozo.cozomvp.mapFragment.LocalMapFragment
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class MainPresenter : MvpBasePresenter<MainView>() {

    private var mDataProvider: DataProvider? = null

    // retrieves user geo location (latitude, longitude) from Data Provider and sends it to the view.
    fun provideUserLocation(){
        mDataProvider = DataProvider(object : DataProvider.UserLocationListener {
            override fun onSuccess(location: NetworkModel.Location) {
                ifViewAttached {
                    it.onLocationAvailable(location)
                }
            }
            override fun onError(e: Throwable) {
                //do something later
            }
        })
        // call function to retrieve userID (here or in BL?)
        mDataProvider?.provideUserLatLng("UserFoo")
    }

    // informs list fragment that user location has been retrieved
    fun relayLocationToListFragment(location: NetworkModel.Location, listFragment: LocalListFragment){
        listFragment.onLocationDataAvailable(location)
    }

    // informs map fragment that user location has been retrieved
    fun relayLocationToMapFragment(location: NetworkModel.Location, mapFragment: LocalMapFragment){
        mapFragment.onLocationDataAvailable(location)
    }

    // informs list fragment that cardview needs to be highlighted
    fun highlightCardView(restaurantID: String, listFragment: LocalListFragment){
        listFragment.highlightCardView(restaurantID)
    }

}