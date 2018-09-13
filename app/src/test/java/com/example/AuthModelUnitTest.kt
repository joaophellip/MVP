package com.example

import android.os.Parcel
import com.cozo.cozomvp.authentication.AuthActivity
import com.google.firebase.auth.PhoneAuthCredential
import org.junit.Test
import org.mockito.Mock
import com.google.firebase.auth.PhoneAuthProvider
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Matchers.any
import org.mockito.Mockito.doAnswer
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeUnit
import android.os.Bundle
import com.cozo.cozomvp.authentication.AuthInterfaces
import com.cozo.cozomvp.authentication.AuthModel
import com.cozo.cozomvp.authentication.PhoneValidationServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import org.mockito.Matchers.anyString
import org.mockito.Mockito.`when`


@RunWith(RobolectricTestRunner::class)
class AuthModelUnitTest{

    /*
    In case test fails with exception NoClassDefFoundError , delete .gradle and .idle folders and
    rebuild project.
    Check: https://stackoverflow.com/questions/42805495/android-junit-dont-compile-if-objects-are-parcelable
     */
    @Mock
    lateinit var mockedPhoneAuthProvider : PhoneAuthProvider

    @Mock
    lateinit var mockedFirebaseAuth : FirebaseAuth

    @Mock
    lateinit var mockedPhoneNumberUtil: PhoneNumberUtil

    private var testPhoneNumber : String = "11945908279"

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun authModel_authenticateNumber_onAuthAndLinkedCompleted(){

        // Mock PhoneNumberUtil methods
        val fakeBrPhone = Phonenumber.PhoneNumber()
        `when`(mockedPhoneNumberUtil.parse(anyString(), anyString())).thenReturn(fakeBrPhone)
        `when`(mockedPhoneNumberUtil.isValidNumber(any(Phonenumber.PhoneNumber::class.java))).thenReturn(true)

        // Stub VerifyPhoneNumberCallback
        /*
        val callbackResultObject: PhoneAuthCredential = responseFromPhoneNumberCallback()
         */
        doAnswer {
            val callback = it.arguments[4] as PhoneVerificationCallback

            // triggers onVerificationCompleted with fake result object of type PhoneAuthCredential
            callback.onVerificationCompleted(null)
            //callback.onVerificationCompleted(callbackResultObject)

            return@doAnswer null
        }.`when`(mockedPhoneAuthProvider).verifyPhoneNumber(Matchers.anyString(),
                Matchers.anyLong(),
                any(TimeUnit::class.java),
                any(AuthActivity::class.java),
                any(PhoneVerificationCallback::class.java))

        // Setup SignInWithCredential

        // Setup ValidationService
        val validationService = PhoneValidationServiceImpl(mockedPhoneNumberUtil, mockedPhoneAuthProvider, mockedFirebaseAuth)

        // Setup onRequestListener
        val authListener = AuthListener()

        val authModel = AuthModel(validationService, authListener)
        authModel.authenticateNumber(testPhoneNumber)
        /*val authModel = AuthModel(any(PhoneValidationServiceImpl::class.java), any(AuthListener::class.java))
        authModel.authenticateNumber(anyString())
         */

    }

    /*
    In order to create a PhoneAuthCredential object, had to add robolectric library for creating a
    Parcel object, as part of callback result mocking
     */
    private fun responseFromPhoneNumberCallback() : PhoneAuthCredential{
        val parcel : Parcel = Parcel.obtain()
        val bundle = Bundle()

        bundle.putString("sessionInfo", "")
        bundle.putString("smsCode", "")
        bundle.putString("hasVerificationProof", "")
        bundle.putString("phoneNumber", "")
        bundle.putString("autoCreate", "")
        parcel.writeBundle(bundle)

        return PhoneAuthCredential.CREATOR.createFromParcel(parcel)
    }
}

abstract class PhoneVerificationCallback : PhoneAuthProvider.OnVerificationStateChangedCallbacks()

class AuthListener : AuthInterfaces.Presenter.OnRequestAuthListener{
    override fun onAuthAndLinkedCompleted() {}
    override fun onRequestLinkWithGoogleNeeded() {}
    override fun onAuthenticationFailed() {}
    override fun onInvalidNumber() {}
}