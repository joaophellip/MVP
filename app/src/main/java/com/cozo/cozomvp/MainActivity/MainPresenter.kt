package com.cozo.cozomvp.mainActivity

import com.cozo.cozomvp.dataProvider.DataProvider
import com.cozo.cozomvp.dataProvider.DataProviderInterface
import com.cozo.cozomvp.listFragment.LocalListFragment
import com.cozo.cozomvp.mapFragment.LocalMapFragment
import com.cozo.cozomvp.networkAPI.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class MainPresenter : MvpBasePresenter<MainView>(), MainInterfaces {

    private var mDataProvider: DataProvider? = null

    override fun provideUserLocation(){
        mDataProvider = DataProvider(object : DataProviderInterface.MainActivityListener {
            override fun onUserLocationRequestCompleted(location: NetworkModel.Location) {
                ifViewAttached {
                    it.onLocationAvailable(location)
                }
            }
            override fun onUserLocationRequestFailed(e: Throwable) {
                //do something later
            }
        })
        // call function to retrieve userID (here or in BL?)
        mDataProvider?.provideUserLatLng("UserFoo")
    }

    override fun relayLocationToListFragment(location: NetworkModel.Location, listFragment: LocalListFragment){
        listFragment.onLocationDataAvailable(location)
    }

    override fun relayLocationToMapFragment(location: NetworkModel.Location, mapFragment: LocalMapFragment){
        mapFragment.onLocationDataAvailable(location)
    }

    override fun onCardViewHighlighted(restID: String, mapFragment: LocalMapFragment){
        mapFragment.highlightMapMarker(restID)
    }

    override fun onMapMarkerClicked(restID: String, listFragment: LocalListFragment) {
        listFragment.highlightCardView(restID)
    }

}