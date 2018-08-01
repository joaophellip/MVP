package com.cozo.cozomvp.mainactivity

import android.support.v7.widget.RecyclerView
import android.view.View
import com.cozo.cozomvp.listfragment.LocalListFragment
import com.cozo.cozomvp.mapfragment.LocalMapFragment
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
    fun addRecyclerViewToContainer(mRecyclerView : RecyclerView)

    /*
Asks activity to hide a item details view. Passes View object as argument.
  */
    fun hideOrderDetailsMenu(mSharedView: View?)

    /*
    Asks activity to return a reference to the very activity.
     */
    fun onActivityRequired() : MainActivity

    /*
    Asks activity to return a reference to current LocalListFragment object is required.
     */
    fun onListFragmentRequired() : LocalListFragment

    /*
    Asks activity to return a reference to current LocalMapFragment object.
     */
    fun onMapFragmentRequired() : LocalMapFragment

    /*
    Asks activity to show a item details view. Passes View and CardMenuData objects as argument.
     */
    fun showOrderDetailsMenu(sharedView: View, data: CardMenuData)

    fun goToSettingsActivity()
    fun goToPaymentActivity()
}