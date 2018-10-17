package com.cozo.cozomvp.userprofile

import android.graphics.Bitmap
import com.cozo.cozomvp.paymentactivity.PaymentActivity

class ProfileServiceImpl : IProfileService {

    private var model = ProfileServiceModel()
    private var user : UserModel? = null
    private var favoriteCardMapping : MutableMap<String, Boolean> = mutableMapOf()

    override fun setUserProfile(userProfile: UserModel) : Boolean {
        //send to back end DB
        model.uploadUserProfileToBackEnd(userProfile)

        user = userProfile
        return true
    }

    override fun getUserProfile(): UserModel? {
        return if (user!=null){
            user
        } else {
            // return null to external class which will then be able to check with backend if user had been logged in and disconnected or if app in invite mode
            null
        }
    }

    override fun setAvatarUrl(image: Bitmap) {
        //send to back end storage
        val avatarUrl = model.uploadUserAvatarToStorage(user!!.ownId, image)
        val dispose = avatarUrl.subscribe {
            //store locally in singleton
            user!!.avatarUrl = it

        }

        //model.updateUserAvatarInBackEnd(user!!.ownId, avatarUrl)
    }

    override fun getAvatarUrl(): String? {
        return if (user != null){
            user!!.avatarUrl
        } else {
            null
        }
    }

    override fun setPaymentExternalId(id: String) {
        //send to back end DB
        model.updateUserPaymentExternalIdToBackEnd(user!!.ownId, id)

        //store locally in singleton
        user!!.paymentExternalId = id
    }

    override fun setFundingInstrument(fundingInstrument: PaymentActivity.CardData) {
        // send to back end dB
        model.addUserFundingInstrumentToBackEnd(user!!.ownId, fundingInstrument)

        //store locally in singleton
        user!!.fundingInstruments.add(fundingInstrument)
        if(user!!.fundingInstruments.size == 1){
            favoriteCardMapping.put(fundingInstrument.cardId, true)
        } else {
            favoriteCardMapping.put(fundingInstrument.cardId, false)
        }
    }

    override fun setFavoriteFundingInstrument(cardId: String) : Boolean {
        // send to back end dB
        model.setFavoriteUserFundingInstrumentToBackEnd(user!!.ownId, cardId)

        //store locally in singleton
        if (favoriteCardMapping.containsKey(cardId)) {
            favoriteCardMapping.forEach {
                if(it.key != cardId && it.value){
                    favoriteCardMapping.remove(it.key)
                    favoriteCardMapping.put(it.key, false)
                } else if (it.key == cardId && !it.value){
                    favoriteCardMapping.remove(it.key)
                    favoriteCardMapping.put(it.key, true)
                }
            }
            return true
        } else {
            return false
        }
    }

    override fun getFavoriteFundingInstrument(): PaymentActivity.CardData? {
        favoriteCardMapping.forEach {
            if(it.value){
                user!!.fundingInstruments.forEach {card : PaymentActivity.CardData ->
                    if(card.cardId == it.key){
                        return card
                    }
                }
            }
        }
        return null
    }

    override fun getFundingInstruments(): List<PaymentActivity.CardData> = user!!.fundingInstruments

    override fun getPaymentExternalId(): String? = user!!.paymentExternalId

    companion object {

        val myInstance = ProfileServiceImpl()

        fun createUserProfile(userId: String, email: String, countryCode: String, areaCode: String,
                              phoneNumber: String) : UserModel {

            //create user profile instance with userId/email/phone
            val userModel = UserModel(userId, email, Phone(countryCode, areaCode, phoneNumber), null,
                    null, mutableListOf<PaymentActivity.CardData>())

            //set and upload userModel to backend for further use
            myInstance.setUserProfile(userModel)

            return userModel
            }
    }
}