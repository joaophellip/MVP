package com.cozo.cozomvp.dataProvider

import android.graphics.BitmapFactory
import android.util.Log
import com.cozo.cozomvp.networkAPI.APIServices
import com.cozo.cozomvp.networkAPI.CardMenuData
import com.cozo.cozomvp.networkAPI.ListPresenterData
import com.cozo.cozomvp.networkAPI.NetworkModel
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

class DataProvider() : DataProviderInterface.Model {

    private lateinit var mListenerMainActivity : DataProviderInterface.MainActivityListener
    private lateinit var mListenerListFragment : DataProviderInterface.ListFragmentListener
    private lateinit var mListenerMapFragment : DataProviderInterface.MapFragmentListener

    constructor(listener: DataProviderInterface.MainActivityListener) : this(){
        this.mListenerMainActivity = listener
    }
    constructor(listener: DataProviderInterface.ListFragmentListener) : this(){
        this.mListenerListFragment = listener
    }
    constructor(listener: DataProviderInterface.MapFragmentListener) : this(){
        this.mListenerMapFragment = listener
    }

    private var disposable: Disposable? = null  //put as a companion object to avoid being constructed every time an object of MapDataProvider is instantiated.
    private val apiServe by lazy {
        APIServices.create()
    }

    override fun provideUserLatLng(userID: String){
        disposable = apiServe.userMainLocation(userID)
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
                                            val mCardMenu = CardMenuData(null, NetworkModel.MenuMetadata(
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
                                mListenerListFragment.onRestCardDataRequestCompleted(mList)
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

}