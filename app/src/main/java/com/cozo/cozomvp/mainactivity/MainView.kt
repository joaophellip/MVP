package com.cozo.cozomvp.mainactivity

import android.support.v7.widget.RecyclerView
import android.view.View
import com.cozo.cozomvp.mainactivity.checkoutfragment.CheckoutFragment
import com.cozo.cozomvp.mainactivity.listfragment.LocalListFragment
import com.cozo.cozomvp.mainactivity.mapfragment.LocalMapFragment
import com.cozo.cozomvp.networkapi.CardMenuData
import com.hannesdorfmann.mosby3.mvp.MvpView

/*
Interface MainView contains the abstract methods implemented by MainActivity.kt and which are seen by the Presenter.
 */
interface MainView : MvpView {

    /*
    Asks activity to add a RecyclerView object to container. Passes RecyclerView object as
    argument.
     */
    fun addRecyclerViewToContainer(recyclerView : RecyclerView)

    /*
    Asks activity whether both list and map fragments are ready to be used.
     */
    fun areFragmentsReady() : Boolean

    /*
    Asks activity to display a message as Toast
     */
    fun displayMessage(message: String)

    /*
    Asks activity to show cart activity
     */
    fun goToCartActivity()

    /*
    Asks activity to show payment activity
     */
    fun goToPaymentActivity()

    /*
    Asks activity to show settings activity
     */
    fun goToSettingsActivity()

    fun goToUserProfileActivity()

    /*
    Asks activity to hide a item details view. Passes View object as argument.
     */
    fun hideOrderDetailsMenu(sharedView: View?)

    /*
    Asks activity to launch a new map fragment.
     */
    fun launchMapFragment()

    /*
    Asks activity to launch a new list fragment.
     */
    fun launchListFragment()

    /*
    Asks activity to launch a new checkout fragment.
     */
    fun launchCheckoutFragment()

    /*
    Asks activity to return a reference to the very activity.
     */
    fun onActivityRequired() : MainActivity

    /*
    Asks activity to return a reference to current LocalListFragment object is required.
     */
    fun onListFragmentRequired() : LocalListFragment

    /*
    Asks activity to return a reference to current CheckoutFragment object is required.
     */
    fun onCheckoutFragmentRequired() : CheckoutFragment

    /*
    Asks activity to return a reference to current LocalMapFragment object.
     */
    fun onMapFragmentRequired() : LocalMapFragment

    /*
    Asks activity to request permission for accessing user location
     */
    fun requestUserPermissionForLocation()

    /*
    Asks activity to create a navigation drawer
     */
    fun setUpNavigationDrawer(userName: String)

    /*
    Asks activity to show a item details view. Passes View and CardMenuData objects as argument.
     */
    fun showOrderDetailsMenu(sharedView: View, data: CardMenuData)

}