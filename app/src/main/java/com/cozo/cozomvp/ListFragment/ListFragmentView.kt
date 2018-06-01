package com.cozo.cozomvp.listFragment

import com.cozo.cozomvp.CardMenuData
import com.cozo.cozomvp.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpView

interface ListFragmentView : MvpView {

    // callback for when user location is available.
    fun onLocationDataAvailable(location: NetworkModel.Location)

    // adds restaurants data to card views.
    fun addRestaurantsDataToCards(cards: List<CardMenuData>)

    // highlight the cardview that is holding information about given restaurantID. Centers card in the screen.
    fun highlightCardView(restaurantID: String)
}