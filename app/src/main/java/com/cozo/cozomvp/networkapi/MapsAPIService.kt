package com.cozo.cozomvp.networkapi

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface to consume the direction API endpoint for google maps service
 */
interface MapsAPIService{

    @GET("maps/api/directions/json")
    fun getRoute(@Query("origin") origin: String,
                 @Query("destination") destination: String):
            Observable<ResponseBody>

    companion object {
        fun create(): MapsAPIService {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.HEADERS
            val okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .baseUrl("http://maps.googleapis.com")
                    .build()

            return retrofit.create(MapsAPIService::class.java)
        }
    }
}