package com.cozo.cozomvp.paymentactivity

import com.cozo.cozomvp.networkapi.APIServices
import com.cozo.cozomvp.paymentactivity.AddCardFragment.AddCardFragment
import com.cozo.cozomvp.paymentapi.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PaymentModel(private var listener: PaymentInterfaces.Model) {

    private var disposable: Disposable? = null
    private val paymentService by lazy {
        PaymentAPIService.create()
    }
    private val backendService by lazy {
        APIServices.create()
    }
    private val defaultShippingAddress = DefaultShippingAddress("São Paulo","Paraíso",
            "Rua Coronel Oscar Porto. Apt 93", "40", "04003000", "SP",
            "BRA")

    fun requestCardListFromBackend(userId: String) {
        disposable = backendService.userCreditCardInfo(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            listener.onCreditCardListAvailable(it)
                        },
                        {})
    }

    fun sendCreditCardToPaymentsAPI(user: PaymentPresenter.UserInfo, creditCard: AddCardFragment.NewCreditCardData){
        var obs: Observable<CustomerProfileResponse>? = null

        if(user.externalId == null) {
            val customer = CustomerProfile(user.id, creditCard.holderName, user.email, creditCard.birthDate,
                    "CPF", creditCard.holderCpf, user.phoneNumber.countryCode, user.phoneNumber.areaCode, user.phoneNumber.number,
                    defaultShippingAddress.city, defaultShippingAddress.district, defaultShippingAddress.street,
                    defaultShippingAddress.streetNumber, defaultShippingAddress.zipCode, defaultShippingAddress.state,
                    defaultShippingAddress.country)
             obs = paymentService.createCustomerProfile(customer)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }

        if (obs == null) {
            val fundingInstrument = FundingInstrument("CREDIT_CARD", creditCard.expireDate, creditCard.expireDate,
                    creditCard.cardNumber, creditCard.cvc, creditCard.holderName, creditCard.birthDate,
                    CustomerProfile.TaxDocument("CPF",creditCard.holderCpf),
                    CustomerProfile.Phone(user.phoneNumber.countryCode,user.phoneNumber.areaCode, user.phoneNumber.number))
            disposable = paymentService.createFundingInstrument(fundingInstrument, user.externalId!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                listener.onCreditCardDataSentToPaymentsAPI(user.externalId, PaymentActivity.CardData(it.creditCard.id,
                                        creditCard.holderName,
                                        it.creditCard.last4,
                                        brandMapping(it.creditCard.brand)))
                            },
                            {})
        } else {
            var extID: String? = null
            disposable = obs.concatMap {
                val fundingInstrument = FundingInstrument("CREDIT_CARD", creditCard.expireDate, creditCard.expireDate,
                        creditCard.cardNumber, creditCard.cvc, creditCard.holderName, creditCard.birthDate,
                        CustomerProfile.TaxDocument("CPF",creditCard.holderCpf),
                        CustomerProfile.Phone(user.phoneNumber.countryCode,user.phoneNumber.areaCode, user.phoneNumber.number))
                extID = it.id
                paymentService.createFundingInstrument(fundingInstrument, it.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }.subscribe({
                listener.onCreditCardDataSentToPaymentsAPI(extID!!, PaymentActivity.CardData(it.creditCard.id,
                        creditCard.holderName,
                        it.creditCard.last4,
                        brandMapping(it.creditCard.brand)))
            }, {})
        }
    }

    fun saveCreditCardToBackend(creditCard: PaymentActivity.CardData, userInfo: PaymentPresenter.UserInfo){
        disposable = backendService.saveUserCreditCard(creditCard, userInfo.externalId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            listener.onCreditCardListAvailable(it)
                        },
                        {})
    }

    private fun brandMapping(brand: String): Int{
        return when(brand){
            "MASTERCARD" -> 0
            "VISA" -> 1
            "HIPER","HIPERCARD" -> 2
            "ELO" -> 3
            "DINERS" -> 4
            "AMEX" -> 5
            else -> -1
        }
    }

    fun saveUserPaymentMappingToBackend(creditCard: PaymentActivity.CardData, userInfo: PaymentPresenter.UserInfo, externalId: String) {
        disposable = backendService.saveUserExternalId(userInfo.id, externalId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap {
                    backendService.saveUserCreditCard(creditCard, it.externalId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }.subscribe({
                    listener.onCreditCardListAvailable(it)
                }, {})
    }

    data class DefaultShippingAddress (val city: String,
               val district: String,
               val street: String,
               val streetNumber: String,
               val zipCode: String,
               val state: String,
               val country: String)

}