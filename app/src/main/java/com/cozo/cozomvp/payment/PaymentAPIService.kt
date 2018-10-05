package com.cozo.cozomvp.payment

import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Url

interface PaymentAPIService {

    @POST("customers")
    fun createCustomerProfile(@Body body: CustomerProfileWithFundingInstrument) : Observable<CustomerProfileResponse>

    @POST
    fun createFundingInstrument(@Body body : FundingInstrument, @Url url: String) : Observable<FundingInstrumentResponse>

    @DELETE
    fun deleteFundingInstrument(@Url url: String)

    companion object {
        fun create(): PaymentAPIService {

            val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
            val baseUrl = "https://sandbox.moip.com.br/v2/"
            val retrofit: Retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .baseUrl(baseUrl)
                    .build()
            return retrofit.create(PaymentAPIService::class.java)
        }
    }
}