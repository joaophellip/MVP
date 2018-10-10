package com.cozo.cozomvp.paymentactivity.AddCardFragment

import com.hannesdorfmann.mosby3.mvp.MvpView

interface AddCardInterface:MvpView {

    interface CardActivityListener
    {
        fun onNewCardCreated(creditCard: AddCardFragment.NewCreditCardData)
    }
}