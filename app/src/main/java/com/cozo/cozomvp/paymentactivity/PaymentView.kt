package com.cozo.cozomvp.paymentactivity

import com.hannesdorfmann.mosby3.mvp.MvpView

interface PaymentView : MvpView {
    fun onCardsAvailable(cards: List<PaymentActivity.CardData>)
}