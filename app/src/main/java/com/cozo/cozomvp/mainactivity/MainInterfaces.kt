package com.cozo.cozomvp.mainactivity

import android.view.View
import com.cozo.cozomvp.networkapi.CardMenuData
import com.cozo.cozomvp.networkapi.NetworkModel

/*
    Interface MainInterfaces contains the abstract methods implemented by MainPresenter.kt and which are seen by the View.
 */
interface MainInterfaces {

    /*
    Informs presenter that view activity was created.
     */
    fun onActivityCreated(isFirstTimeLogged : Boolean)

    /*
    Informs presenter that back button has been pressed in main activity. Passes position within
    RecyclerView that had been previously inflated as argument.
     */
    fun onBackPressed(listPosition: Int)

    /*
    Informs presenter that both list and map fragments are ready to be used.
     */
    fun onFragmentReady(){

    }

    fun onLocationServiceReady()

    /*
    Informs presenter that a restaurant map marker was clicked by user. Passes restaurant ID
    associated with the marker as argument.
     */
    fun onMapMarkerClicked(restID: String)

    /*
    Informs presenter that order button was clicked by user. Passes position within RecyclerView
    of chosen menu as argument.
     */
    fun onOrderButtonClicked(listPosition: Int)

    /*
    Informs presenter that a card view for a delivery partner was clicked by user. Passes delivery
    partner ID associated with the card view as argument.
     */
    fun onPartnerCardViewClicked(partnerID: String)

    /*
    Informs presenter that a card view for a delivery partner was clicked by user. Passes the
    locations and routes for all available delivery partners as argument.
     */
    fun onPartnersCardDataReady(locations: MutableMap<String, NetworkModel.Location>,
                                routes: MutableMap<String, List<NetworkModel.Leg>>)

    /*
    Informs presenter that a card view for a restaurant was clicked by user. Passes the restaurant
    ID, the menu data, and the View object as argument.
     */
    fun onRestaurantCardViewClicked(restID: String, sharedView: View, data: CardMenuData)

    /*
    Informs presenter that a card view for a restaurant was highlighted. Passes the restaurant ID
    associated with the card view as argument.
     */
    fun onRestCardViewHighlighted(restID: String)

    fun onSettingsMenuClicked()
    fun onPaymentMenuClicked()

}