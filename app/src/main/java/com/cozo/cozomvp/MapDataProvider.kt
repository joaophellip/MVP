package com.cozo.cozomvp

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

class MapDataProvider(listener: MapDataLoadListener) {

    // Callback - Listener
    interface MapDataLoadListener{
        fun onMapDataGenerated(location: NetworkModel.Location)
        fun onRestaurantDataGenerated(locations: List<NetworkModel.Location>)
    }

    private var mListener = listener
    private var disposable: Disposable? = null
    private val apiServe by lazy {
        APIServices.create()
    }

    fun provideUserLatLng(userID: String){
        disposable = apiServe.userMainLocation(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            mListener.onMapDataGenerated(result)
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
                        }
                )
    }

    fun provideRestaurantsLatLng(location: NetworkModel.Location, radius: Int){
        disposable = apiServe.restaurantsNearest(radius,location.latitude,location.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            // Unzips file that contains images and json file. Reference: http://www.thecoderscorner.com/team-blog/java-and-jvm/12-reading-a-zip-file-from-java-using-zipinputstream/
                            val mList = mutableListOf<NetworkModel.Location>()
                            ZipInputStream(result.byteStream()).use{
                                var entry : ZipEntry?
                                val bufSize = 8192
                                val buffer = ByteArray(bufSize)
                                do {
                                    entry = it.nextEntry
                                    if (entry == null) break
                                    // For each file, read bytes to a byte array and convert it to a bitmap representation if image or string if text
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
                                        // Extracts location from Data.json
                                        val gson = Gson()
                                        val result2 = gson.fromJson(outputStream.toString(), NetworkModel.ListRestaurantsNearest::class.java)
                                        for (restID in result2.objects){
                                            mList.add(restID.location)
                                        }
                                        mListener.onRestaurantDataGenerated(mList)
                                    }
                                } while (true)
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
                        }
                )
    }

    /*companion object {
        fun instance(): MapDataProvider = MapDataProvider()
    }*/
}