package com.cozo.cozomvp.userprofile

import com.cozo.cozomvp.helpers.IdleResourceInterceptor
import com.cozo.cozomvp.networkapi.CreditCardData
import com.cozo.cozomvp.networkapi.SaveFavoriteCreditCardResponse
import com.cozo.cozomvp.networkapi.SaveUserExternalIdResponse
import com.cozo.cozomvp.paymentactivity.PaymentActivity
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ProfileServiceAPI{

    @Multipart
    @POST("user/{id}/profileAvatar")
    fun saveUserAvatar(@Path("id") id: String, @Part image: MultipartBody.Part) : Observable<String>

    @FormUrlEncoded
    @PATCH("user/{id}")
    fun saveUserExternalId(@Path("id") id: String, @Field("externalId") externalId: String):
            Observable<SaveUserExternalIdResponse>

    @POST("user/")
    fun createUserProfile(@Body userModel: UserModel): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("user/{id}/creditCard")
    fun saveUserCreditCard(@Path("id") id: String, @Field("creditCardData") creditCard: PaymentActivity.CardData): Observable<CreditCardData>

    @FormUrlEncoded
    @POST("user/{id}/favorite")
    fun saveFavoriteCreditCard(@Path("id") userId: String, @Field("cardId") cardId: String): Observable<SaveFavoriteCreditCardResponse>

    @GET("user/{id}/favorite")
    fun getUserFavoriteCreditCard(@Path("id") id: String) :
            Observable<String?>

    @GET("user/{id}")
    fun loadUserProfile(@Path("id") token: String) : Observable<UserModel>

    companion object {
        fun create(baseUrl: String? = null): ProfileServiceAPI {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.HEADERS
            val client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS)

            val okHttpClient: OkHttpClient = client.addInterceptor(logging)
                    .addInterceptor(IdleResourceInterceptor.getInstance())
                    .build()
            okHttpClient.dispatcher().setIdleCallback(IdleResourceInterceptor.getInstance())

            val defaultUrl = "https://us-central1-cozo-platform-version-1.cloudfunctions.net"

            val retrofit: Retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .baseUrl(baseUrl ?: defaultUrl)
                    .build()

            return retrofit.create(ProfileServiceAPI::class.java)
        }
    }
}