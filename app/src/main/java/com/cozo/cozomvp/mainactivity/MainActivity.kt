package com.cozo.cozomvp.mainactivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.RecyclerView
import android.transition.Scene
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cozo.cozomvp.R
import com.cozo.cozomvp.PaymentActivity
import com.cozo.cozomvp.SettingsActivity
import com.cozo.cozomvp.mainactivity.checkoutfragment.CheckoutFragment
import com.cozo.cozomvp.mainactivity.checkoutfragment.CheckoutView
import com.cozo.cozomvp.listfragment.ListFragmentView
import com.cozo.cozomvp.listfragment.LocalListFragment
import com.cozo.cozomvp.mapfragment.LocalMapFragment
import com.cozo.cozomvp.mapfragment.MapFragmentView
import com.cozo.cozomvp.networkapi.CardMenuData
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.transition.TransitionUtils
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : MvpActivity<MainView, MainPresenter>(), MainView, ListFragmentView.MainActivityListener,
        MapFragmentView.MainActivityListener, CheckoutView.MainActivityListener,
        DetailsInterface.MainActivityListener,
        NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mListFragment : LocalListFragment
    private lateinit var mMapFragment : LocalMapFragment
    private lateinit var mCheckoutFragment: CheckoutFragment
    private lateinit var currentTransitionName: String
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userNameText: TextView
    private var containerLayout: ViewGroup? = null
    private var detailsScene: Scene? = null
    private var isListFragmentReady = false
    private var isMapFragmentReady = false
    private var isCheckoutFragmentReady = false

    override fun addRecyclerViewToContainer(mRecyclerView : RecyclerView) {
        containerLayout?.addView(mRecyclerView)
    }

    override fun areFragmentsReady(): Boolean{
        return isListFragmentReady && isMapFragmentReady
    }

    override fun createPresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun hideOrderDetailsMenu(mSharedView: View?) {
        if (mSharedView != null){
            DetailsLayout.hideScene(this, containerLayout!!, mSharedView, "name")
            detailsScene = null
            containerLayout?.removeAllViews()
        }
        else {
            // treat exception
            Log.d("MVPdebug", "couldn't find view for transition $currentTransitionName")
        }
    }

    override fun launchListFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.listContainer, LocalListFragment.newInstance(), LocalListFragment.TAG)
                .addToBackStack(LocalListFragment.TAG)
                .commit()
    }

    override fun launchMapFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mapContainer, LocalMapFragment.newInstance(), LocalMapFragment.TAG)
                .addToBackStack(LocalMapFragment.TAG)
                .commit()
    }

    override fun launchCheckoutFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.checkoutContainer, CheckoutFragment.newInstance(), CheckoutFragment.TAG)
                .addToBackStack(CheckoutFragment.TAG)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            MY_GPS_RESOLUTION_STATUS_CODE -> {
                if (resultCode == RESULT_OK) {
                    presenter.onLocationServiceReady()
                    //createLocationRequest()
                    Log.d("Permission", "1")
                }
                else {
                    linear_layout_permission_box.visibility = View.VISIBLE
                    Log.d("Permission", "2")
                }
            }
        }
    }

    override fun onActivityRequired(): MainActivity {
        return this
    }

    override fun onBackPressed() {
        if (detailsScene != null) {
            val childPosition : Int = TransitionUtils.getItemPositionFromTransition(currentTransitionName)
            presenter.onBackPressed(childPosition)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                    presenter.onLocationServiceReady()
                } else {
                    // permission denied
                    linear_layout_permission_box.visibility = View.VISIBLE
                    Log.d("locationDebug","permission denied")
                }
                return
            }
            else -> {} //ignore all other requests
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCompleteCheckoutFragment(checkoutFragment: CheckoutFragment) {
        mCheckoutFragment = checkoutFragment
        isCheckoutFragmentReady = true
    }

    override fun onCompleteListFragment(listFragment: LocalListFragment){
        mListFragment = listFragment
        isListFragmentReady = true
        presenter.onFragmentReady()
    }

    override fun onCompleteMapFragment(mapFragment: LocalMapFragment){
        mMapFragment = mapFragment
        isMapFragmentReady = true
        presenter.onFragmentReady()
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        setContentView(R.layout.activity_main)

        val isFirstTimeLogged: Boolean = intent.getBooleanExtra("IS_FIRST_TIME_LOGGED",false)

        presenter.onActivityCreated(isFirstTimeLogged)

        permission_button.setOnClickListener{
            linear_layout_permission_box.visibility = View.GONE
            requestUserPermissionForLocation()
        }

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
        //transitionName vem de uma classe chama TransitionUtils
        if (containerLayout == null) {
            containerLayout = findViewById(R.id.recyclerContainer)
        }
        presenter.onRestaurantCardViewClicked(restID, sharedView, data)
    }

    override fun onRestCardViewHighlighted(restID: String) {
        presenter.onRestCardViewHighlighted(restID)
    }

    override fun requestUserPermissionForLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            presenter.onLocationServiceReady()
            //createLocationRequest()
        } else {
            Log.d("permission", "ask for permission")
//            TODO: tratar caso de "não perguntar novamente"
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun showOrderDetailsMenu(sharedView: View, data: CardMenuData){
        // shows up detailed view
        detailsScene = DetailsLayout.showScene(this, containerLayout!!, sharedView, currentTransitionName, data)
    }

    override fun goToPaymentActivity() {
        startActivity(Intent(this, PaymentActivity::class.java))
    }

    override fun goToSettingsActivity() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when  (item.itemId) {
            R.id.menu_home -> {}
            R.id.menu_payment -> presenter.onPaymentMenuClicked()
            R.id.menu_settings -> presenter.onSettingsMenuClicked()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun displayMessage(message: String){
        toast(message)
    }

    override fun setUpNavigationDrawer(userName: String) {
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        userNameText = navigationView.getHeaderView(0).findViewById(R.id.user_name_text)
        userNameText.text = userName

        navigation_drawer_button.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }
    }

    companion object {
        var MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0
        var MY_GPS_RESOLUTION_STATUS_CODE = 1
    }

}