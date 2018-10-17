package com.cozo.cozomvp.userprofile

import com.cozo.cozomvp.paymentactivity.PaymentActivity
import com.google.android.gms.tasks.Task
import java.io.File

interface IProfileService {

    fun setUserProfile(userProfile: UserModel) : Boolean
    fun getUserProfile() : UserModel?
    fun setAvatarUrl(image: File)
    fun getAvatarUrl() : String?
    fun setPaymentExternalId(id: String)
    fun getPaymentExternalId() : String?
    fun setFundingInstrument(fundingInstrument: PaymentActivity.CardData)
    fun getFundingInstruments() : List<PaymentActivity.CardData>
    fun setFavoriteFundingInstrument(cardId: String) : Boolean
    fun getFavoriteFundingInstrument() : PaymentActivity.CardData?
    fun loadUserProfile(token: String, callback: ProfileServiceImpl.ProfileServiceListener)
}