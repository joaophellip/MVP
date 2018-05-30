package com.cozo.cozomvp

import com.hannesdorfmann.mosby3.mvp.MvpView

interface ListFragmentView : MvpView {

    // Highlight the cardview that is holding information about given restaurantID. Centers card in the screen.
    fun highlightCardView(restaurantID: String)
}