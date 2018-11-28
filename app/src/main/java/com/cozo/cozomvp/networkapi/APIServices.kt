package com.cozo.cozomvp.networkapi

import com.cozo.cozomvp.helpers.IdleResourceInterceptor
import io.reactivex.Observable
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * Interface to consume the API endpoint for the backend Location services
 */
interface APIServices{

    @GET("location/main")
    fun userMainLocation(@Query("idToken") idToken: String):
            Observable<NetworkModel.Location>

    @GET("/location/reverse-geocoding/{id}")
    fun userReverseGeocoding(@Path("id") id: String, @Query("latitude") latitude: Double,
                             @Query("longitude") longitude: Double) :
            Observable<UserReverseGeocodingResponse>

    @GET("/location/preview/{id}")
    fun userPreviewDeliveryInfo(@Path("id") id: String, @Query("userLatitude") userLatitude: Double,
                                @Query("userLongitude") userLongitude: Double,
                                @Query("restLatitude") restLatitude: Double,
                                @Query("restLongitude") restLongitude: Double) :
            Observable<UserPreviewDeliveryInfoResponse>

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

    @POST("order/new")
    fun sendOrderToBackEnd(@Body order: OrderConfirmation): Observable<OrderConfirmationResponse>

    companion object {
        fun create(baseUrl: String? = null): APIServices {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.HEADERS
            val client = OkHttpClient.Builder().readTimeout(30,TimeUnit.SECONDS)

            val okHttpClient = client.addInterceptor(logging)
                    .addInterceptor(IdleResourceInterceptor.getInstance())
                    .build()
            okHttpClient.dispatcher().setIdleCallback(IdleResourceInterceptor.getInstance())

            val defaultUrl = "https://us-central1-cozo-platform-version-1.cloudfunctions.net"

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