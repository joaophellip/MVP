package com.cozo.cozomvp.mapFragment

import com.cozo.cozomvp.mainActivity.MainActivity
import com.cozo.cozomvp.networkAPI.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpView

/*
    Interface MapFragmentView contains the abstract methods implemented by LocalMapFragment.kt and which are seen by the Presenter.
 */
interface MapFragmentView : MvpView {

    // informs activity that a reference to it is required by another class
    fun onActivityRequired() : MainActivity

    // callback for back press event when restaurants are shown
    fun onBackPressed()

    // callback for when delivery partner locations are available.
    fun onPartLocationDataAvailable(locations: MutableMap<String, NetworkModel.Location>, routes: MutableMap<String, List<NetworkModel.Leg>>)

    // callback for when delivery partner card view has been clicked.
    fun onPartnerCardViewClicked(partnerID: String)

    // callback for when restaurant card view has been clicked.
    fun onRestaurantCardViewClicked(restID: String)

    // callback for when user location is available.
    fun onUserLocationDataAvailable(location: NetworkModel.Location)

    // makes all restaurant markers visible on map
    fun addMarkersBack()

    // adds markers to identify delivery partners' locations on map.
    fun addPartnerMarkersToMap(locations: MutableMap<String, NetworkModel.Location>)

    // adds markers to identify restaurants' locations on map.
    fun addRestaurantMarkersToMap(locations: List<NetworkModel.RestLocationObjects>)

    // adds single marker to identify user's location on map. Centers map around it.
    fun addUserMarkerToMap(location: NetworkModel.Location)

    // draws route to from 'partnerID' to user location
    fun drawRouteToUserLocation(partnerID: String)

    // highlights restaurant marker to match highlighted cardview.
    fun highlightMapMarker(restID: String)

    // makes all delivery partner markers invisible on map, except for the one mapped to 'partnerID'
    fun removeAllButThisPartnerMarker(partnerID: String)

    // makes all restaurant markers invisible on map, except for the one mapped to 'restID'
    fun removeAllButThisRestaurantMarker(restID: String)

    // returns restaurant location
    fun restLocation(restID: String) : NetworkModel.Location

    // stores routes from delivery partner to user location with a midway point at restaurant
    fun savePartnerRoutes(routes: MutableMap<String, List<NetworkModel.Leg>>)

    /*
    Interface MainActivityListener contains the abstract methods implemented by MainActivity.kt and
    which are seen by the LocalMapFragment class.
     */
    interface MainActivityListener{

        /*
        Asks activity to return a reference to the very activity.
         */
        fun onActivityRequired(): MainActivity

        /*
        Informs activity that method onCreateView from LocalListFragment run successfully. Passes
        reference to the class as argument.
         */
        fun onCompleteMapFragment(mapFragment: LocalMapFragment)

        /*
        Informs activity that a marker containing a restaurant location was clicked. Passes the
        restaurant ID as argument.
         */
        fun onMapMarkerClicked(restID: String)
    }

}