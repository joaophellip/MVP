package com.cozo.cozomvp.networkapi

import com.cozo.cozomvp.paymentactivity.PaymentActivity
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable
import io.reactivex.internal.operators.observable.ObservableAll
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.POST

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

    @GET("user/creditCardInfo")
    fun userCreditCardInfo(@Query("idtoken") idToken: String) :
            Observable<CreditCardData>

    @POST("user")
    fun saveUserExternalId(@Field("userId") id: String, @Field("externalId") externalId: String):
            Observable<SaveUserExternalIdResponse>

    @POST("user/creditCard")
    fun saveUserCreditCard(creditCard: PaymentActivity.CardData, externalId: String): Observable<CreditCardData>

    companion object {
        fun create(): APIServices {
            // Variables used for testing API using an interceptor
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.HEADERS
            val okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .baseUrl("https://us-central1-cozo-platform-version-1.cloudfunctions.net")
                    .build()

            return retrofit.create(APIServices::class.java)
        }
    }
}