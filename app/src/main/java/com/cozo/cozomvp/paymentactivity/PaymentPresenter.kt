package com.cozo.cozomvp.paymentactivity

import com.cozo.cozomvp.networkapi.CreditCardData
import com.cozo.cozomvp.paymentactivity.AddCardFragment.AddCardFragment
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class PaymentPresenter(private var userInfo: UserInfo) : MvpBasePresenter<PaymentView>(), PaymentInterfaces, PaymentInterfaces.Model{

    private var model = PaymentModel(this)
    private var externalAPIId: String? = null

    override fun onCardListFragmentReady() {
        val cards = updateCardList()
    }

    override fun onCardDeleted() {
        //do something with model
    }

    override fun onNewCardCreated(creditCard: AddCardFragment.NewCreditCardData) {
        //save card data to backend and payments API
        model.sendCreditCardToPaymentsAPI(userInfo, creditCard)
    }

    override fun onCreditCardDataSentToPaymentsAPI(externalId: String, creditCard: PaymentActivity.CardData) {
        if(userInfo.externalId == null) {
            model.saveUserPaymentMappingToBackend(creditCard, userInfo, externalId)
        } else {
            model.saveCreditCardToBackend(creditCard, userInfo)
        }

    }

    override fun onCreditCardDataStoredInBackend() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreditCardListAvailable(creditCardData: CreditCardData) {
        externalAPIId = creditCardData.externalAPIId
        ifViewAttached {
            it.onCardsAvailable(creditCardData.creditCardList)
        }
    }

    private fun updateCardList() {
        //request card list for user.
        model.requestCardListFromBackend(userInfo.id)
    }

    data class UserInfo(
            val externalId: String?,
            val id: String,
            val email: String,
            val phoneNumber: PhoneNumber)
    data class PhoneNumber(
            val countryCode: String,
            val areaCode: String,
            val number: String)

}