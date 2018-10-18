package com.cozo.cozomvp.userprofile

import com.cozo.cozomvp.paymentactivity.PaymentActivity
import com.google.android.gms.tasks.Task
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

class ProfileServiceImpl : IProfileService {

    private var model: ProfileServiceModel
    private var user : UserModel? = null
    private var favoriteCardMapping : MutableMap<String, Boolean> = mutableMapOf()

    constructor(baseUrl: String? = null) {
        model = ProfileServiceModel(baseUrl)
    }

    fun updateUserInfo(user:UserModel) {
        this.user = user
        this.user!!.fundingInstruments.forEach {
            favoriteCardMapping.put(it.cardId, false)
        }
    }

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

    override fun setAvatarUrl(image: File) {
        //send to back end storage
        val avatarUrl = model.uploadUserAvatarToStorage(user!!.ownId, image)
        val dispose = avatarUrl
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    //store locally in singleton
                    user!!.avatarUrl = it
                }
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
        favoriteCardMapping.put(fundingInstrument.cardId, false)
        if(user!!.fundingInstruments.size == 1){
            setFavoriteFundingInstrument(fundingInstrument.cardId)
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

    override fun loadUserProfile(token: String, callback: ProfileServiceListener) {
        //load userProfile from model
        val disposable = model.loadUserProfileFromBackEnd(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap {
                    updateUserInfo(it)
                    callback.onComplete(it)
                    model.getFavoriteUserFundingInstrumentFromBackEnd(it.ownId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }
                .subscribe(
                {
                    if(it != null) {
                        if (favoriteCardMapping.containsKey(it)) {
                            favoriteCardMapping.forEach {vl ->
                                if(vl.key != it && vl.value){
                                    favoriteCardMapping.remove(vl.key)
                                    favoriteCardMapping.put(vl.key, false)
                                } else if (vl.key == it && !vl.value){
                                    favoriteCardMapping.remove(vl.key)
                                    favoriteCardMapping.put(vl.key, true)
                                }
                            }
                        }
                    }
                },{
                    callback.onError()
                }
        )
    }

    companion object {

        private var myInstance:ProfileServiceImpl? = null

        fun getInstance (baseUrl: String? = null): ProfileServiceImpl {
            if (myInstance != null) {
                return myInstance!!
            }
            if(baseUrl == null) {
                myInstance = ProfileServiceImpl()
            } else {
                myInstance = ProfileServiceImpl(baseUrl)
            }
            return myInstance!!
        }

        fun createUserProfile(userId: String, email: String, countryCode: String, areaCode: String,
                              phoneNumber: String) : UserModel {

            //create user profile instance with userId/email/phone
            val userModel = UserModel(userId, email, Phone(countryCode, areaCode, phoneNumber), null,
                    null, mutableListOf<PaymentActivity.CardData>())

            //set and upload userModel to backend for further use
            getInstance().setUserProfile(userModel)

            return userModel
            }
    }

    interface ProfileServiceListener{
        fun onComplete(userProfile: UserModel)
        fun onError()
    }
}