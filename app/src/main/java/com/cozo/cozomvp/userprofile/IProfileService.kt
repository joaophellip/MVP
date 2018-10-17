package com.cozo.cozomvp.userprofile

import android.graphics.Bitmap

interface IProfileService {

    fun setUserProfile(userProfile: UserModel) : Boolean
    fun getUserProfile() : UserModel?
    fun setAvatarUrl(image: Bitmap)
    fun getAvatarUrl() : String?
    fun setPaymentExternalId(id: String)
    fun getPaymentExternalId() : String?
    fun setFundingInstrument(fundingInstrument: XPTO)
    fun getFundingInstruments() : List<XPTO>
    fun setFavoriteFundingInstrument(cardId: String) : Boolean
    fun getFavoriteFundingInstrument() : XPTO
}