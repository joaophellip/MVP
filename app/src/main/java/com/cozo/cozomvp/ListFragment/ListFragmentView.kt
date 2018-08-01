package com.cozo.cozomvp.listFragment

import android.support.v7.widget.RecyclerView
import android.view.View
import com.cozo.cozomvp.mainActivity.MainActivity
import com.cozo.cozomvp.networkAPI.CardInfoData
import com.cozo.cozomvp.networkAPI.CardMenuData
import com.cozo.cozomvp.networkAPI.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpView

interface ListFragmentView : MvpView {

    // adds delivery partners data to card views.
    fun addPartnersDataToCards(cards: MutableMap<String, CardInfoData>)

    // adds restaurants data to card views.
    fun addRestaurantsDataToCards(cards: MutableMap<String, CardMenuData>)

    fun currentRestID(listPosition: Int) : String

    // highlight the cardview that is holding information about given restaurantID. Centers card in the screen.
    fun highlightRestCardView(restID: String)

    // informs activity that a reference to it is required by another class
    fun onActivityRequired() : MainActivity

    fun onRecyclerViewRequired() : RecyclerView

    // callback for when chosen restaurant location is available.
    fun onRestLocationDataAvailable(location: NetworkModel.Location)

    // callback for when user location is available.
    fun onUserLocationDataAvailable(location: NetworkModel.Location)

    fun requestLayout()

    fun sharedViewByPosition(childPosition: Int): View?

    /*
    Interface MainActivityListener contains the abstract methods implemented by MainActivity.kt and
    which are seen by the LocalListFragment class.
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
        fun onCompleteListFragment(listFragment: LocalListFragment)

        /*
        Informs activity that RecyclerView object is ready to show delivery partners data. Passes
        delivery partners locations and routes to be relayed to MapFragment as argument.
         */
        fun onPartnersCardDataReady(locations: MutableMap<String, NetworkModel.Location>,
                                    routes: MutableMap<String, List<NetworkModel.Leg>>)

        /*
        Informs activity that cardview item containing a delivery partner was clicked. Passes
        delivery partner ID as argument.
         */
        fun onPartnerCardViewClicked(partnerID: String)

        /*
        Informs activity that cardview item containing a restaurant menu was clicked. Passes View object,
         transitionName, menu data, and the restaurant ID as argument.
         */
        fun onRestaurantCardViewClicked(sharedView: View, transitionName: String,
                                        data: CardMenuData, restID: String)

        /*
        Informs activity that cardview item containing a restaurant menu was highlighted. Passes
        restaurant ID as argument.
         */
        fun onRestCardViewHighlighted(restID: String)

    }
}

