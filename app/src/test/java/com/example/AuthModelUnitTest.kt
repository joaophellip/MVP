package com.example

import com.cozo.cozomvp.authentication.AuthActivity
import org.mockito.Mockito.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import com.cozo.cozomvp.authentication.PhoneValidationServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class AuthModelUnitTest{

    private val fakePhoneNumber = "11999999999"
    private val fakeFormattedPhoneNumber = "+5511999999999"
    private val timeOut: Long = 60
    private val timeUnit = TimeUnit.SECONDS
    private val fakeAuthActivity = AuthActivity()

    @Mock
    lateinit var mockedPhoneNumberUtil : PhoneNumberUtil
    lateinit var mockedPhoneAuthProvider : PhoneAuthProvider
    lateinit var mockedFirebaseAuth: FirebaseAuth

    @Test
    fun authModel_authenticateNumber(){

        `when`(mockedPhoneNumberUtil.parse(fakePhoneNumber,"BR")).thenReturn(fakeBrPhoneNumber)
        `when`(mockedPhoneAuthProvider.verifyPhoneNumber(fakeFormattedPhoneNumber,
                timeOut,
                timeUnit,
                fakeAuthActivity,
                callbacks)).thenReturn(Unit)
        `when`(mockedFirebaseAuth.signInWithCredential(authCredential)).thenReturn(authResultTask)

        val objectUnderTest = PhoneValidationServiceImpl(mockedPhoneNumberUtil, mockedPhoneAuthProvider, mockedFirebaseAuth)

    }

}