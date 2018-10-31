package com.cozo.cozomvp.networkapi

import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Interface to consume the API endpoint for the backend Location services
 */
interface APIServices{

    @GET("location/main")
    fun userMainLocation(@Query("idToken") idToken: String):
            Observable<NetworkModel.Location>

    @GET ("restaurants/nearest/location")
    fun restaurantsNearestLocation(@Query("radius") radius: Int,
                           @Query("latitude") latitude: Double,
                           @Query("longitude") longitude: Double):
            Observable<NetworkModel.ListNearestRestaurantsLocation>

    @GET ("restaurants/nearest/menu")
    fun restaurantsNearestMenu(@Query("radius") radius: Int,
                                   @Query("latitude") latitude: Double,
                                   @Query("longitude") longitude: Double):
            Observable<NetworkModel.ListRestaurantItem>

    @GET ("restaurants/items")
    fun restaurantsItems(@Query("restaurantID") restaurantID: String):
            Observable<NetworkModel.ListRestaurantItem>

    @GET("config/authentication")
    fun authenticationToken(): Observable<AuthorizationToken>

    companion object {
        fun create(baseUrl: String? = null): APIServices {
            // Variables used for testing API using an interceptor
            val logging = HttpLoggingInterceptor()
            val defaultUrl = "https://us-central1-cozo-platform-version-1.cloudfunctions.net"
            logging.level = HttpLoggingInterceptor.Level.HEADERS
            val client = OkHttpClient.Builder().readTimeout(30,TimeUnit.SECONDS)
            val okHttpClient = client.addInterceptor(logging).build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .baseUrl(baseUrl ?: defaultUrl)
                    .build()

            return retrofit.create(APIServices::class.java)
        }
    }
}