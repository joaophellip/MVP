package com.cozo.cozomvp.listFragment

import com.cozo.cozomvp.networkapi.ListPresenterData
import com.cozo.cozomvp.networkapi.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpView

interface ListFragmentView : MvpView {

    // callback for when user location is available.
    fun onLocationDataAvailable(location: NetworkModel.Location)

    // adds restaurants data to card views.
    fun addRestaurantsDataToCards(cards: List<ListPresenterData>)

    // highlight the cardview that is holding information about given restaurantID. Centers card in the screen.
    fun highlightCardView(restID: String)

    interface MainActivityListener{

        // callback for when cardview item is highlighted.
        fun onCardViewHighlighted(restID: String)
    }
}

