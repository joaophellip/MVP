package com.cozo.cozomvp.authentication

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class AuthPresenter : MvpBasePresenter<AuthView>(), AuthInterfaces.Presenter {

    private lateinit var mAuthModel: AuthModel

    override fun requestAuth(phoneNumber: String) {
        ifViewAttached {
            it.showLoading() }

        mAuthModel = AuthModel(object : AuthInterfaces.Presenter.OnRequestAuthListener {
            override fun onAuthAndLinkedCompleted() {
                ifViewAttached {
                    it.showMainActivity()
                }
            }
            override fun onRequestLinkWithGoogleNeeded() {
                ifViewAttached {
                    it.startLinkWithGoogle()
                }
//                TODO: check params
            }
            override fun onAuthenticationFailed() {
                ifViewAttached {
                    it.showAuthenticationFail()
                }
            }
            override fun onInvalidNumber() {
                ifViewAttached {
                    it.showInvalidNumber()
                }
            }
        })

        mAuthModel.authenticateNumber(phoneNumber)
    }

    override fun requestLinkWithGoogle(task: Task<GoogleSignInAccount>) {

        mAuthModel = AuthModel(object : AuthInterfaces.Presenter.OnRequestSignInWithGoogleListener {
            override fun onCompleted(providerData: MutableList<out UserInfo>) {

                val mMapUserId: MutableMap<String, UserInfo> = mutableMapOf()

                providerData.forEach {
                    mMapUserId[it.providerId] = it
                }

                ifViewAttached {
                    it.showMainActivity()
                }
            }

            override fun onFailed() {
                ifViewAttached {
                    it.showLinkFail()
                }
            }
        })
        mAuthModel.linkAccountWithGoogle(task)
    }

    override fun requestSignOut(mGoogleSignInClient: GoogleSignInClient, mAuth: FirebaseAuth) {

        ifViewAttached {
            it.showLoading()
        }

        mAuthModel = AuthModel(object : AuthInterfaces.Presenter.OnRequestSignOutListener {
            override fun onCompleted() {
                ifViewAttached {
                    it.showLogOffActivity()
                }
            }

            override fun onFailed() {
                ifViewAttached {
                    it.showAuthenticationFail()
                }
            }
//            Todo: onFailed() Is Never Used.
        })
        mAuthModel.signOutModel(mGoogleSignInClient, mAuth)
    }

}