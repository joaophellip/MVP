package com.cozo.cozomvp.mainActivity

import android.os.Bundle
import com.cozo.cozomvp.*
import com.cozo.cozomvp.listFragment.ListFragmentView
import com.cozo.cozomvp.listFragment.LocalListFragment
import com.cozo.cozomvp.mapFragment.LocalMapFragment
import com.cozo.cozomvp.mapFragment.MapFragmentView
import com.cozo.cozomvp.networkAPI.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpActivity

class MainActivity : MvpActivity<MainView, MainPresenter>(), MainView, ListFragmentView.MainActivityListener,
        MapFragmentView.MainActivityListener {

    private lateinit var mListFragment : LocalListFragment
    private lateinit var mMapFragment : LocalMapFragment

    override fun createPresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        setContentView(R.layout.activity_main)

        // request presenter to provide user location, which is needed by both fragments
        presenter.provideUserLocation()

        // launch map fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mapContainer, LocalMapFragment.newInstance(), LocalMapFragment.TAG)
                .addToBackStack(LocalMapFragment.TAG)
                .commit()

        // launch list fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.listContainer, LocalListFragment.newInstance(), LocalListFragment.TAG)
                .addToBackStack(LocalListFragment.TAG)
                .commit()
    }

    override fun onMapMarkerClicked(restID: String) {
        presenter.onMapMarkerClicked(restID, mListFragment)
    }

    override fun onCardViewHighlighted(restID: String) {
        presenter.onCardViewHighlighted(restID, mMapFragment)
    }

    override fun onLocationAvailable(location: NetworkModel.Location) {
        mListFragment = supportFragmentManager.findFragmentByTag(LocalListFragment.TAG) as LocalListFragment
        mMapFragment = supportFragmentManager.findFragmentByTag(LocalMapFragment.TAG) as LocalMapFragment
        presenter.relayLocationToListFragment(location, mListFragment)
        presenter.relayLocationToMapFragment(location, mMapFragment)
    }

}