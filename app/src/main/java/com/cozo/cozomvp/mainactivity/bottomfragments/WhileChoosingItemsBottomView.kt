package com.cozo.cozomvp.mainactivity.bottomfragments

import com.hannesdorfmann.mosby3.mvp.MvpView

interface WhileChoosingItemsBottomView : MvpView {

    interface MainActivityListener{

        /*
        Informs activity that method onViewCreated from WhileChoosingItemsBottomFragment was invoked. Passes
        reference to the class as argument.
         */
        fun onCompleteWhileChoosingItemsBottomFragment(whileChoosingItemsBottomFragment: WhileChoosingItemsBottomFragment)

        /*
        Informs activity that button onChoosingItemsDeliveryPartnerButton was clicked.
         */
        fun onChoosingItemsDeliveryPartnerButtonClicked()
    }
}