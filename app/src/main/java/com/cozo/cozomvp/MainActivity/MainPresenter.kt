package com.cozo.cozomvp

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class MainPresenter : MvpBasePresenter<MainView>() {

    fun highlightCardView(restaurantID: String, mListFragment: LocalListFragment){
        mListFragment.highlightCardView(restaurantID)
    }

}