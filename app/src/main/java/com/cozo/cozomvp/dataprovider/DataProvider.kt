package com.cozo.cozomvp.dataprovider

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.cozo.cozomvp.mainactivity.MainActivity
import com.cozo.cozomvp.networkapi.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.socket.client.Socket
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.UnknownHostException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import io.socket.emitter.Emitter

class DataProvider() : DataProviderInterface.Model {

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
    private val mapsServe by lazy {
        MapsAPIService.create()
    }   // companion object?
    private var mRestaurantsMap: MutableMap<String,CardMenuData> = mutableMapOf()
    private var mPartnersMap: MutableMap<String,CardInfoData> = mutableMapOf()

    constructor(listener: DataProviderInterface.MainActivityListener) : this(){
        this.mListenerMainActivity = listener
        mActivity = listener.getActivity()
    }
    constructor(listener: DataProviderInterface.ListFragmentListener) : this(){
        this.mListenerListFragment = listener
        mActivity = listener.getActivity()
    }
    constructor(listener: DataProviderInterface.MapFragmentListener) : this(){
        this.mListenerMapFragment = listener
        mActivity = listener.getActivity()
    }

    private val onConnect = Emitter.Listener {
        /*mActivity?.runOnUiThread(Runnable {
            if (!isConnectedToSocket) {
                Toast.makeText(mActivity?.applicationContext,
                        "connected to server", Toast.LENGTH_LONG).show()
                isConnectedToSocket = true
            }
        })*/
    }
    private val onDisconnect = Emitter.Listener {
        mActivity?.runOnUiThread {
            Log.i("socketIODebug", "disconnected")
            isConnectedToSocket = false
            Toast.makeText(mActivity?.applicationContext,
                    "disconnected from server", Toast.LENGTH_LONG).show()
        }
    }
    private val onConnectError = Emitter.Listener {
        mActivity?.runOnUiThread {
            for (msg in it){
                Log.e("DeliveryPartnerDebug", "Error connecting. $msg")
            }
            Toast.makeText(mActivity?.applicationContext,
                    "error while connecting to IO.Socket server", Toast.LENGTH_LONG).show()
        }
    }
    private val onListAvailable = Emitter.Listener { args ->
        mActivity?.runOnUiThread {
            val result = args[0] as ByteArray
            ZipInputStream(result.inputStream()).use{
                var entry : ZipEntry?
                val bufSize = 8192
                val buffer = ByteArray(bufSize)
                do {
                    entry = it.nextEntry
                    if (entry == null) break
                    // For each file, try reading bytes to byte array, and convert it to either a bitmap representation if image or a string if text.
                    var bytesRead : Int
                    var bytesReadTotal = 0
                    val outputStream = ByteArrayOutputStream()
                    try {
                        bytesRead = it.read(buffer,0, bufSize)
                        while(bytesRead > 0) {
                            bytesReadTotal += bytesRead
                            outputStream.write(buffer,0,bytesRead)
                            bytesRead = it.read(buffer,0, bufSize)
                        }
                    } catch (e: IOException){
                        Log.d("bytes","Error while reading bytes.")
                    }
                    if (entry.name == "Data.json") {
                        val gson = Gson()
                        val result2 = gson.fromJson(outputStream.toString(), NetworkModel.ListDeliveryPartnersInfo::class.java)
                        for (mDeliveryPartner in result2.objects){
                            if (mPartnersMap.containsKey(mDeliveryPartner.id)){
                                mPartnersMap[mDeliveryPartner.id]?.info = NetworkModel.PartnerMetadata(
                                        mDeliveryPartner.metadata.location,
                                        mDeliveryPartner.metadata.name,
                                        mDeliveryPartner.metadata.pricePerKm,
                                        mDeliveryPartner.metadata.totalPrice,
                                        mDeliveryPartner.metadata.route)
                            } else {
                                val mPartnerInfo = CardInfoData(null, NetworkModel.PartnerMetadata(
                                        mDeliveryPartner.metadata.location,
                                        mDeliveryPartner.metadata.name,
                                        mDeliveryPartner.metadata.pricePerKm,
                                        mDeliveryPartner.metadata.totalPrice,
                                        mDeliveryPartner.metadata.route))
                                mPartnersMap[mDeliveryPartner.id] = mPartnerInfo
                            }
                        }
                    } else {
                        val mPartnerID : String = entry.name.substringBefore("/",entry.name)
                        if (mPartnersMap.containsKey(mPartnerID)){
                            mPartnersMap[mPartnerID]?.image = BitmapFactory.decodeByteArray(outputStream.toByteArray(),0,outputStream.toByteArray().size)
                        } else {
                            val mPartnerInfo = CardInfoData(BitmapFactory.decodeByteArray(outputStream.toByteArray(),0,outputStream.toByteArray().size),
                                    null)
                            mPartnersMap[mPartnerID] = mPartnerInfo
                        }
                    }
                } while (true)
            }
            mListenerListFragment.onPartCardDataRequestCompleted(mPartnersMap)
        }
    }
    private val onUpdatedListAvailable = Emitter.Listener { args ->
        mActivity?.runOnUiThread {
            Log.d("DeliveryPartnerDebug","onUpdatedListAvailable: ${args[0]}")
        }
    }
    private val onUpdatedLocationsAvailable = Emitter.Listener { args ->
        mActivity?.runOnUiThread {
            Log.d("DeliveryPartnerDebug","onUpdatedLocationsAvailable. Args: ${args[0]}")
        }
    }

    override fun provideUserLatLng(idToken: String){
        disposable = apiServe.userMainLocation(idToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            mListenerMainActivity.onUserLocationRequestCompleted(result)
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
                            mListenerMainActivity.onUserLocationRequestFailed(error)
                        }
                )
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
                            // Unzips file that contains images and json file. Reference: http://www.thecoderscorner.com/team-blog/java-and-jvm/12-reading-a-zip-file-from-java-using-zipinputstream/
                            ZipInputStream(result.byteStream()).use{
                                var entry : ZipEntry?
                                val bufSize = 8192
                                val buffer = ByteArray(bufSize)
                                do {
                                    entry = it.nextEntry
                                    if (entry == null) break
                                    // For each file, try reading bytes to byte array, and convert it to either a bitmap representation if image or a string if text.
                                    var bytesRead : Int
                                    var bytesReadTotal = 0
                                    val outputStream = ByteArrayOutputStream()
                                    try {
                                        bytesRead = it.read(buffer,0, bufSize)
                                        while(bytesRead > 0) {
                                            bytesReadTotal += bytesRead
                                            outputStream.write(buffer,0,bytesRead)
                                            bytesRead = it.read(buffer,0, bufSize)
                                        }
                                    } catch (e: IOException){
                                        Log.d("bytes","Error while reading bytes.")
                                    }
                                    // Expects Data.json file to be the first file within zip. The order has to be assured by backend service.
                                    if (entry.name == "Data.json") {
                                        // Extracts data from Data.json
                                        val gson = Gson()
                                        val result2 = gson.fromJson(outputStream.toString(), NetworkModel.ListNearestRestaurantMenu::class.java)
                                        for (restaurant in result2.objects){
                                            if (mRestaurantsMap.containsKey(restaurant.id)){
                                                mRestaurantsMap[restaurant.id]?.menu = NetworkModel.MenuMetadata(
                                                        restaurant.metadata.ingredients,
                                                        restaurant.metadata.name,
                                                        restaurant.metadata.prepTime,
                                                        restaurant.metadata.pictureRefID,
                                                        restaurant.metadata.price,
                                                        restaurant.metadata.rating,
                                                        restaurant.metadata.ratedBy)
                                            } else {
                                                val mRestaurantMenu = CardMenuData(null, NetworkModel.MenuMetadata(
                                                        restaurant.metadata.ingredients,
                                                        restaurant.metadata.name,
                                                        restaurant.metadata.prepTime,
                                                        restaurant.metadata.pictureRefID,
                                                        restaurant.metadata.price,
                                                        restaurant.metadata.rating,
                                                        restaurant.metadata.ratedBy))
                                                mRestaurantsMap[restaurant.id] = mRestaurantMenu
                                            }
                                        }
                                    } else {
                                        val mRestID : String = entry.name.substringBefore("/",entry.name)
                                        if (mRestaurantsMap.containsKey(mRestID)){
                                            mRestaurantsMap[mRestID]?.image = BitmapFactory.decodeByteArray(outputStream.toByteArray(),0,outputStream.toByteArray().size)
                                        } else {
                                            val mRestaurantMenu = CardMenuData(BitmapFactory.decodeByteArray(outputStream.toByteArray(),0,outputStream.toByteArray().size),
                                                    null)
                                            mRestaurantsMap[mRestID] = mRestaurantMenu
                                        }
                                    }
                                } while (true)
                                mListenerListFragment.onRestCardDataRequestCompleted(mRestaurantsMap)
                            }
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
        mSocket.emit("ready to receive partners list", data)
    }
    override fun provideRoute(from: LatLng, to: LatLng){
        val fromParam = "${from.latitude},${from.longitude}"
        val toParam = "${to.latitude},${to.longitude}"
        disposable = mapsServe.getRoute(fromParam, toParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { _ ->
                            mListenerMapFragment.onRouteRequestCompleted()
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
                            mListenerMapFragment.onRouteRequestFailed(error)
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
        mSocket.on("partners list available", onListAvailable)
        mSocket.on("updated partners list available", onUpdatedListAvailable)
        mSocket.on("updated locations available", onUpdatedLocationsAvailable)
        mSocket.connect()
    }

}

data class SocketIOEmitData(
    val restLocation: NetworkModel.Location,
    val userLocation: NetworkModel.Location)
