package com.cozo.cozomvp.mainActivity

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.transition.Scene
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.*
import com.cozo.cozomvp.listFragment.ListFragmentView
import com.cozo.cozomvp.listFragment.LocalListFragment
import com.cozo.cozomvp.mapFragment.LocalMapFragment
import com.cozo.cozomvp.mapFragment.MapFragmentView
import com.cozo.cozomvp.networkAPI.CardMenuData
import com.cozo.cozomvp.networkAPI.NetworkModel
import com.cozo.cozomvp.transition.TransitionUtils
import com.hannesdorfmann.mosby3.mvp.MvpActivity

class MainActivity : MvpActivity<MainView, MainPresenter>(), MainView, ListFragmentView.MainActivityListener,
        MapFragmentView.MainActivityListener, DetailsInterface.MainActivityListener {

    private lateinit var mListFragment : LocalListFragment
    private lateinit var mMapFragment : LocalMapFragment
    private lateinit var containerLayout: ViewGroup
    private lateinit var currentTransitionName: String
    private var detailsScene: Scene? = null

    override fun addRecyclerViewToContainer(mRecyclerView : RecyclerView) {
        containerLayout.addView(mRecyclerView)
    }

    override fun createPresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun hideOrderDetailsMenu(mSharedView: View?) {
        if (mSharedView != null){
            DetailsLayout.hideScene(this, containerLayout, mSharedView, "name")
            detailsScene = null
            containerLayout.removeAllViews()
        } else {
            // treat exception
            Log.d("MVPdebug", "couldn't find view for transition $currentTransitionName")
        }
    }

    override fun onActivityRequired(): MainActivity {
        return this
    }

    override fun onBackPressed() {
        if (detailsScene != null) {
            val childPosition : Int = TransitionUtils.getItemPositionFromTransition(currentTransitionName)
            presenter.onBackPressed(childPosition)
        } else {
            super.onBackPressed()
        }
    }

    override fun onRestCardViewHighlighted(restID: String) {
        presenter.onRestCardViewHighlighted(restID)
    }

    override fun onCompleteListFragment(listFragment: LocalListFragment){
        mListFragment = listFragment
    }

    override fun onCompleteMapFragment(mapFragment: LocalMapFragment){
        mMapFragment = mapFragment
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        setContentView(R.layout.activity_main)

        presenter.onActivityCreated()

        // launches map fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mapContainer, LocalMapFragment.newInstance(), LocalMapFragment.TAG)
                .addToBackStack(LocalMapFragment.TAG)
                .commit()

        // launches list fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.listContainer, LocalListFragment.newInstance(), LocalListFragment.TAG)
                .addToBackStack(LocalListFragment.TAG)
                .commit()
    }

    override fun onListFragmentRequired(): LocalListFragment {
        return mListFragment
    }

    override fun onMapFragmentRequired(): LocalMapFragment {
        return mMapFragment
    }

    override fun onMapMarkerClicked(restID: String) {
        presenter.onMapMarkerClicked(restID)
    }

    override fun onOrderButtonClicked() {
        val childPosition : Int = TransitionUtils.getItemPositionFromTransition(currentTransitionName)
        presenter.onOrderButtonClicked(childPosition)
    }

    override fun onPartnerCardViewClicked(partnerID: String) {
        presenter.onPartnerCardViewClicked(partnerID)
    }

    override fun onPartnersCardDataReady(locations: MutableMap<String, NetworkModel.Location>, routes: MutableMap<String, List<NetworkModel.Leg>>) {
        presenter.onPartnersCardDataReady(locations, routes)
    }

    override fun onRestaurantCardViewClicked(sharedView: View, transitionName: String, data: CardMenuData, restID: String) {
        currentTransitionName = transitionName
        containerLayout = findViewById(R.id.recyclerContainer)  //initialize here?
        presenter.onRestaurantCardViewClicked(restID, sharedView, data)
    }

    override fun showOrderDetailsMenu(sharedView: View, data: CardMenuData){
        // shows up detailed view and sets listener for 'order' button
        detailsScene = DetailsLayout.showScene(this, containerLayout, sharedView, currentTransitionName, data)
    }

}