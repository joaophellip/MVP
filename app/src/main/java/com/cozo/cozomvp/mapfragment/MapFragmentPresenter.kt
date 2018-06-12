package com.cozo.cozomvp.mapFragment

import android.util.Log
import com.cozo.cozomvp.dataprovider.DataProvider
import com.cozo.cozomvp.dataprovider.DataProviderInterface
import com.cozo.cozomvp.networkapi.MapPresenterData
import com.cozo.cozomvp.networkapi.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class MapFragmentPresenter : MvpBasePresenter<MapFragmentView>(), MapInterfaces.Presenter {

    private var mDataProvider: DataProvider? = null
    private var mMapPresenterData : MutableList<MapPresenterData>? = null   // is it really necessary to have in presenter?

    override  fun onLocationDataAvailable(location: NetworkModel.Location){
        ifViewAttached {
            it.addUserMarkerToMap(location)
            getNearbyRestaurantsLocations(location)
        }
    }

    // retrieves locations for nearby restaurants from Provider and sends them to the map view.
    private fun getNearbyRestaurantsLocations(location: NetworkModel.Location){
        mDataProvider = DataProvider(object : DataProviderInterface.MapFragmentListener{
            override fun onRestLocationsRequestCompleted(locations: List<NetworkModel.RestLocationObjects>) {
                val latlngList = mutableListOf<NetworkModel.Location>()
                locations.forEach {
                    mMapPresenterData?.add(MapPresenterData(it.id,it.location))
                    latlngList.add(NetworkModel.Location(it.location.latitude,it.location.longitude))
                }
                ifViewAttached {
                    it.addRestaurantMarkersToMap(locations)
                }
            }
            override fun onRestLocationsRequestFailed(e: Throwable) {
                //do something later
                Log.d("MVPdebug","eh deu pau prq a funcao do backend ta modificada")
            }
        })
        //call function to retrieve radius (here or in BL)
        mDataProvider?.provideRestaurantsLatLng(location,2000)
    }
}