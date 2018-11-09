package com.cozo.cozomvp.dataprovider

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.cozo.cozomvp.mainactivity.MainActivity
import com.cozo.cozomvp.networkapi.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.socket.client.Socket
import retrofit2.HttpException
import java.net.UnknownHostException
import io.socket.emitter.Emitter

class DataProvider : DataProviderInterface.Model {

    private lateinit var mListenerMainActivity : DataProviderInterface.MainActivityListener
    private lateinit var mListenerListFragment : DataProviderInterface.ListFragmentListener
    private lateinit var mListenerMapFragment : DataProviderInterface.MapFragmentListener
    private lateinit var mSocket: Socket
    private var mActivity : MainActivity? = null
    private var isConnectedToSocket: Boolean = false
    private var disposable: Disposable? = null  // companion object?
    private val apiServe by lazy {
        APIServices.create()
    }   // companion object?
    private var mLocationPermission = false
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    constructor(listener: DataProviderInterface.MainActivityListener) {
        this.mListenerMainActivity = listener
        mActivity = listener.getActivity()
    }
    constructor(listener: DataProviderInterface.ListFragmentListener) {
        this.mListenerListFragment = listener
        mActivity = listener.getActivity()
    }
    constructor(listener: DataProviderInterface.MapFragmentListener) {
        this.mListenerMapFragment = listener
        mActivity = listener.getActivity()
    }

    private val onConnect = Emitter.Listener {}
    private val onDisconnect = Emitter.Listener {
        mActivity?.runOnUiThread {
            isConnectedToSocket = false
            Toast.makeText(mActivity?.applicationContext,
                    "disconnected from server", Toast.LENGTH_LONG).show()
        }
    }
    private val onConnectError = Emitter.Listener {
        mActivity?.runOnUiThread {
            Toast.makeText(mActivity?.applicationContext,
                    "error while connecting to IO.Socket server", Toast.LENGTH_LONG).show()
        }
    }
    private val onListAvailable = Emitter.Listener { args ->
        mActivity?.runOnUiThread {
            val gson = Gson()
            val result = args[0] as String

            val partnerList = gson.fromJson(result, NetworkModel.ListPartners::class.java)

            mListenerListFragment.onPartCardDataRequestCompleted(partnerList.items)
        }
    }
    private val onUpdatedListAvailable = Emitter.Listener { args ->
        mActivity?.runOnUiThread {}
    }
    private val onUpdatedLocationsAvailable = Emitter.Listener { args ->
        mActivity?.runOnUiThread {}
    }

    override fun provideUserLatLng(idToken: String, isUserDeviceLocationNeeded: Boolean){
        if (isUserDeviceLocationNeeded){
            //get location from device
            createLocationRequest()
        }
        else {
            // get location from API
            disposable = apiServe.userMainLocation(idToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result ->
                                mListenerMainActivity.onUserLocationRequestCompleted(result)
                            },
                            { error ->
                                mListenerMainActivity.onUserLocationRequestFailed(error)
                                /*when (error) {
                                    is HttpException -> {
                                        when(error.code()){
                                            404 -> {}
                                        }
                                        val codeString = error.code()
                                        val errorString = error.response().errorBody()?.string()
                                        Log.d("Retrofit", "error code is $codeString. $errorString")
                                    }
                                    is UnknownHostException -> {
                                        //"UnknownHostException. Please check your internet connection".showToast(this)
                                        val errorString = error.toString()
                                        Log.d("Retrofit", "error is $errorString")
                                    }
                                    else -> {
                                        //"Unknown Error. Try again".showToast(this)
                                        Log.d("Retrofit", "unknown error")
                                    }
                                }*/
                            }
                    )
        }
    }
    override fun provideRestaurantsLatLng(location: NetworkModel.Location, radius: Int){
        disposable = apiServe.restaurantsNearestLocation(radius,location.latitude,location.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            val restList = result.objects
                            mListenerMapFragment.onRestLocationsRequestCompleted(restList)
                        },
                        { error ->
                            when (error) {
                                is HttpException -> {
                                    //"HttpException".showToast(this)
                                    val codeString = error.code()
                                    val errorString = error.response().errorBody()?.string()
                                    Log.d("Retrofit", "error code is $codeString. $errorString")
                                }
                                is UnknownHostException -> {
                                    //"UnknownHostException. Please check your internet connection".showToast(this)
                                    val errorString = error.toString()
                                    Log.d("Retrofit", "error is $errorString")
                                }
                                else -> {
                                    //"Unknown Error. Try again".showToast(this)
                                    Log.d("Retrofit", "unknown error")
                                }
                            }
                            mListenerMapFragment.onRestLocationsRequestFailed(error)
                        }
                )
    }
    override fun provideRestaurantsCardData(location: NetworkModel.Location, radius: Int){
        disposable = apiServe.restaurantsNearestMenu(radius,location.latitude,location.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            mListenerListFragment.onRestCardDataRequestCompleted(result.items)
                        },
                        { error ->
                            when (error) {
                                is HttpException -> {
                                    //"HttpException".showToast(this)
                                    val codeString = error.code()
                                    val errorString = error.response().errorBody()?.string()
                                    Log.d("Retrofit", "error code is $codeString. $errorString")
                                }
                                is UnknownHostException -> {
                                    //"UnknownHostException. Please check your internet connection".showToast(this)
                                    val errorString = error.toString()
                                    Log.d("Retrofit", "error is $errorString")
                                }
                                else -> {
                                    //"Unknown Error. Try again".showToast(this)
                                    Log.d("Retrofit", "unknown error")
                                }
                            }
                            mListenerListFragment.onRestCardDataRequestFailed(error)
                        }
                )
    }
    override fun provideDeliveryPartnersList(restLocation: NetworkModel.Location, userLocation: NetworkModel.Location) {
        setupSocketIO()
        val gson = Gson()
        val data = gson.toJson(SocketIOEmitData(restLocation, userLocation))
        mSocket.emit("awaiting_partners_list", data)
    }
    override fun provideRestaurantItems(restaurantID: String) {
        disposable = apiServe.restaurantsItems(restaurantID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {result ->
                            mListenerListFragment.onRestItemsDataRequestCompleted(result.items)
                        },
                        {error ->
                            mListenerListFragment.onRestItemsDataRequestFailed(error)

                        }
                )
    }

    private fun setupSocketIO(){
        val app : DelivererApplication = mActivity?.application as DelivererApplication
        mSocket = app.mSocket!!
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
        mSocket.on("partners_list_available", onListAvailable)
        //mSocket.on("updated partners list available", onUpdatedListAvailable)
        //mSocket.on("updated locations available", onUpdatedLocationsAvailable)
        mSocket.connect()
    }

    //refactor later
    private fun createLocationRequest(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity!!)
        val mLocationRequest: LocationRequest = LocationRequest().apply {
            interval = 60000
            fastestInterval = 60000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(mActivity!!)

        client.checkLocationSettings(builder.build()).addOnCompleteListener { mTask ->
            if (mTask.isSuccessful) {
                // All location settings are satisfied. Initializes location requests here.
                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationResult ?: return
                        for (location in locationResult.locations){
                            // only cares about the first location provided by app. Ignores the next ones
                            if (!mLocationPermission) {
                                mLocationPermission = true
                                mListenerMainActivity.onUserLocationRequestCompleted(NetworkModel.Location(location.latitude,location.longitude))
                            }
                        }
                    }
                }
                //think on how to decouple this (avoid model to check permissions from activity
                val hasPermissionBeenGranted: Boolean = (ContextCompat.checkSelfPermission(mActivity!!, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
                if(hasPermissionBeenGranted){
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
                }
            }
            else {
                val mException: ApiException = mTask.exception as ApiException
                when (mException.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        // do this at activity class because callback is there...refactor later
                        val mResolvableException = mException as ResolvableApiException
                        mResolvableException.startResolutionForResult(mActivity!!, MY_GPS_RESOLUTION_STATUS_CODE)
                    }
                }
            }
        }
    }

    companion object {
        var MY_GPS_RESOLUTION_STATUS_CODE = 1
    }

}

data class SocketIOEmitData(
    val restLocation: NetworkModel.Location,
    val userLocation: NetworkModel.Location)
