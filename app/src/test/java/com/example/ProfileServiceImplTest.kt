package com.example

import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkapi.CreditCardData
import com.cozo.cozomvp.networkapi.SaveFavoriteCreditCardResponse
import com.cozo.cozomvp.paymentactivity.PaymentActivity
import com.cozo.cozomvp.userprofile.ProfileServiceImpl
import com.cozo.cozomvp.userprofile.UserModel
import com.google.gson.Gson
import io.reactivex.observers.TestObserver
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class ProfileServiceImplTest: TestBase() {

    lateinit var mockServer: MockWebServer
    lateinit var repository: ProfileServiceImpl

    @Before
    @Throws fun setUp() {
        mockServer = MockWebServer()
        val baseURL = mockServer.url("/").toString()

        repository = ProfileServiceImpl.getInstance(baseURL)

    }


    @After
    @Throws fun tearDown() {
        // We're done with tests, shut it down
        mockServer.shutdown()
    }

    @Test
    fun testCreateUserProfile () {

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody("{}")
        mockServer.enqueue(mockResponse)

        ProfileServiceImpl.createUserProfile("abc123","abc@123.com","55","11", "9999-9999")
        Assert.assertEquals(repository.getUserProfile()?.ownId,"abc123")
    }

    @Test
    fun testLoadUserProfile () {
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_model_full))
        mockServer.enqueue(mockResponse)

        val mockResponse2 = MockResponse()
        mockResponse2.setResponseCode(200)
        mockResponse2.setBody("{}")
        mockServer.enqueue(mockResponse2)

        repository.loadUserProfile("abc123",object : ProfileServiceImpl.ProfileServiceListener {
            override fun onComplete(userProfile: UserModel) {
                Assert.assertEquals("abc123", userProfile.ownId)
            }

            override fun onError() {
                Assert.fail()
            }

        })
    }

}