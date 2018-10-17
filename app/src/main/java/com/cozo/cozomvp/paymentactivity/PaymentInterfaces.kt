package com.cozo.cozomvp.paymentactivity

import com.cozo.cozomvp.networkapi.CreditCardData
import com.cozo.cozomvp.paymentactivity.AddCardFragment.AddCardFragment

interface PaymentInterfaces {

    fun onNewCardCreated(creditCard: AddCardFragment.NewCreditCardData)
    fun onCardDeleted()
    fun onCardListFragmentReady()

    interface Model{
        fun onCreditCardListAvailable(creditCardData: CreditCardData)
        fun onCreditCardDataSentToPaymentsAPI(externalId: String, creditCard: PaymentActivity.CardData)
        fun onCreditCardDataStoredInBackend()
    }
}