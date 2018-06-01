package com.cozo.cozomvp

import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.UnknownHostException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class DataProvider() {

    private lateinit var mListener1 : UserLocationListener
    private lateinit var mListener2 : RestaurantLocationListener
    private lateinit var mListener3 : RestaurantCardDataListener

    constructor(listener: UserLocationListener) : this(){
        this.mListener1 = listener
    }

    constructor(listener: RestaurantLocationListener) : this(){
        this.mListener2 = listener
    }

    constructor(listener: RestaurantCardDataListener) : this(){
        this.mListener3 = listener
    }

    // Callback - Listener
    interface UserLocationListener{
        fun onSuccess(location: NetworkModel.Location)
        fun onError(e: Throwable)
    }
    interface RestaurantLocationListener{
        fun onSuccess(locations: List<NetworkModel.RestLocationObjects>)
        fun onError(e: Throwable)
    }
    interface RestaurantCardDataListener{
        fun onSuccess(cards: List<ListPresenterData>)
        fun onError(e: Throwable)
    }

    private var disposable: Disposable? = null  //put as a companion object to avoid being constructed every time an object of MapDataProvider is instantiated.
    private val apiServe by lazy {
        APIServices.create()
    }

    fun provideUserLatLng(userID: String){
        disposable = apiServe.userMainLocation(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            mListener1.onSuccess(result)
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
                            mListener1.onError(error)
                        }
                )
    }

    fun provideRestaurantsLatLng(location: NetworkModel.Location, radius: Int){
        disposable = apiServe.restaurantsNearestLocation(radius,location.latitude,location.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            val restList = result.objects
                            mListener2.onSuccess(restList)
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
                            mListener2.onError(error)
                        }
                )
    }

    fun provideRestaurantsCardData(location: NetworkModel.Location, radius: Int){
        disposable = apiServe.restaurantsNearestMenu(radius,location.latitude,location.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            // Unzips file that contains images and json file. Reference: http://www.thecoderscorner.com/team-blog/java-and-jvm/12-reading-a-zip-file-from-java-using-zipinputstream/
                            val mList = mutableListOf<ListPresenterData>()
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
                                        for (restID in result2.objects){
                                            val mCardMenu = CardMenuData(null,NetworkModel.MenuMetadata(
                                                    restID.metadata.ingredients,
                                                    restID.metadata.name,
                                                    restID.metadata.prepTime,
                                                    restID.metadata.pictureRefID,
                                                    restID.metadata.price,
                                                    restID.metadata.rating,
                                                    restID.metadata.ratedBy))
                                            mList.add(ListPresenterData(restID.id,mCardMenu))
                                        }
                                    } else {
                                        val mId = entry.name.substringBefore("/",entry.name)
                                        // refactor code to use list binarySearch !!
                                        mList.map{
                                            when(it.restID) {
                                                mId -> it.cardMenu.image = BitmapFactory.decodeByteArray(outputStream.toByteArray(),0,outputStream.toByteArray().size)
                                            }
                                        }
                                    }
                                } while (true)
                                mListener3.onSuccess(mList)
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
                            mListener3.onError(error)
                        }
                )
    }
}