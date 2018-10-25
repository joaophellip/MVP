package com.example

import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkapi.CreditCardData
import com.cozo.cozomvp.networkapi.SaveFavoriteCreditCardResponse
import com.cozo.cozomvp.paymentactivity.PaymentActivity
import com.cozo.cozomvp.userprofile.ProfileServiceAPI
import com.cozo.cozomvp.userprofile.UserModel
import com.google.gson.Gson
import io.reactivex.observers.TestObserver
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProfileServiceAPITest: TestBase() {

    lateinit var mockServer: MockWebServer
    lateinit var repository: ProfileServiceAPI

    @Before
    @Throws fun setUp() {
        mockServer = MockWebServer()
        val baseURL = mockServer.url("/").toString()

        repository = ProfileServiceAPI.create(baseURL)

    }


    @After
    @Throws fun tearDown() {
        // We're done with tests, shut it down
        mockServer.shutdown()
    }

    @Test
    fun testCreateUserProfile () {
        val testObserver = TestObserver<ResponseBody>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody("{}")
        mockServer.enqueue(mockResponse)

        val customer = Gson().fromJson<UserModel>(getJson(R.raw.user_model)
                , UserModel::class.java)

        repository.createUserProfile(customer).subscribe(testObserver)
        testObserver.assertNoErrors()
    }

    @Test
    fun testCreateUserProfileFull () {
        val testObserver = TestObserver<ResponseBody>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody("{}")
        mockServer.enqueue(mockResponse)

        val customer = Gson().fromJson<UserModel>(getJson(R.raw.user_model_full)
                , UserModel::class.java)
        repository.createUserProfile(customer).subscribe(testObserver)
        testObserver.assertNoErrors()
    }

    @Test
    fun testSaveUserCreditCardOnSuccess() {
        val testObserver = TestObserver<CreditCardData>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.save_credit_card_response))
        mockServer.enqueue(mockResponse)

        val customer = Gson().fromJson<UserModel>(getJson(R.raw.user_model_full)
                , UserModel::class.java)
        val creditCard = Gson().fromJson<PaymentActivity.CardData>(getJson(R.raw.credit_card), PaymentActivity.CardData::class.java)
        repository.saveUserCreditCard(customer.ownId, creditCard).subscribe(testObserver)
        val creditCardDataResponse = testObserver.values().get(0)

        Assert.assertEquals(2, creditCardDataResponse.creditCardList.size)
    }

    @Test
    fun testSaveUserCreditCardOnFail() {
        val testObserver = TestObserver<CreditCardData>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.save_credit_card_response))
        mockServer.enqueue(mockResponse)

        val customer = Gson().fromJson<UserModel>(getJson(R.raw.user_model_full)
                , UserModel::class.java)
        val creditCard = Gson().fromJson<PaymentActivity.CardData>(getJson(R.raw.credit_card), PaymentActivity.CardData::class.java)
        repository.saveUserCreditCard(customer.ownId, creditCard).subscribe(testObserver)
        val creditCardDataResponse = testObserver.values().get(0)

        Assert.assertNotEquals(1, creditCardDataResponse.creditCardList.size)
    }

    @Test
    fun testSaveFavoriteCreditCardOnSuccess () {
        val testObserver = TestObserver<SaveFavoriteCreditCardResponse>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.save_favorite_credit_card_response))
        mockServer.enqueue(mockResponse)

        val customer = Gson().fromJson<UserModel>(getJson(R.raw.user_model_full)
                , UserModel::class.java)
        val creditCard = Gson().fromJson<PaymentActivity.CardData>(getJson(R.raw.credit_card), PaymentActivity.CardData::class.java)
        repository.saveFavoriteCreditCard(customer.ownId, creditCard.cardId).subscribe(testObserver)
        val creditCardDataResponse = testObserver.values().get(0)

        Assert.assertEquals(creditCard.cardId, creditCardDataResponse.creditCardId)

    }

    @Test
    fun testSaveFavoriteCreditCardOnFail () {
        val testObserver = TestObserver<SaveFavoriteCreditCardResponse>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.save_favorite_credit_card_response))
        mockServer.enqueue(mockResponse)

        val customer = Gson().fromJson<UserModel>(getJson(R.raw.user_model_full)
                , UserModel::class.java)
        val creditCard = Gson().fromJson<PaymentActivity.CardData>(getJson(R.raw.credit_card), PaymentActivity.CardData::class.java)
        repository.saveFavoriteCreditCard(customer.ownId, creditCard.cardId).subscribe(testObserver)

        val creditCardDataResponse = testObserver.values().get(0)

        Assert.assertNotEquals(null, creditCardDataResponse.creditCardId)
    }

    @Test
    fun testLoadUserProfileOnSuccess() {
        val testObserver = TestObserver<UserModel>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_model_full))
        mockServer.enqueue(mockResponse)

        repository.loadUserProfile("abc123").subscribe(testObserver)

        val userModelResponse = testObserver.values().get(0)

        Assert.assertEquals("abc@123.com", userModelResponse.email)
    }

    @Test
    fun testLoadUserProfileOnFail() {
        val testObserver = TestObserver<UserModel>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_model_full))
        mockServer.enqueue(mockResponse)

        repository.loadUserProfile("abc123").subscribe(testObserver)

        val userModelResponse = testObserver.values().get(0)

        Assert.assertNotEquals(null, userModelResponse.email)
    }
}