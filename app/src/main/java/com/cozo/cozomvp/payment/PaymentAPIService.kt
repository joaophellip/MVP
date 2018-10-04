package com.cozo.cozomvp.payment

import retrofit2.http.Field
import retrofit2.http.POST

interface PaymentAPIService {

    @POST("https://sandbox.moip.com.br/v2/customers/")
    fun createCustomerProfile(@Field("") profile: CustomerProfile)
}