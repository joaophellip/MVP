package com.cozo.cozomvp

import android.os.Bundle
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hannesdorfmann.mosby3.mvp.MvpActivity

class MapActivity : MvpActivity<MapView, MapPresenter>(), MapView, OnMapReadyCallback {

    @BindView(R.id.greetingTextView)
    lateinit var greetingTextView: TextView

    lateinit var mapFragment: SupportMapFragment
    private var mUserDefaultLocation: LatLng? = null
    private var mMap: GoogleMap? = null
    private var defaultMapZoom = 15

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        presenter.provideUserLocation()
        mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override
    fun createPresenter(): MapPresenter {
        return MapPresenter()
    }

    @OnClick(R.id.startButton)
    fun onHelloButtonClicked() {}

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
    }

    override fun addUserMarkerToMap(location: NetworkModel.Location) {
        mUserDefaultLocation = LatLng(location.latitude,location.longitude)
        mMap?.addMarker(MarkerOptions()
                .position(mUserDefaultLocation!!)
                .title("você está aqui"))
        mMap?.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mUserDefaultLocation, defaultMapZoom.toFloat()))
    }

    override fun addRestaurantMarkersToMap(locations: List<NetworkModel.Location>) {
        mUserDefaultLocation = null
        locations.forEach {
            mMap?.addMarker(MarkerOptions()
                    .position(LatLng(it.latitude,it.longitude))
                    .title("restaurante"))
        }
    }

    override fun readyToReceiveRestaurantsLocations(location: NetworkModel.Location) {
        presenter.provideRestaurantsLocations(location)
    }
}