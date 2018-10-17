package com.cozo.cozomvp.userprofile

data class UserModel(
        val ownId: String,
        var email: String,
        var phone: Phone,
        var avatarUrl: String?,
        var paymentExternalId: String?,
        var fundingInstruments: MutableList<XPTO>)

data class Phone(
        val countryCode: String,
        val areaCode: String,
        val phoneNumber: String)