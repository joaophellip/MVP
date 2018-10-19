package com.cozo.cozomvp.userprofile

import com.cozo.cozomvp.paymentactivity.PaymentActivity

data class UserModel(
        val ownId: String,
        val name: String,
        var email: String,
        var phone: Phone,
        var avatarUrl: String?,
        var paymentExternalId: String?,
        var fundingInstruments: MutableList<PaymentActivity.CardData>)

data class Phone(
        val countryCode: String,
        val areaCode: String,
        val phoneNumber: String)