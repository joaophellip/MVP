package com.cozo.cozomvp.listfragment

import android.util.Log
import com.cozo.cozomvp.dataprovider.DataProvider
import com.cozo.cozomvp.dataprovider.DataProviderInterface
import com.cozo.cozomvp.mainactivity.MainActivity
import com.cozo.cozomvp.networkapi.*

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class ListPresenter : MvpBasePresenter<ListFragmentView>(), ListInterfaces.Presenter,
        DataProviderInterface.ListFragmentListener {

    private var mDataProvider: DataProvider? = null

    private lateinit var mUserLocation : NetworkModel.Location

    override fun dishNew(restID: String) {
        mDataProvider = DataProvider(this)
        mDataProvider?.provideRestaurantItems(restID)
    }

    override fun getActivity(): MainActivity? {
        var mActivity : MainActivity? = null
        ifViewAttached {
            mActivity = it.onActivityRequired()
        }
        return mActivity
    }

    override fun onRestCardDataRequestCompleted(cards: MutableMap<String, CardMenuData>) {
        ifViewAttached {
            restListSize = cards.size
            it.addRestaurantsDataToCards(cards)
        }
    }

    override fun onRestCardDataRequestFailed(e: Throwable) {
        // do something later
    }

    override fun onPartCardDataRequestCompleted(cards: MutableMap<String, CardInfoData>) {
        ifViewAttached {
            partListSize = cards.size
            it.addPartnersDataToCards(cards)
        }
    }

    override fun onPartCardDataRequestFailed(e: Throwable) {
        // do something later
    }

    override fun onRestItemsDataRequestCompleted(items: List<NetworkModel.MenuMetadata>) {
        ifViewAttached {
            it.addItemsDataToCards(items)
        }
    }

    override fun onRestItemsDataRequestFailed(e: Throwable) {
        // do something later
    }

    override  fun onUserLocationDataAvailable(location: NetworkModel.Location){
        getNearbyRestaurantsCardData(location)
        mUserLocation = location
    }

    override fun onRestLocationDataAvailable(location: NetworkModel.Location) {
        getNearbyDeliveryPartnersCardData(location, mUserLocation)
    }

    // retrieves data for nearby restaurants from Data Provider and sends them to the list view.
    private fun getNearbyRestaurantsCardData(location: NetworkModel.Location){
        mDataProvider = DataProvider(this)
        mDataProvider?.provideRestaurantsCardData(location,10000)
    }

    // retrieves list of nearby delivery partners from Data Provider and sends them to the list view.
    private fun getNearbyDeliveryPartnersCardData(restLocation: NetworkModel.Location, userLocation: NetworkModel.Location){
        mDataProvider = DataProvider(this)
        mDataProvider?.provideDeliveryPartnersList(restLocation, userLocation)
    }

    companion object {
        var restListSize : Int = 0
        var partListSize : Int = 0
    }
}