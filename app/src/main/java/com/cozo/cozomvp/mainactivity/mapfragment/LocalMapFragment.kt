package com.cozo.cozomvp.mainactivity.mapfragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkapi.NetworkModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.support.v4.content.res.ResourcesCompat
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import com.cozo.cozomvp.mainactivity.MainActivity
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.CameraUpdate
import com.google.maps.android.PolyUtil
import java.lang.Math.round

class LocalMapFragment : MvpFragment<MapFragmentView, MapFragmentPresenter>(), MapFragmentView, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private lateinit var mUserDefaultLocation: LatLng
    private lateinit var currentRestLocation: LatLng
    private lateinit var mListenerMainActivity : MapFragmentView.MainActivityListener
    private var mMap: GoogleMap? = null
    private var mRestMarkerMap: MutableMap<String,Marker> = mutableMapOf()
    private var mPartMarkerMap: MutableMap<String,Marker> = mutableMapOf()
    private var mRoutes: MutableMap<String, List<LatLng>> = mutableMapOf()
    private var mPolyline: Polyline? = null
    private var isUserLocationAvailable = false
    private var areGesturesEnabled = true
    private var animateCameraCallback = object: GoogleMap.CancelableCallback{
        override fun onFinish() {
            mMap?.uiSettings?.setAllGesturesEnabled(areGesturesEnabled)
        }
        override fun onCancel() {}
    }

    override fun addMarkersBack(){
        mRestMarkerMap.forEach{
            it.value.isVisible = true
        }

        val mListLocations: MutableList<LatLng> = mutableListOf()
        mPartMarkerMap.forEach {
            mListLocations.add(it.value.position)
        }
        mListLocations.add(currentRestLocation)
        mListLocations.add(mUserDefaultLocation)
        areGesturesEnabled = true
        adjustZoomLevel(mListLocations, false)
    }

    override fun addPartnerMarkersToMap(locations: MutableMap<String, NetworkModel.Location>) {
        locations.forEach {
            val mMarker = mMap?.addMarker(MarkerOptions()
                    .position(LatLng(it.value.latitude,it.value.longitude))
                    .title("entregador")
                    .icon(vectorToBitmap(R.drawable.ic_motorcycle_black_18dp, ContextCompat.getColor(this.context!!,R.color.blue_700))))
            mPartMarkerMap[it.key] = mMarker!!
        }

        val mListLocations: MutableList<LatLng> = mutableListOf()
        mPartMarkerMap.forEach {
            mListLocations.add(it.value.position)
        }
        mListLocations.add(currentRestLocation)
        mListLocations.add(mUserDefaultLocation)
        areGesturesEnabled = true
        adjustZoomLevel(mListLocations)
    }

    override fun addRestaurantMarkersToMap(locations: List<NetworkModel.RestLocationObjects>) {
        locations.forEach {
            val mMarker = mMap?.addMarker(MarkerOptions()
                    .position(LatLng(it.location.latitude,it.location.longitude))
                    .title("restaurante")
                    .icon(vectorToBitmap(R.drawable.ic_restaurant_test_18dp, ContextCompat.getColor(this.context!!,R.color.blue_700))))
            mRestMarkerMap[it.id] = mMarker!!
        }
        val mListLocations: MutableList<LatLng> = mutableListOf()
        mRestMarkerMap.forEach {
            mListLocations.add(it.value.position)
        }
        mListLocations.add(mUserDefaultLocation)
        areGesturesEnabled = true
        adjustZoomLevel(mListLocations)
    }

    override fun addUserMarkerToMap(location: NetworkModel.Location) {
        mUserDefaultLocation = LatLng(location.latitude,location.longitude)
        isUserLocationAvailable = true
        mMap?.let {
            // centers camera to user location
            it.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(mUserDefaultLocation, defaultMapZoom.toFloat()))

            // add marker to identify user location on map
            it.addMarker(MarkerOptions()
                    .position(mUserDefaultLocation)
                    .title("você está aqui"))
        }
    }

    override fun createPresenter(): MapFragmentPresenter {
        return MapFragmentPresenter()
    }

    override fun drawRouteToUserLocation(partnerID: String) {
        if (mPolyline != null){
            mPolyline?.remove()
        }
        if (mRoutes[partnerID] != null){
            val mLineOptions = PolylineOptions()
            mLineOptions.addAll(mRoutes[partnerID])
            mLineOptions.width(10f)
            mLineOptions.color(Color.GRAY)
            mPolyline = mMap?.addPolyline(mLineOptions)
        } else {
            Log.d("MVPdebug","wont draw route because partnerID couldn't be found")
            //to do something
        }
    }

    override fun highlightMapMarker(restID: String) {
        if (mRestMarkerMap[restID] != null){
            mRestMarkerMap.forEach{
                when(it.key){
                    restID -> it.value.setIcon(vectorToBitmap(R.drawable.ic_restaurant_black_24dp, ContextCompat.getColor(this.context!!,R.color.blue_500)))
                    else -> it.value.setIcon(vectorToBitmap(R.drawable.ic_restaurant_test_18dp, ContextCompat.getColor(this.context!!,R.color.blue_700)))
                }
            }
        } else {
            //to do something
        }
    }

    override fun onActivityRequired(): MainActivity {
        return mListenerMainActivity.onActivityRequired()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mListenerMainActivity = activity as MapFragmentView.MainActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement MapFragmentView.MainActivityListener ")
        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mView : View? = inflater.inflate(R.layout.fragment_map, container, false)
        mListenerMainActivity.onCompleteMapFragment(this)

        mView?.viewTreeObserver?.addOnGlobalLayoutListener {
            mWidth = mView.width
            mHeight = mView.height
        }

        return mView
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        mMap!!.setOnMarkerClickListener(this)
        if (isUserLocationAvailable){
            mMap?.let {
                // centers camera to user location
                it.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(mUserDefaultLocation, defaultMapZoom.toFloat()))

                // add marker to identify user location on map
                it.addMarker(MarkerOptions()
                        .position(mUserDefaultLocation)
                        .title("você está aqui"))
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        mListenerMainActivity.onMapMarkerClicked(idByMarker(p0!!))
        return true
    }

    override fun onPartLocationDataAvailable(locations: MutableMap<String, NetworkModel.Location>, encodedPolylines: Map<String, String>) {
        presenter.onPartLocationDataAvailable(locations, encodedPolylines)
    }

    override fun onPartnerCardViewClicked(partnerID: String) {
        presenter.onPartnerCardViewClicked(partnerID)
    }

    override fun onRestaurantCardViewClicked(restID: String){
        presenter.onRestaurantCardViewClicked(restID)
    }

    override fun onUserLocationDataAvailable(location: NetworkModel.Location) {
        presenter.onUserLocationDataAvailable(location)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment).getMapAsync(this)
    }

    override fun removeAllButThisRestaurantMarker(restID: String) {
        mRestMarkerMap.forEach{
            if (it.key != restID) {
                it.value.isVisible = false
            }
        }
        currentRestLocation = locationById(restID)

        val mListLocations: MutableList<LatLng> = mutableListOf()
        mListLocations.add(currentRestLocation)
        mListLocations.add(mUserDefaultLocation)
        areGesturesEnabled = true
        adjustZoomLevel(mListLocations, true)
    }

    override fun removeAllButThisPartnerMarker(partnerID: String) {
        mPartMarkerMap.forEach{
            when(it.key){
                partnerID -> it.value.isVisible = true
                else -> it.value.isVisible = false
            }
        }
    }

    override fun restLocation(restID: String): NetworkModel.Location {
        return when (mRestMarkerMap.containsKey(restID)){
            true -> {
                val mMap = mRestMarkerMap.filterKeys{
                    it == restID
                }
                return when(mMap.size == 1){
                    true -> NetworkModel.Location(mMap.values.elementAt(0).position.latitude,
                            mMap.values.elementAt(0).position.longitude)
                    else -> NetworkModel.Location(0.0,0.0)
                }
            }
            false -> NetworkModel.Location(0.0,0.0)
        }
    }

    override fun savePartnerRoutes(encodedPolylines: Map<String, String>) {
        //decode polylines and save resulting routes
        //check: https://stackoverflow.com/questions/15924834/decoding-polyline-with-new-google-maps-api
        encodedPolylines.forEach{
            mRoutes[it.key] = PolyUtil.decode(it.value)
        }
    }

    /*
    Adjust map zoom level to fit all locations passed as argument.
     */
    private fun adjustZoomLevel(mListLocations: MutableList<LatLng>, shiftOnY: Boolean = false){
        val builder:LatLngBounds.Builder = LatLngBounds.Builder()
        mListLocations.forEach {
            builder.include(it)
        }
        val bounds: LatLngBounds = builder.build()

        val shiftOnYCallback = object : GoogleMap.CancelableCallback{
            override fun onFinish() {
                val mCardViewItemPlaceHeight: Int = 400*3
                val mCardViewHeight: Int = 300*3
                val scrollXBy: Float = ((mCardViewItemPlaceHeight - mCardViewHeight)/2).toFloat()
                val mOffset:CameraUpdate = CameraUpdateFactory.scrollBy(0f,scrollXBy)
                mMap?.animateCamera(mOffset, animateCameraCallback)
            }
            override fun onCancel() {}
        }

        //set margin as 25% of the map dimensions
        val horizontalMargin: Int = round(0.25*mWidth).toInt()
        val verticalMargin: Int = round(0.25*mHeight).toInt()

        val cu:CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, mWidth - horizontalMargin, mHeight - verticalMargin, 0)
        if (shiftOnY) {
            mMap?.animateCamera(cu,shiftOnYCallback)
        }
        else {
            mMap?.animateCamera(cu)
        }
    }

    /*
    Return restaurant ID mapped to argument 'Marker'
     */
    private fun idByMarker(marker: Marker) : String {
        // do a few error handling here...
        return when (mRestMarkerMap.containsValue(marker)){
            true -> {
                val mMap = mRestMarkerMap.filterValues{
                    it == marker
                }
                return when(mMap.size == 1){
                    true -> mMap.keys.elementAt(0)
                    else -> "MoreThanOne"
                }
            }
            false -> "NotFound"
        }
    }

    /*
    Return restaurant location mapped to 'restID'
     */
    private fun locationById(restID: String): LatLng {
        return when (mRestMarkerMap.containsKey(restID)){
            true -> {
                val mMap = mRestMarkerMap.filterKeys{
                    it == restID
                }
                mMap.values.elementAt(0).position
            }
            false -> {
                mUserDefaultLocation
            }
        }
    }

    /**
     * Demonstrates converting a [Drawable] to a [BitmapDescriptor],
     * for use as a marker icon.
     */
    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    companion object {
        val TAG : String = LocalMapFragment::class.java.simpleName
        const val defaultMapZoom = 15

        fun newInstance(): LocalMapFragment {
            return LocalMapFragment()
        }
    }
}