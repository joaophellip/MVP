package com.cozo.cozomvp.authentication

import com.cozo.cozomvp.authentication.validationservice.PhoneValidationServiceImpl
import com.cozo.cozomvp.authentication.validationservice.ValidationData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class AuthModel : AuthInterfaces.Model {

    constructor(onRequestAuthListener: AuthInterfaces.Presenter.OnRequestAuthListener) {
        this.mOnRequestAuthListener = onRequestAuthListener
    }

    constructor(onRequestSignOutListener: AuthInterfaces.Presenter.OnRequestSignOutListener) {
        this.mOnRequestSignOutListener = onRequestSignOutListener
    }

    constructor(onRequestSignInWithGoogleListener: AuthInterfaces.Presenter.OnRequestSignInWithGoogleListener) {
        this.mOnRequestSignInWithGoogleListener = onRequestSignInWithGoogleListener
    }

    lateinit var mOnRequestAuthListener: AuthInterfaces.Presenter.OnRequestAuthListener
    lateinit var mOnRequestSignOutListener: AuthInterfaces.Presenter.OnRequestSignOutListener
    lateinit var mOnRequestSignInWithGoogleListener: AuthInterfaces.Presenter.OnRequestSignInWithGoogleListener

    override fun authenticateNumber(phoneNumber: String) {
        val signInData = ValidationData(phoneNumber)
        PhoneValidationServiceImpl.getInstance().signUserIn(signInData, this)
    }

    override fun linkAccountWithGoogle(completedTask: Task<GoogleSignInAccount>) {
        handleSignInResult(completedTask)
    }

    override fun signOutModel(mGoogleSignInClient: GoogleSignInClient, mAuth: FirebaseAuth) {
        mGoogleSignInClient.signOut()
        mAuth.signOut()
        mOnRequestSignOutListener.onCompleted()

    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val accountData = ValidationData(account)
        PhoneValidationServiceImpl.getInstance().linkWithAccount(accountData, this)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            PhoneValidationServiceImpl.getInstance().signUserOut(this)
        }
    }
}
