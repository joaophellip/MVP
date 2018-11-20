package com.cozo.cozomvp.mainactivity.listfragment

import android.support.v7.widget.RecyclerView
import android.view.View
import com.cozo.cozomvp.mainactivity.MainActivity
import com.cozo.cozomvp.networkapi.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpView

interface ListFragmentView : MvpView {

    // adds delivery partners data to card views.
    fun addPartnersDataToCards(cards: List<NetworkModel.PartnerMetadata>)

    // adds restaurants data to card views.
    fun addRestaurantsDataToCards(items: List<NetworkModel.MenuMetadata>)

    // adds items from same restaurant to card views.
    fun addItemsDataToCards(items: List<NetworkModel.MenuMetadata>)

    fun currentRestID(listPosition: Int) : String

    fun dishOrderCreation(listPosition: Int)

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
        Informs activity that RecyclerView object is ready to show items from same restaurant.
         */
        fun onItemsCardDataReady()

        /*
Informs activity that cardview item containing a restaurant menu was highlighted. Passes
restaurant ID as argument.
 */
        fun onRestCardViewHighlighted(restID: String)

        /*
Informs activity that RecyclerView object is ready to show delivery partners data. Passes
delivery partners locations and routes to be relayed to MapFragment as argument.
 */
        fun onPartnersCardDataReady(locations: MutableMap<String, NetworkModel.Location>,
                                    encodedPolylines: Map<String, String>)

        /*
        Informs activity that cardview item was clicked. Passes item data as argument.
         */
        fun onItemCardViewClicked(childListPosition: Int, data: NetworkModel.MenuMetadata)

        /*
        Informs activity that cardview item containing a delivery partner was clicked. Passes
        delivery partner ID as argument.
         */
        fun onPartnerCardViewClicked(childListPosition: Int, data: NetworkModel.PartnerMetadata)

        /*
        Informs activity that cardview item containing a restaurant menu was clicked. Passes View object,
         transitionName, menu data, and the restaurant ID as argument.
         */
        fun onRestaurantCardViewClicked(childListPosition: Int, data: NetworkModel.MenuMetadata)

        /*
        Informs activity that cardview item containing a delivery partner was swiped. Passes View object,
         transitionName, and card data as argument.
         */
        fun onPartnerCardViewSwiped(sharedView: View, data: NetworkModel.PartnerMetadata)

        /*
        Informs activity that cardview item containing a restaurant item was swiped. Passes View object,
         transitionName, and card data as argument.
         */
        fun onItemCardViewSwiped(sharedView: View, data: NetworkModel.MenuMetadata)

        /*
        Informs activity that cardview item containing a restaurant item was swiped. Passes View object,
         transitionName, and card data as argument.
         */
        fun onRestaurantCardViewSwiped(sharedView: View, data: NetworkModel.MenuMetadata)

    }
}

