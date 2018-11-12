package com.cozo.cozomvp.authentication

import com.cozo.cozomvp.authentication.validationservice.ValidationData
import com.cozo.cozomvp.authentication.validationservice.ValidationService
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class AuthModel : AuthInterfaces.Model {

    constructor(validationService: ValidationService, onRequestAuthListener: AuthInterfaces.Presenter.OnRequestAuthListener) {
        this.mOnRequestAuthListener = onRequestAuthListener
        this.mValidationService = validationService
    }
    constructor(validationService: ValidationService, onRequestSignOutListener: AuthInterfaces.Presenter.OnRequestSignOutListener) {
        this.mOnRequestSignOutListener = onRequestSignOutListener
        this.mValidationService = validationService
    }
    constructor(validationService: ValidationService, onRequestSignInWithGoogleListener: AuthInterfaces.Presenter.OnRequestSignInWithGoogleListener) {
        this.mOnRequestSignInWithGoogleListener = onRequestSignInWithGoogleListener
        this.mValidationService = validationService
    }

    lateinit var mOnRequestAuthListener: AuthInterfaces.Presenter.OnRequestAuthListener
    lateinit var mOnRequestSignOutListener: AuthInterfaces.Presenter.OnRequestSignOutListener
    lateinit var mOnRequestSignInWithGoogleListener: AuthInterfaces.Presenter.OnRequestSignInWithGoogleListener
    private var mValidationService: ValidationService

    override fun authenticateNumber(phoneNumber: String) {
        val signInData = ValidationData(phoneNumber)
        mValidationService.signUserIn(signInData, this)
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
        mValidationService.linkWithAccount(accountData, this)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            mValidationService.signUserOut(this)
        }
    }
}
