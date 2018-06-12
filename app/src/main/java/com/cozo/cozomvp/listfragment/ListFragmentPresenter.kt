package com.cozo.cozomvp.listFragment

import android.util.Log
import com.cozo.cozomvp.dataprovider.DataProvider
import com.cozo.cozomvp.dataprovider.DataProviderInterface
import com.cozo.cozomvp.networkapi.CardMenuData
import com.cozo.cozomvp.networkapi.ListPresenterData
import com.cozo.cozomvp.networkapi.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class ListFragmentPresenter : MvpBasePresenter<ListFragmentView>(), ListInterfaces.Presenter {

    private var mDataProvider: DataProvider? = null
    private var mListPresenterData : MutableList<ListPresenterData>? = null

    override  fun onLocationDataAvailable(location: NetworkModel.Location){
        getNearbyRestaurantsCardData(location)
    }

    // retrieves data for nearby restaurants from Provider and sends them to the list view.
    private fun getNearbyRestaurantsCardData(location: NetworkModel.Location){
        mDataProvider = DataProvider(object : DataProviderInterface.ListFragmentListener {
            override fun onRestCardDataRequestCompleted(cards: List<ListPresenterData>) {
                val menuList = mutableListOf<CardMenuData>()
                cards.forEach {
                    mListPresenterData?.add(it)
                    menuList.add(it.cardMenu)
                }
                ifViewAttached {
                    listSize = menuList.size
                    it.addRestaurantsDataToCards(cards)
                }
            }

            override fun onRestCardDataRequestFailed(e: Throwable) {
                //do something later
                Log.d("MVPdebug", "error na chamada http do menu - expected")
            }
        })
        //call function to retrieve radius (here or in BL?)
        mDataProvider?.provideRestaurantsCardData(location,2000)
    }

    companion object {
        var listSize : Int = 0
    }
}