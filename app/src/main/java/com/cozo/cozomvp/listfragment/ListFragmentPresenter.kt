package com.cozo.cozomvp.listfragment

import android.util.Log
import com.cozo.cozomvp.dataprovider.DataProvider
import com.cozo.cozomvp.dataprovider.DataProviderInterface
import com.cozo.cozomvp.mainactivity.MainActivity
import com.cozo.cozomvp.networkapi.*

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class ListFragmentPresenter : MvpBasePresenter<ListFragmentView>(), ListInterfaces.Presenter {

    private var mDataProvider: DataProvider? = null
    private lateinit var mUserLocation : NetworkModel.Location

    override  fun onUserLocationDataAvailable(location: NetworkModel.Location){
        getNearbyRestaurantsCardData(location)
        mUserLocation = location
    }

    override fun onRestLocationDataAvailable(location: NetworkModel.Location) {
        getNearbyDeliveryPartnersCardData(location, mUserLocation)
    }

    // retrieves data for nearby restaurants from Data Provider and sends them to the list view.
    private fun getNearbyRestaurantsCardData(location: NetworkModel.Location){
        mDataProvider = DataProvider(object : DataProviderInterface.ListFragmentListener {
            override fun onPartCardDataRequestCompleted(cards: MutableMap<String,CardInfoData>) {}
            override fun onPartCardDataRequestFailed(e: Throwable) {}
            override fun onRestCardDataRequestCompleted(cards: MutableMap<String,CardMenuData>) {
                val menuList = mutableListOf<CardMenuData>()
                /*cards.forEach {
                    mListPresenterData?.add(it)
                    menuList.add(it.cardMenu)
                }*/
                ifViewAttached {
                    restListSize = menuList.size
                    it.addRestaurantsDataToCards(cards)
                }
            }
            override fun onRestCardDataRequestFailed(e: Throwable) {
                Log.d("MVPdebug", "error na chamada http do menu - expected")
                //do something later
            }
            override fun getActivity(): MainActivity? {
                var mActivity : MainActivity? = null
                ifViewAttached {
                    mActivity = it.onActivityRequired()
                }
                return mActivity
            }
        })
        mDataProvider?.provideRestaurantsCardData(location,2000)
    }

    // retrieves list of nearby delivery partners from Data Provider and sends them to the list view.
    private fun getNearbyDeliveryPartnersCardData(restLocation: NetworkModel.Location, userLocation: NetworkModel.Location){
        mDataProvider = DataProvider(object : DataProviderInterface.ListFragmentListener {
            override fun onPartCardDataRequestCompleted(cards: MutableMap<String, CardInfoData>) {
                ifViewAttached {
                    partListSize = cards.size
                    it.addPartnersDataToCards(cards)
                }
            }
            override fun onPartCardDataRequestFailed(e: Throwable) {
                //do something later
            }
            override fun onRestCardDataRequestCompleted(cards: MutableMap<String,CardMenuData>) {}
            override fun onRestCardDataRequestFailed(e: Throwable) {}
            override fun getActivity(): MainActivity? {
                var mActivity : MainActivity? = null
                ifViewAttached {
                    mActivity = it.onActivityRequired()
                }
                return mActivity
            }
        })
        mDataProvider?.provideDeliveryPartnersList(restLocation, userLocation)
    }

    companion object {
        var restListSize : Int = 0
        var partListSize : Int = 0
    }
}