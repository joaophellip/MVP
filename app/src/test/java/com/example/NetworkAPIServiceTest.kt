package com.example

import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkapi.*
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NetworkAPIServiceTest : TestBase(){

    lateinit var mockServer: MockWebServer
    lateinit var repository: APIServices

    @Before @Throws fun setUp() {
        mockServer = MockWebServer()
        val baseURL = mockServer.url("/").toString()

        repository = APIServices.create(baseURL)
    }

    @After @Throws fun tearDown() {
        // We're done with tests, shut it down
        mockServer.shutdown()
    }

    @Test
    fun testUserMainLocation() {
        val testObserver =  TestObserver<NetworkModel.Location>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_main_location_response))
        mockServer.enqueue(mockResponse)

        repository.userMainLocation("abc123").subscribe(testObserver)
        //testObserver.assertNoErrors()
        val location = testObserver.values().get(0)

        testObserver.assertNoErrors()
    }

    @Test
    fun testUserMainLocationLatitudeOnSuccess() {
        val testObserver =  TestObserver<NetworkModel.Location>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_main_location_response))
        mockServer.enqueue(mockResponse)

        repository.userMainLocation("abc123").subscribe(testObserver)
        val location = testObserver.values().get(0)

        Assert.assertEquals(-23.5595116, location.latitude, 0.0)
    }


    @Test
    fun testUserMainLocationLongitudeOnSuccess() {
        val testObserver =  TestObserver<NetworkModel.Location>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_main_location_response))
        mockServer.enqueue(mockResponse)

        repository.userMainLocation("abc123").subscribe(testObserver)
        //testObserver.assertNoErrors()
        val location = testObserver.values().get(0)

        Assert.assertEquals(-46.731304, location.longitude, 0.0)
    }

    @Test
    fun testUserMainLocationLatitudeOnFail() {
        val testObserver =  TestObserver<NetworkModel.Location>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_main_location_response))
        mockServer.enqueue(mockResponse)

        repository.userMainLocation("abc123").subscribe(testObserver)
        //testObserver.assertNoErrors()
        val location = testObserver.values().get(0)

        Assert.assertNotEquals(0.0, location.latitude, 0.0)
    }

    @Test
    fun testUserMainLocationLongitudeOnFail() {
        val testObserver =  TestObserver<NetworkModel.Location>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_main_location_response))
        mockServer.enqueue(mockResponse)

        repository.userMainLocation("abc123").subscribe(testObserver)
        //testObserver.assertNoErrors()
        val location = testObserver.values().get(0)

        Assert.assertNotEquals(0.0, location.longitude, 0.0)
    }

    @Test
    fun testUserReverseGeocoding(){
        val testObserver =  TestObserver<UserReverseGeocodingResponse>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_reverse_geocoding_response))
        mockServer.enqueue(mockResponse)

        repository.userReverseGeocoding("abc123", -23.5595116, -46.731304).subscribe(testObserver)
        testObserver.assertNoErrors()
    }

    @Test
    fun testUserReverseGeocodingFormattedAddressOnSuccess() {
        val testObserver = TestObserver<UserReverseGeocodingResponse>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_reverse_geocoding_response))
        mockServer.enqueue(mockResponse)

        repository.userReverseGeocoding("abc123", -23.5595116, -46.731304).subscribe(testObserver)
        val response = testObserver.values().get(0)

        Assert.assertEquals("Rua do Matão, 1010", response.formattedAddress)
    }

    @Test
    fun testUserReverseGeocodingFormattedAddressOnFail() {
        val testObserver = TestObserver<UserReverseGeocodingResponse>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_reverse_geocoding_response))
        mockServer.enqueue(mockResponse)

        repository.userReverseGeocoding("abc123", -23.5595116, -46.731304).subscribe(testObserver)
        val response = testObserver.values().get(0)

        Assert.assertNotEquals("Av. Prof. Luciano Gualberto, 380", response.formattedAddress)
    }

   /*
   @Path("id") id: String, @Query("userLatitude") userLatitude: Double,
                                @Query("userLongitude") userLongitude: Double,
                                @Query("restLatitude") restLatitude: Double,
                                @Query("restLongitude") restLongitude: Double
   Observable<UserPreviewDeliveryInfoResponse>
    */
    @Test
    fun testUserPreviewDeliveryInfo(){
       val testObserver =  TestObserver<UserPreviewDeliveryInfoResponse>()

       val mockResponse = MockResponse()
       mockResponse.setResponseCode(200)
       mockResponse.setBody(getJson(R.raw.user_preview_delivery_info_response))
       mockServer.enqueue(mockResponse)

       repository.userPreviewDeliveryInfo("abc123",
               -23.5595116, -46.731304,
               -23.560632, -46.721948).subscribe(testObserver)
       testObserver.assertNoErrors()
   }

    @Test
    fun testUserPreviewDeliveryInfoMinTimeOnSuccess(){
        val testObserver =  TestObserver<UserPreviewDeliveryInfoResponse>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_preview_delivery_info_response))
        mockServer.enqueue(mockResponse)

        repository.userPreviewDeliveryInfo("abc123",
                -23.5595116, -46.731304,
                -23.560632, -46.721948).subscribe(testObserver)

        val response = testObserver.values().get(0)
        Assert.assertEquals(15, response.minTime)
    }

    @Test
    fun testUserPreviewDeliveryInfoMaxTimeOnSuccess(){
        val testObserver =  TestObserver<UserPreviewDeliveryInfoResponse>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_preview_delivery_info_response))
        mockServer.enqueue(mockResponse)

        repository.userPreviewDeliveryInfo("abc123",
                -23.5595116, -46.731304,
                -23.560632, -46.721948).subscribe(testObserver)

        val response = testObserver.values().get(0)
        Assert.assertEquals(30, response.maxTime)
    }

    @Test
    fun testUserPreviewDeliveryInfoMinPriceOnSuccess(){
        val testObserver =  TestObserver<UserPreviewDeliveryInfoResponse>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_preview_delivery_info_response))
        mockServer.enqueue(mockResponse)

        repository.userPreviewDeliveryInfo("abc123",
                -23.5595116, -46.731304,
                -23.560632, -46.721948).subscribe(testObserver)

        val response = testObserver.values().get(0)
        Assert.assertEquals(0.0f, response.minPrice, 0.0f)
    }

    @Test
    fun testUserPreviewDeliveryInfoMaxPriceOnSuccess(){
        val testObserver =  TestObserver<UserPreviewDeliveryInfoResponse>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.user_preview_delivery_info_response))
        mockServer.enqueue(mockResponse)

        repository.userPreviewDeliveryInfo("abc123",
                -23.5595116, -46.731304,
                -23.560632, -46.721948).subscribe(testObserver)

        val response = testObserver.values().get(0)
        Assert.assertEquals(7.26f, response.maxPrice, 0.0f)
    }

    /*
    @Query("radius") radius: Int,
                                   @Query("latitude") latitude: Double,
                                   @Query("longitude") longitude: Double):
            Observable<NetworkModel.ListNearestRestaurantsLocation>
     */
    @Test
    fun testRestaurantsNearestLocation(){
        val testObserver =  TestObserver<NetworkModel.ListNearestRestaurantsLocation>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.restaurants_nearest_location_response))
        mockServer.enqueue(mockResponse)

        repository.restaurantsNearestLocation(100,
                -23.5595116, -46.731304).subscribe(testObserver)
        testObserver.assertNoErrors()
    }

   /*
   @Query("radius") radius: Int,
                               @Query("latitude") latitude: Double,
                               @Query("longitude") longitude: Double):
            Observable<NetworkModel.ListRestaurantItem>
    */
   @Test
   fun testRestaurantsNearestMenu(){
       val testObserver =  TestObserver<NetworkModel.ListRestaurantItem>()

       val mockResponse = MockResponse()
       mockResponse.setResponseCode(200)
       mockResponse.setBody(getJson(R.raw.restaurants_nearest_menu_response))
       mockServer.enqueue(mockResponse)

       repository.restaurantsNearestMenu(100,
               -23.5595116, -46.731304).subscribe(testObserver)
       testObserver.assertNoErrors()
   }

    /*
    (@Query("restaurantID") restaurantID: String):
            Observable<NetworkModel.ListRestaurantItem>
     */
    @Test
    fun testRestaurantsItems(){
        val testObserver =  TestObserver<NetworkModel.ListRestaurantItem>()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(getJson(R.raw.restaurants_items_response))
        mockServer.enqueue(mockResponse)

        repository.restaurantsNearestMenu(100,
                -23.5595116, -46.731304).subscribe(testObserver)
        testObserver.assertNoErrors()
    }
}