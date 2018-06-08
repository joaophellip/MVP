package com.cozo.cozomvp

import android.util.Log
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
    var mAuth = FirebaseAuth.getInstance()
    val TAG: String = "Authentication"

    override fun authenticateNumber(phoneNumber: String) {
        val phoneUtil = PhoneNumberUtil.getInstance()
        val brPhone: Phonenumber.PhoneNumber = phoneUtil.parse(phoneNumber, "BR")
        val isValid = phoneUtil.isValidNumber(brPhone)
        if (isValid) {
            val validPhoneNumber = "+" + brPhone.countryCode.toString() + brPhone.nationalNumber.toString()
            Log.e(TAG, "authmodel")

            val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Log.d(TAG, "onVerificationCompleted:$credential")
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w(TAG, "onVerificationFailed", e)
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        mOnRequestAuthListener.onAuthenticationFailed()
                    } else if (e is FirebaseTooManyRequestsException) {
                        mOnRequestAuthListener.onAuthenticationFailed()
                        // The SMS quota for the project has been exceeded
                        // ...
// TODO: work on this if statement above
                    }
                    // Show a message and update the UI
                    // ...
                }
                override fun onCodeSent(verificationId: String?,
                                        token: PhoneAuthProvider.ForceResendingToken?) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d(TAG, "onCodeSent:" + verificationId!!)

                    // Save verification ID and resending token so we can use them later
                    /*mVerificationId = verificationId
                    mResendToken = token*/
                    // ...
                }
            }
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    validPhoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    AuthActivity(),               // Activity (for callback binding)
                    mCallbacks)        // OnVerificationStateChangedCallbacks
        } else {
            mOnRequestAuthListener.onInvalidNumber()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(AuthActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")

//                          CHECK IF USER IS ALREADY LINKED WITH GOOGLE
                        // Check for existing Google Sign In account, if the user is already signed in
                        // the GoogleSignInAccount will be non-null.

                        val user = mAuth.currentUser
                        val providerData: MutableList<out UserInfo> = user!!.providerData
                        val mMapUserId: MutableMap<String, UserInfo> = mutableMapOf()

                        providerData.forEach{
                            mMapUserId[it.providerId] = it
                        }

                        when(mMapUserId.containsKey("google.com")) {
                            true -> {
                                mOnRequestAuthListener.onAuthAndLinkedCompleted()

                            }
                            false -> {
                                Log.d(TAG, "signInWithCredential:success 22" )
                                mOnRequestAuthListener.onRequestLinkWithGoogleNeeded()
                            }
                        }

                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            mOnRequestAuthListener.onAuthenticationFailed()
                        }
                    }
                }
    }

    override fun linkAccountWithGoogle(completedTask: Task<GoogleSignInAccount>) {
        handleSignInResult(completedTask)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val providerData: MutableList<out UserInfo> = mAuth.currentUser!!.providerData
        val user = mAuth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(account.displayName)
                .build()

        user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                    }
                }
        mAuth.currentUser!!.linkWithCredential(credential)
                .addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        mOnRequestSignInWithGoogleListener.onCompleted(providerData)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        mOnRequestSignInWithGoogleListener.onFailed()

                    }
                    // ...
                })
    }

    override fun signOutModel(mGoogleSignInClient: GoogleSignInClient, mAuth: FirebaseAuth) {
        mGoogleSignInClient.signOut()
        mAuth.signOut()

        mOnRequestSignOutListener.onCompleted()
    }
}