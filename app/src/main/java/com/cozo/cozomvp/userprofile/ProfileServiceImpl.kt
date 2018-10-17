package com.cozo.cozomvp.userprofile

import android.graphics.Bitmap

class ProfileServiceImpl : IProfileService {

    private var model = ProfileServiceModel()
    private var user : UserModel? = null
    private var favoriteCardMapping : MutableMap<String, Boolean> = mutableMapOf()

    override fun setUserProfile(userProfile: UserModel) : Boolean {
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
        model.updateUserAvatarInBackEnd(user!!.ownId, avatarUrl)

        //store locally in singleton
        user!!.avatarUrl = avatarUrl
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

    override fun setFundingInstrument(fundingInstrument: XPTO) {
        // send to back end dB
        model.addUserFundingInstrumentToBackEnd(user!!.ownId, XPTO)

        //store locally in singleton
        user!!.fundingInstruments.add(XPTO)
        if(user!!.fundingInstruments.size == 1){
            favoriteCardMapping.put(XPTO.cardId, true)
        } else {
            favoriteCardMapping.put(XPTO.cardId, false)
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

    override fun getFavoriteFundingInstrument(): XPTO? {
        favoriteCardMapping.forEach {
            if(it.value){
                user!!.fundingInstruments.forEach {card ->
                    if(card.cardId == it.key){
                        return card
                    }
                }
            }
        }
        return null
    }

    override fun getFundingInstruments(): List<XPTO> = user!!.fundingInstruments

    override fun getPaymentExternalId(): String? = user!!.paymentExternalId

    companion object {

        val myInstance = ProfileServiceImpl()

        fun createUserProfile(userId: String, email: String, countryCode: String, areaCode: String,
                              phoneNumber: String) : UserModel {

            //create user profile instance with userId/email/phone
            val userModel = UserModel(userId, email, Phone(countryCode, areaCode, phoneNumber), null,
                    null, mutableListOf<XPTO>())

            //upload userModel to backend for further use
            myInstance.model.uploadUserProfileToBackEnd(userModel)

            return userModel
            }
    }
}