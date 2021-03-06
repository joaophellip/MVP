package com.cozo.cozomvp.mainactivity.mapfragment

import android.util.Log
import com.cozo.cozomvp.dataprovider.DataProvider
import com.cozo.cozomvp.dataprovider.DataProviderInterface
import com.cozo.cozomvp.mainactivity.MainActivity
import com.cozo.cozomvp.networkapi.MapPresenterData
import com.cozo.cozomvp.networkapi.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class MapFragmentPresenter : MvpBasePresenter<MapFragmentView>(), MapInterfaces.Presenter {

    private var mDataProvider: DataProvider? = null
    private var mMapPresenterData : MutableList<MapPresenterData>? = null   // is it really necessary to have in presenter?

    override fun onUserLocationDataAvailable(location: NetworkModel.Location) {
        ifViewAttached {
            it.addUserMarkerToMap(location)
            getNearbyRestaurantsLocations(location)
        }
    }

    override fun onPartLocationDataAvailable(locations: MutableMap<String, NetworkModel.Location>, encodedPolylines: Map<String, String>) {
        ifViewAttached {
            it.addPartnerMarkersToMap(locations)
            it.savePartnerRoutes(encodedPolylines)
        }
    }

    override fun onRestaurantCardViewClicked(restID: String){
        ifViewAttached {
            it.removeAllButThisRestaurantMarker(restID)
        }
    }

    override fun onPartnerCardViewClicked(partnerID: String) {
        ifViewAttached {
            it.removeAllButThisPartnerMarker(partnerID)
            it.drawRouteToUserLocation(partnerID)
        }
    }

    override fun onBackPressed() {
        ifViewAttached {
            it.addMarkersBack()
        }
    }

    // retrieves locations for nearby restaurants from Data Provider and sends them to the map view.
    private fun getNearbyRestaurantsLocations(location: NetworkModel.Location){
        mDataProvider = DataProvider(object : DataProviderInterface.MapFragmentListener{
            override fun getActivity(): MainActivity? {
                var mActivity : MainActivity? = null
                ifViewAttached {
                    mActivity = it.onActivityRequired()
                }
                return mActivity
            }
            override fun onRouteRequestCompleted() {}
            override fun onRouteRequestFailed(e: Throwable) {}
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
                Log.d("MVPdebug","error while requesting location to backend")
            }
        })
        mDataProvider?.provideRestaurantsLatLng(location,10000)
    }

}