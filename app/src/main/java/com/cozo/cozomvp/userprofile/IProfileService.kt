package com.cozo.cozomvp.userprofile

import android.graphics.Bitmap
import com.cozo.cozomvp.paymentactivity.PaymentActivity

interface IProfileService {

    fun setUserProfile(userProfile: UserModel) : Boolean
    fun getUserProfile() : UserModel?
    fun setAvatarUrl(image: Bitmap)
    fun getAvatarUrl() : String?
    fun setPaymentExternalId(id: String)
    fun getPaymentExternalId() : String?
    fun setFundingInstrument(fundingInstrument: PaymentActivity.CardData)
    fun getFundingInstruments() : List<PaymentActivity.CardData>
    fun setFavoriteFundingInstrument(cardId: String) : Boolean
    fun getFavoriteFundingInstrument() : PaymentActivity.CardData?
}