package com.cozo.cozomvp.mainActivity

import com.cozo.cozomvp.networkapi.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpView

interface MainView : MvpView {

    // asks activity to highlight the cardview which is holding information about given restaurantID
    //fun highlightCard(restaurantID: String)

    // informs activity that location data has been received. Passes location as a parameter
    fun onLocationAvailable(location: NetworkModel.Location)

    fun goToSettingsActivity()
}