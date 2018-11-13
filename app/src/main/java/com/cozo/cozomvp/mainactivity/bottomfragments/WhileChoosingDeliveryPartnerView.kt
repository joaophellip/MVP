package com.cozo.cozomvp.mainactivity.bottomfragments

import com.hannesdorfmann.mosby3.mvp.MvpView

interface WhileChoosingDeliveryPartnerView : MvpView {

    interface MainActivityListener{

        /*
        Informs activity that method onViewCreated from WhileChoosingDeliveryPartnerFragment was invoked. Passes
        reference to the class as argument.
         */
        fun onCompleteWhileChoosingDeliveryPartnerFragment(whileChoosingDeliveryPartnerFragment: WhileChoosingDeliveryPartnerFragment)

        /*
        Informs activity that button ChoosingDeliveryPartnerCheckoutButton was clicked.
         */
        fun onChoosingDeliveryPartnerConfirmButtonClicked()
    }
}