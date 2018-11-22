package com.cozo.cozomvp.authentication

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo

interface AuthInterfaces {

    interface Model {
        fun authenticateNumber(phoneNumber: String)
        fun linkAccountWithGoogle(completedTask: Task<GoogleSignInAccount>)
        fun signOutModel(mGoogleSignInClient: GoogleSignInClient, mAuth: FirebaseAuth)
        fun authenticateWithSmsCode(smsCode: String)
    }

    interface Presenter {
        fun requestAuth(phoneNumber: String)
        fun requestLinkWithGoogle(task: Task<GoogleSignInAccount>)
        fun requestSignOut(mGoogleSignInClient: GoogleSignInClient, mAuth: FirebaseAuth)
        fun onSmsCodeEntered(smsCode: String)

        interface OnRequestAuthListener {
            fun smsAutoRetrievalTimedOut()
            fun onAuthAndLinkedCompleted()
            fun onRequestLinkWithGoogleNeeded()
            fun onAuthenticationFailed()
            fun onInvalidNumber()
        }

        interface OnRequestSignInWithGoogleListener {
            fun onCompleted(providerData: MutableList<out UserInfo>)
            fun onFailed()
        }

        interface OnRequestSignOutListener {
            fun onCompleted()
            fun onFailed()
        }
    }
}