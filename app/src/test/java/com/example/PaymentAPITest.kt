package com.example

import com.cozo.cozomvp.R
import com.cozo.cozomvp.paymentapi.*
import com.google.gson.Gson
import io.reactivex.observers.TestObserver
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PaymentAPITest: TestBase() {

    lateinit var mockServer: MockWebServer
    lateinit var repository: PaymentAPIService

    @Before @Throws fun setUp() {
        mockServer = MockWebServer()
        val baseURL = mockServer.url("/").toString()

        repository = PaymentAPIService.create(baseURL)

    }


    @After @Throws fun tearDown() {
        // We're done with tests, shut it down
        mockServer.shutdown()
    }

    @Test
    fun testClientResponse () {
        val testObserver = TestObserver<CustomerProfileResponse>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.client_response))
        mockServer.enqueue(mockResponse)

        val customer = Gson().fromJson<CustomerProfile>(getJson(R.raw.client_profile)
                ,CustomerProfile::class.java)
        repository.createCustomerProfile(customer).subscribe(testObserver)
        testObserver.assertNoErrors()
    }

    @Test
    fun testCreateFundingInstrument() {
        val testObserver = TestObserver<FundingInstrumentResponse>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.instrument_response))
        mockServer.enqueue(mockResponse)

        val instrument = Gson().fromJson<FundingInstrument>(getJson(R.raw.funding_instrument)
                ,FundingInstrument::class.java)
        repository.createFundingInstrument(instrument,"1234").subscribe(testObserver)
        testObserver.assertNoErrors()
    }

    @Test
    fun testDeleteFundingInstrument() {
        val testObserver = TestObserver<ResponseBody>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody("{}")
        mockServer.enqueue(mockResponse)

        repository.deleteFundingInstrument("abc1234").subscribe(testObserver)

        testObserver.assertNoErrors()
    }

    @Test
    fun testCheckHeaderDefaultTokenUsingDeleteMethod(){
        val testObserver = TestObserver<ResponseBody>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody("{}")
        mockServer.enqueue(mockResponse)

        repository.deleteFundingInstrument("abc1234").subscribe(testObserver)

        val takeRequest = mockServer.takeRequest()

        Assert.assertEquals("Basic no token", takeRequest.getHeader("Authorization"))
    }

    @Test
    fun testCheckHeaderCustomTokenUsingDeleteMethod(){
        val baseURL = mockServer.url("/").toString()
        PaymentAPIService.setHeader("r6rdiydriydfd==")
        val repository = PaymentAPIService.create(baseURL)
        val testObserver = TestObserver<ResponseBody>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody("{}")
        mockServer.enqueue(mockResponse)

        repository.deleteFundingInstrument("abc1234").subscribe(testObserver)

        val takeRequest = mockServer.takeRequest()

        Assert.assertNotEquals("Basic no token", takeRequest.getHeader("Authorization"))
    }
}