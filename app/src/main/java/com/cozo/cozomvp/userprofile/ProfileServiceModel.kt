package com.cozo.cozomvp.userprofile

import android.graphics.Bitmap

class ProfileServiceModel {

    fun uploadUserProfileToBackEnd(userModel: UserModel) {}
    fun uploadUserAvatarToStorage(userId: String, image: Bitmap) : String {
        return ""
    }
    fun updateUserAvatarInBackEnd(userId: String, avatarUrl: String){}
    fun updateUserPaymentExternalIdToBackEnd(userId: String, externalId: String){}
    fun addUserFundingInstrumentToBackEnd(userId: String, fundingInstrument: XPTO){}
    fun setFavoriteUserFundingInstrumentToBackEnd(userId: String, cardId: String){}
}