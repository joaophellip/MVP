package com.cozo.cozomvp.userprofile

import com.cozo.cozomvp.paymentactivity.PaymentActivity
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileServiceModel {

    constructor(baseUrl: String? = null) {
        if (baseUrl == null) {
            profileServiceAPI = ProfileServiceAPI.create()
        } else {
            profileServiceAPI = ProfileServiceAPI.create(baseUrl)
        }
    }

    private val profileServiceAPI: ProfileServiceAPI

    fun uploadUserProfileToBackEnd(userModel: UserModel) {
        profileServiceAPI.createUserProfile(userModel)
    }

    fun uploadUserAvatarToStorage(userId: String, image: File): Observable<String> {
        val reqFile = RequestBody.create(MediaType.parse("image/*"), image)
        val body = MultipartBody.Part.createFormData("upload", userId, reqFile)
        return profileServiceAPI.saveUserAvatar(userId, body)
    }

    fun updateUserPaymentExternalIdToBackEnd(userId: String, externalId: String){
        profileServiceAPI.saveUserExternalId(userId, externalId)
    }

    fun addUserFundingInstrumentToBackEnd(userId: String, fundingInstrument: PaymentActivity.CardData){
        profileServiceAPI.saveUserCreditCard(userId, fundingInstrument)
    }

    fun setFavoriteUserFundingInstrumentToBackEnd(userId: String, cardId: String){
        profileServiceAPI.saveFavoriteCreditCard(userId, cardId)
    }

    fun getFavoriteUserFundingInstrumentFromBackEnd(userId: String) : Observable<String?> {
        return profileServiceAPI.getUserFavoriteCreditCard(userId)
    }

    fun loadUserProfileFromBackEnd(token: String): Observable<UserModel>{
        return profileServiceAPI.loadUserProfile(token)
    }
}