package com.cozo.cozomvp.emptyactivity

import android.util.Log
import com.cozo.cozomvp.paymentapi.PaymentAPIService
import com.cozo.cozomvp.userprofile.ProfileServiceImpl
import com.cozo.cozomvp.userprofile.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EmptyPresenter(private var firebaseAuth: FirebaseAuth) : MvpBasePresenter<EmptyView>(),
        EmptyInterfaces.Presenter, ProfileServiceImpl.ProfileServiceListener {

    private var model = EmptyModel(firebaseAuth, null)
    val user : FirebaseUser? = firebaseAuth.currentUser

    override fun onCreatedInvoked(){
        retrieveAuthorizationToken()
    }

    override fun onComplete(userProfile: UserModel) {
        ifViewAttached {
            it.startMainActivity()
        }
    }

    override fun onError() {
        Log.d("DebugXPTO","Couldnt load profile from back end")
    }

    private fun retrieveAuthorizationToken(){
        val authToken = model.retrieveAuthorizationToken()
        val dispose = authToken
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {authToken ->

                            //storeAuthToken somewhere
                            PaymentAPIService.setHeader(authToken.encryptedToken)

                            //check if user is logged already. Start either AuthActivity or MainActivity accordingly
                            ifViewAttached {
                                if (user == null){
                                    it.startAuthActivity()
                                } else {
                                    // tries to refresh user data from Firebase servers. Forces user to login when refresh
                                    // fails, which means either Token is no longer valid or User has been deleted/disabled
                                    // from DB
                                    user.reload().addOnCompleteListener { mTask ->
                                        if (mTask.isSuccessful){
                                            //retrieve userProfile from backend
                                            user.getIdToken(true).addOnSuccessListener{
                                                ProfileServiceImpl.getInstance().loadUserProfile(it.token!!, this)
                                            }
                                        } else {
                                            it.startAuthActivity()
                                        }
                                    }
                                }
                            }
                        },
                        {

                        }
                )
    }

}