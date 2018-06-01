package com.cozo.cozomvp.mapFragment

import android.os.Bundle
import android.view.View
import com.cozo.cozomvp.NetworkModel
import com.cozo.cozomvp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hannesdorfmann.mosby3.mvp.MvpFragment

class LocalMapFragment : MvpFragment<MapFragmentView, MapFragmentPresenter>(), MapFragmentView, OnMapReadyCallback {

    private var mUserDefaultLocation: LatLng? = null
    private var mMap: GoogleMap? = null

    override fun createPresenter(): MapFragmentPresenter {
        return MapFragmentPresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment).getMapAsync(this)
    }
    /*override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mapFragment, container, false)
    */

    override fun addUserMarkerToMap(location: NetworkModel.Location) {
        mUserDefaultLocation = LatLng(location.latitude,location.longitude)
        mMap?.addMarker(MarkerOptions()
                .position(mUserDefaultLocation!!)
                .title("você está aqui"))
        mMap?.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mUserDefaultLocation, defaultMapZoom.toFloat()))
    }

    override fun addRestaurantMarkersToMap(locations: List<NetworkModel.Location>) {
        locations.forEach {
            mMap?.addMarker(MarkerOptions()
                    .position(LatLng(it.latitude,it.longitude))
                    .title("restaurante"))
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
    }

    override fun onLocationDataAvailable(location: NetworkModel.Location) {
        presenter.onLocationDataAvailable(location)
    }

    /*//testOnClickCallbackProcess
val mActivity = activity as MainActivity
mActivity.highlightCard("test")*/

    companion object {
        val TAG : String = LocalMapFragment::class.java.simpleName
        const val defaultMapZoom = 15

        fun newInstance(): LocalMapFragment {
            return LocalMapFragment()
        }
    }

}