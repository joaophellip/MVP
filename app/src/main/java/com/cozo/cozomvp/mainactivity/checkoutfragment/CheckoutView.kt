package com.cozo.cozomvp.mainactivity.checkoutfragment

import com.hannesdorfmann.mosby3.mvp.MvpView

interface CheckoutView : MvpView {

    interface MainActivityListener{

        /*
        Informs activity that method onCreateView from CheckoutFragment run successfully. Passes
        reference to the class as argument.
         */
        fun onCompleteCheckoutFragment(checkoutFragment: CheckoutFragment)

        fun displayContainer()
    }
}