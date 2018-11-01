package com.cozo.cozomvp.paymentapi

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface PaymentAPIService {

    @POST("customers")
    fun createCustomerProfile(@Body body: CustomerProfile) : Observable<CustomerProfileResponse>

    @POST("customers/{id}/fundinginstruments")
    fun createFundingInstrument(@Body body : FundingInstrument, @Path("id") url: String) : Observable<FundingInstrumentResponse>

    @DELETE("fundinginstruments/{id}")
    fun deleteFundingInstrument(@Path("id") id: String): Observable<ResponseBody>

    companion object {
        private var httpHeader : String = "no token"

        fun setHeader(header:String) {
            httpHeader = header
        }

        //TODO: refactor create function to add encrypted authToken. Pass as header in client
        fun create(baseUrl: String? = null): PaymentAPIService {

            val okHttpClient = OkHttpClient.Builder()

            okHttpClient.addInterceptor {
                val original = it.request()

                val request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Basic " + httpHeader)
                        .method(original.method(), original.body())
                        .build()


               it.proceed(request)
            }

            val defaultUrl = "https://sandbox.moip.com.br/v2/"
            val retrofit: Retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient.build())
                    .baseUrl(baseUrl ?: defaultUrl)
                    .build()
            return retrofit.create(PaymentAPIService::class.java)
        }
    }
}