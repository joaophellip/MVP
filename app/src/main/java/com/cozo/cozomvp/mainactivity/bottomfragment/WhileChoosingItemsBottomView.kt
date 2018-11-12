package com.cozo.cozomvp.mainactivity.bottomfragment

import com.hannesdorfmann.mosby3.mvp.MvpView

interface WhileChoosingItemsBottomView : MvpView {

    interface MainActivityListener{

        /*
        Informs activity that method onCreateView from WhileChoosingItemsBottomFragment run successfully. Passes
        reference to the class as argument.
         */
        fun onCompleteWhileChoosingItemsBottomFragment(whileChoosingItemsBottomFragment: WhileChoosingItemsBottomFragment)

        fun displayContainer()

        /*
        Informs activity that button onChoosingItemsDeliveryPartnerButton was clicked.
         */
        fun onChoosingItemsDeliveryPartnerButtonClicked()
    }
}