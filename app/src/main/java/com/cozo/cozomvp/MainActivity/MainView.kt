package com.cozo.cozomvp

import com.hannesdorfmann.mosby3.mvp.MvpView

interface MainView : MvpView {

    // asks activity to highlight the cardview which is holding information about given restaurantID
    fun highlightCard(restaurantID: String)
}