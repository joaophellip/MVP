package com.cozo.cozomvp.authentication

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.FirebaseAuth
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.util.concurrent.TimeUnit

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
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            mValidationService.signUserOut(this)
        }
    }
}

interface ValidationService{
    fun signUserIn(signInData: ValidationData, authModel: AuthModel)
    fun linkWithAccount(accountData: ValidationData, authModel: AuthModel)
    fun signUserOut(authModel: AuthModel)
}

class PhoneValidationServiceImpl : ValidationService{

    constructor(phoneUtil: PhoneNumberUtil, phoneAuthProvider: PhoneAuthProvider, firebaseAuth: FirebaseAuth){
        this.phoneUtil = phoneUtil
        this.phoneAuthProvider = phoneAuthProvider
        this.firebaseAuth = firebaseAuth
    }

    private var phoneUtil : PhoneNumberUtil
    private var phoneAuthProvider : PhoneAuthProvider
    private var firebaseAuth : FirebaseAuth

    override fun linkWithAccount(accountData: ValidationData, authModel: AuthModel) {

        val credential : AuthCredential = GoogleAuthProvider.getCredential(accountData.account.idToken, null)
        val providerData: MutableList<out UserInfo> = firebaseAuth.currentUser!!.providerData
        val user: FirebaseUser? = firebaseAuth.currentUser
        val profileUpdates: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                .setDisplayName(accountData.account.displayName)
                .setPhotoUri(accountData.account.photoUrl)
                .build()

        user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                    }
                }

        firebaseAuth.currentUser!!.linkWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        authModel.mOnRequestSignInWithGoogleListener.onCompleted(providerData)
                    } else {
                        authModel.mOnRequestSignInWithGoogleListener.onFailed()
                    }
                }
    }

    override fun signUserIn(signInData: ValidationData, authModel : AuthModel) {
        val brPhone: Phonenumber.PhoneNumber = phoneUtil.parse(signInData.phoneNumber, "BR")
        val isValid = phoneUtil.isValidNumber(brPhone)
        if (isValid) {
            val validPhoneNumber = "+" + brPhone.countryCode.toString() + brPhone.nationalNumber.toString()
            val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    firebaseAuth.signInWithCredential(credential)
                            .addOnCompleteListener(AuthActivity()) { task ->
                                if (task.isSuccessful) {
                                    val user: FirebaseUser? = firebaseAuth.currentUser
                                    when(user?.providers?.contains("google.com")) {
                                        true -> {
                                            authModel.mOnRequestAuthListener.onAuthAndLinkedCompleted()
                                        }
                                        false -> {
                                            authModel.mOnRequestAuthListener.onRequestLinkWithGoogleNeeded()
                                        }
                                    }

                                } else {
                                    // Sign in failed, display a message and update the UI
                                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                        authModel.mOnRequestAuthListener.onAuthenticationFailed()
                                    }
                                }
                            }


                }
                override fun onVerificationFailed(e: FirebaseException) {
                    when (e) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            // Invalid request
                            authModel.mOnRequestAuthListener.onAuthenticationFailed()
                        }
                        is FirebaseTooManyRequestsException -> {
                            authModel.mOnRequestAuthListener.onAuthenticationFailed()
                            // The SMS quota for the project has been exceeded
                            // ...
                        }
                        is FirebaseAuthException -> {
                            // The app is not authorized to use Firebase Authentication
                        }
                    }
                    // Show a message and update the UI
                }
                override fun onCodeSent(verificationId: String?,
                                        token: PhoneAuthProvider.ForceResendingToken?) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.

                    // Save verification ID and resending token so we can use them later
                    /*mVerificationId = verificationId
                    mResendToken = token*/
                    // ...
                }
            }
            phoneAuthProvider.verifyPhoneNumber(
                    validPhoneNumber,       // Phone number to verify
                    60,                     // Timeout duration
                    TimeUnit.SECONDS,       // Unit of timeout
                    AuthActivity(),         // Activity (for callback binding)
                    mCallbacks)             // OnVerificationStateChangedCallbacks

        } else {
            authModel.mOnRequestAuthListener.onInvalidNumber()
        }
    }

    override fun signUserOut(authModel: AuthModel) {
        firebaseAuth.signOut()
        authModel.mOnRequestSignInWithGoogleListener.onFailed()
    }
}

open class ValidationData{

    constructor(phoneNumber : String){
        this.phoneNumber = phoneNumber
    }
    constructor(account: GoogleSignInAccount){
        this.account = account
    }

    lateinit var phoneNumber : String
    lateinit var account : GoogleSignInAccount
}