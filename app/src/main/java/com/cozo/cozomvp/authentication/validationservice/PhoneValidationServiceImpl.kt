package com.cozo.cozomvp.authentication.validationservice

import com.cozo.cozomvp.authentication.AuthActivity
import com.cozo.cozomvp.authentication.AuthModel
import com.cozo.cozomvp.emptyactivity.EmptyModel
import com.cozo.cozomvp.userprofile.ProfileServiceImpl
import com.cozo.cozomvp.userprofile.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.util.concurrent.TimeUnit

class PhoneValidationServiceImpl(private var phoneUtil: PhoneNumberUtil,
                                 private var phoneAuthProvider: PhoneAuthProvider,
                                 private var firebaseAuth: FirebaseAuth) : ValidationService,
        ProfileServiceImpl.ProfileServiceListener{

    private lateinit var emptyModel: EmptyModel

    override fun isThereALoggedUser(emptyModel: EmptyModel) {
        val user : FirebaseUser? = firebaseAuth.currentUser
        if (user == null){
            emptyModel.modelListener.noUserAvailable()
        } else {
            // tries to refresh user data from Firebase servers. Forces user to login when refresh
            // fails, which means either Token is no longer valid or User has been deleted/disabled
            // from DB
            user.reload().addOnCompleteListener { mTask ->
                if (mTask.isSuccessful){
                    //retrieve userProfile from backend
                    this.emptyModel = emptyModel
                    user.getIdToken(true).addOnSuccessListener{
                        ProfileServiceImpl.getInstance().loadUserProfile(it.token!!, this)
                    }
                } else {
                    emptyModel.modelListener.noUserAvailable()
                }
            }
        }
    }

    override fun getCurrentToken(): Task<GetTokenResult> {
        val user: FirebaseUser? = firebaseAuth.currentUser
        return user!!.getIdToken(true)
    }

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

                        //get country code (from user, not accountData, which doesn't have it here)
                        val brPhone: Phonenumber.PhoneNumber = phoneUtil.parse(user.phoneNumber, "BR")

                        // get user token
                        user.getIdToken(true).addOnSuccessListener{
                            // create userProfile inside ProfileService
                            ProfileServiceImpl.createUserProfile(it.token!!, user.displayName!!, user.email!!,
                                    brPhone.countryCode.toString(), user.phoneNumber!!.substring(3,5), user.phoneNumber!!.substring(5))
                        }
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
                            .addOnCompleteListener(AuthActivity(), object : OnCompleteListener<AuthResult> {
                                override fun onComplete(task: Task<AuthResult>) {
                                    if (task.isSuccessful) {
                                        val user: FirebaseUser? = firebaseAuth.currentUser
                                        when(user?.providers?.contains("google.com")) {
                                            true -> {
                                                // get user token
                                                user.getIdToken(true).addOnSuccessListener{
                                                    // create userProfile inside ProfileService
                                                    ProfileServiceImpl.createUserProfile(it.token!!, user.displayName!!, user.email!!,
                                                            brPhone.countryCode.toString(), signInData.phoneNumber.substring(3,5), signInData.phoneNumber.substring(5))
                                                }
                                                // fire callback
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
                            })
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

    override fun onComplete(userProfile: UserModel) {
        emptyModel.modelListener.userAvailable()
    }

    override fun onError() {}

    companion object {

        private var myInstance:PhoneValidationServiceImpl? = null

        fun getInstance(phoneUtil: PhoneNumberUtil? = null,
                        phoneAuthProvider: PhoneAuthProvider? = null,
                        firebaseAuth: FirebaseAuth? = null): PhoneValidationServiceImpl {
            if (myInstance != null) {
                return myInstance!!
            }
            myInstance = PhoneValidationServiceImpl(phoneUtil!!, phoneAuthProvider!!, firebaseAuth!!)
            return myInstance!!
        }
    }
}