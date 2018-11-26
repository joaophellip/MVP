package com.example

/*import com.cozo.cozomvp.authentication.AuthActivity
import com.google.firebase.auth.PhoneAuthCredential
import org.junit.Test
import org.mockito.Mock
import com.google.firebase.auth.PhoneAuthProvider
import org.junit.Before
import org.mockito.Matchers
import org.mockito.Matchers.any
import org.mockito.Mockito.doAnswer
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit
import com.cozo.cozomvp.authentication.AuthInterfaces
import com.cozo.cozomvp.authentication.AuthModel
import com.cozo.cozomvp.authentication.validationservice.PhoneValidationServiceImpl
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import org.junit.runner.RunWith
import org.mockito.Matchers.anyString
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuthModelUnitTest{

    /*
    In case test fails with exception NoClassDefFoundError , delete .gradle and .idle folders and
    rebuild project.
    Check: https://stackoverflow.com/questions/42805495/android-junit-dont-compile-if-objects-are-parcelable
     */
    @Mock lateinit var mockedPhoneAuthProvider : PhoneAuthProvider
    @Mock lateinit var mockedFirebaseAuth : FirebaseAuth
    @Mock lateinit var mockedPhoneNumberUtil: PhoneNumberUtil
    @Mock lateinit var mockedCredential: PhoneAuthCredential
    @Mock lateinit var mockedTask : Task<AuthResult>
    private var testPhoneNumber : String = "11945908279"

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun authModel_authenticateNumber_onAuthAndLinkedCompleted(){

        // Mock methods parse and isValidNumber from PhoneNumberUtil
        val fakeBrPhone = Phonenumber.PhoneNumber()
        `when`(mockedPhoneNumberUtil.parse(anyString(), anyString())).thenReturn(fakeBrPhone)
        `when`(mockedPhoneNumberUtil.isValidNumber(any(Phonenumber.PhoneNumber::class.java))).thenReturn(true)

        // Stub OnVerificationStateChangedCallbacks
        doAnswer {
            val callback = it.arguments[4] as PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // triggers onVerificationCompleted with fake result object of type PhoneAuthCredential
            callback.onVerificationCompleted(mockedCredential)

            return@doAnswer null
        }.`when`(mockedPhoneAuthProvider).verifyPhoneNumber(Matchers.anyString(),
                Matchers.anyLong(),
                any(TimeUnit::class.java),
                any(AuthActivity::class.java),
                any(PhoneAuthProvider.OnVerificationStateChangedCallbacks::class.java))

        // Mock method signInWithCredential from FirebaseAuth
        `when`(mockedFirebaseAuth.signInWithCredential(any(PhoneAuthCredential::class.java))).thenReturn(mockedTask)

        //
        doAnswer {
            val callback = it.arguments[1] as OnCompleteListener<AuthResult>

            callback.onComplete(mockedTask)

            return@doAnswer null
        }.`when`(mockedTask).addOnCompleteListener(any(AuthActivity::class.java), any())

        // Instantiate validationService as PhoneValidationServiceImpl
        val validationService = PhoneValidationServiceImpl(mockedPhoneNumberUtil, mockedPhoneAuthProvider, mockedFirebaseAuth)

        // Instantiate authListener
        val authListener = AuthListener()

        val authModel = AuthModel(validationService, authListener)
        authModel.authenticateNumber(testPhoneNumber)

        //read for Monday https://www.baeldung.com/mockito-spy
    }

    class AuthListener : AuthInterfaces.Presenter.OnRequestAuthListener{
        override fun onAuthAndLinkedCompleted() {}
        override fun onRequestLinkWithGoogleNeeded() {}
        override fun onAuthenticationFailed() {}
        override fun onInvalidNumber() {}
    }
}
        */