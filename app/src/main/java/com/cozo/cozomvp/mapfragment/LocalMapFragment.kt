package com.cozo.cozomvp.mapFragment

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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hannesdorfmann.mosby3.mvp.MvpFragment

class LocalMapFragment : MvpFragment<MapFragmentView, MapFragmentPresenter>(), MapFragmentView, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

    private var mUserDefaultLocation: LatLng? = null
    private var mMap: GoogleMap? = null
    private var mMarkerMap: MutableMap<String,Marker> = mutableMapOf()
    private lateinit var mListenerMainActivity : MapFragmentView.MainActivityListener

    override fun createPresenter(): MapFragmentPresenter {
        return MapFragmentPresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment).getMapAsync(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mListenerMainActivity = activity as MapFragmentView.MainActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement MapFragmentView.MainActivityListener ")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        mMap!!.setOnMarkerClickListener(this)
    }

    override fun onLocationDataAvailable(location: NetworkModel.Location) {
        Log.d("MVPdebug","onLocationDataAvailable fired")
        presenter.onLocationDataAvailable(location)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        mListenerMainActivity.onMapMarkerClicked(idByMarker(p0!!))
        return true
    }

    override fun addUserMarkerToMap(location: NetworkModel.Location) {
        mUserDefaultLocation = LatLng(location.latitude,location.longitude)
        mMap?.addMarker(MarkerOptions()
                .position(mUserDefaultLocation!!)
                .title("você está aqui"))
        mMap?.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mUserDefaultLocation, defaultMapZoom.toFloat()))
    }

    override fun addRestaurantMarkersToMap(locations: List<NetworkModel.RestLocationObjects>) {
        locations.forEach {
            val mMarker = mMap?.addMarker(MarkerOptions()
                    .position(LatLng(it.location.latitude,it.location.longitude))
                    .title("restaurante"))
            //Log.d("MVPdebug","marker $mMarker added")
            mMarkerMap[it.id] = mMarker!!
        }
    }

    override fun highlightMapMarker(restID: String) {
        if (mMarkerMap[restID] != null){
            //change icon for instance...
            //Log.d("MVPdebug","change icon for $restID")
        } else {
            Log.d("MVPdebug","wont change icon because marker couldn't be found")
            //to do something
        }
    }

    private fun idByMarker(marker: Marker) : String {
        // do a few error handling here...
        return when (mMarkerMap.containsValue(marker)){
            true -> {
                val mMap = mMarkerMap.filterValues{
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

    companion object {
        val TAG : String = LocalMapFragment::class.java.simpleName
        const val defaultMapZoom = 15

        fun newInstance(): LocalMapFragment {
            return LocalMapFragment()
        }
    }

}