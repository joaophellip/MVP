package com.cozo.cozomvp.paymentactivity.CardListFragment

import com.cozo.cozomvp.paymentactivity.PaymentActivity
import com.hannesdorfmann.mosby3.mvp.MvpView

interface CardListInterface: MvpView {

    fun onCardListAvailable(cards: List<PaymentActivity.CardData>)

    interface CardActivityListener
    {
        fun onAddCardButtonClicked()
        fun onCardDeleted()
        fun updateCardList()
    }
}