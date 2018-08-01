package com.cozo.cozomvp.mainactivity

import android.view.View
import com.cozo.cozomvp.dataprovider.DataProvider
import com.cozo.cozomvp.dataprovider.DataProviderInterface
import com.cozo.cozomvp.listfragment.LocalListFragment
import com.cozo.cozomvp.mapfragment.LocalMapFragment
import com.cozo.cozomvp.networkapi.CardMenuData
import com.cozo.cozomvp.networkapi.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class MainPresenter : MvpBasePresenter<MainView>(), MainInterfaces {

    private var mDataProvider: DataProvider? = null

    /*
    Retrieves user geo location (latitude, longitude) from Data Provider and sends it to the view.
      */
    override fun onActivityCreated() {
        retrieveUserLocation()
    }

    /*
    Informs map fragment that all markers need to be visible
      */
    override fun onBackPressed(listPosition: Int) {
        ifViewAttached {

            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mMapFragment.onBackPressed()
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.requestLayout()

            it.hideOrderDetailsMenu(mListFragment.sharedViewByPosition(listPosition))
            it.addRecyclerViewToContainer(mListFragment.onRecyclerViewRequired())
        }
    }

    /*
    Informs list fragment that card needs to be highlighted
      */
    override fun onMapMarkerClicked(restID: String) {
        ifViewAttached {
            val mListFragment : LocalListFragment = it.onListFragmentRequired()
            mListFragment.highlightRestCardView(restID)
        }
    }

    override fun onOrderButtonClicked(listPosition: Int) {
        ifViewAttached{
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            val restID: String = mListFragment.currentRestID(listPosition)
            it.hideOrderDetailsMenu(mListFragment.sharedViewByPosition(listPosition))
            provideRestLocation(restID)
        }
    }

    /*
    Informs map fragment that other markers need to be invisible and route needs to be drawn
     */
    override fun onPartnerCardViewClicked(partnerID: String) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onPartnerCardViewClicked(partnerID)
        }
    }

    //check later
    override fun onPaymentMenuClicked() {
        ifViewAttached {
            it.goToPaymentActivity()
        }
    }

    //check later
    override fun onSettingsMenuClicked() {
        ifViewAttached {
            it.goToSettingsActivity()
        }
    }

    /*
    Informs map fragment the geolocation and routes of the delivery partners
     */
    override fun onPartnersCardDataReady(locations: MutableMap<String, NetworkModel.Location>, routes: MutableMap<String, List<NetworkModel.Leg>>) {
        ifViewAttached {
            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mMapFragment.onPartLocationDataAvailable(locations, routes)

            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.requestLayout()

            it.addRecyclerViewToContainer(mListFragment.onRecyclerViewRequired())
        }
    }

    /*
    Informs map fragment that other markers need to be invisible
     */
    override fun onRestaurantCardViewClicked(restID: String, sharedView: View, data: CardMenuData) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onRestaurantCardViewClicked(restID)
            it.showOrderDetailsMenu(sharedView, data)
        }
    }

    /*
    Informs map fragment that marker needs to be highlighted
     */
    override fun onRestCardViewHighlighted(restID: String){
        ifViewAttached {
            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mMapFragment.highlightMapMarker(restID)
        }
    }

    private fun provideRestLocation(restID: String) {
        ifViewAttached {
            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            relayChosenRestaurantLocationToListFragment(mMapFragment.restLocation(restID))
        }
    }

    private fun retrieveUserLocation(){
        mDataProvider = DataProvider(object : DataProviderInterface.MainActivityListener {
            override fun getActivity(): MainActivity? {
                var mActivity : MainActivity? = null
                ifViewAttached {
                    mActivity = it.onActivityRequired()
                }
                return mActivity
            }
            override fun onUserLocationRequestCompleted(location: NetworkModel.Location) {
                relayUserLocationToListFragment(location)
                relayUserLocationToMapFragment(location)
            }
            override fun onUserLocationRequestFailed(e: Throwable) {
                //do something later
            }
        })
        // call function to retrieve userID (here or in BL?)
        mDataProvider?.provideUserLatLng("UserFoo")
    }

    private fun relayChosenRestaurantLocationToListFragment(location: NetworkModel.Location) {
        ifViewAttached {
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.onRestLocationDataAvailable(location)
        }
    }

    private fun relayUserLocationToListFragment(location: NetworkModel.Location){
        ifViewAttached {
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.onUserLocationDataAvailable(location)
        }
    }

    private fun relayUserLocationToMapFragment(location: NetworkModel.Location){
        ifViewAttached {
            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mMapFragment.onUserLocationDataAvailable(location)
        }
    }

}