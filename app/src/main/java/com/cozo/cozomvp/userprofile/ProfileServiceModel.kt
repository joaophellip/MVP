package com.cozo.cozomvp.userprofile

import android.app.TaskStackBuilder
import android.graphics.Bitmap
import com.cozo.cozomvp.paymentactivity.PaymentActivity
import com.google.android.gms.tasks.Task
import io.reactivex.Observable
import io.reactivex.internal.operators.observable.ObservableCreate
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileServiceModel {

    private val profileServiceAPI = ProfileServiceAPI.create()

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

    fun loadUserProfileFromBackEnd(token: String): Observable<UserModel>{
        return profileServiceAPI.loadUserProfile(token)
    }
}