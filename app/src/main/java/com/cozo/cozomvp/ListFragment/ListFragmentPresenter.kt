package com.cozo.cozomvp.listFragment

import com.cozo.cozomvp.CardMenuData
import com.cozo.cozomvp.DataProvider
import com.cozo.cozomvp.ListPresenterData
import com.cozo.cozomvp.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class ListFragmentPresenter : MvpBasePresenter<ListFragmentView>() {

    private var mDataProvider: DataProvider? = null
    private var mListPresenterData : MutableList<ListPresenterData>? = null

    // triggered when user location data is available.
    fun onLocationDataAvailable(location: NetworkModel.Location){
        getNearbyRestaurantsCardData(location)
    }

    // retrieves data for nearby restaurants from Provider and sends them to the list view.
    private fun getNearbyRestaurantsCardData(location: NetworkModel.Location){
        mDataProvider = DataProvider(object : DataProvider.RestaurantCardDataListener {
            override fun onSuccess(cards: List<ListPresenterData>) {
                val menuList = mutableListOf<CardMenuData>()
                cards.forEach {
                    mListPresenterData?.add(it)
                    menuList.add(it.cardMenu)
                }
                ifViewAttached {
                    it.addRestaurantsDataToCards(menuList)
                }
            }
            override fun onError(e: Throwable) {
                //do something later
            }
        })
        //call function to retrieve radius (here or in BL?)
        mDataProvider?.provideRestaurantsCardData(location,2000)
    }
}