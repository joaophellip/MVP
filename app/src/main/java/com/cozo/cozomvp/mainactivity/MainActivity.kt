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
import butterknife.ButterKnife
import com.cozo.cozomvp.R
import com.cozo.cozomvp.paymentactivity.PaymentActivity
import com.cozo.cozomvp.SettingsActivity
import com.cozo.cozomvp.cartactivity.CartActivity
import com.cozo.cozomvp.mainactivity.checkoutfragment.CheckoutFragment
import com.cozo.cozomvp.mainactivity.checkoutfragment.CheckoutView
import com.cozo.cozomvp.mainactivity.listfragment.ListFragmentView
import com.cozo.cozomvp.mainactivity.listfragment.LocalListFragment
import com.cozo.cozomvp.mainactivity.mapfragment.LocalMapFragment
import com.cozo.cozomvp.mainactivity.mapfragment.MapFragmentView
import com.cozo.cozomvp.mainactivity.showdelivfragment.ShowDeliverersFragment
import com.cozo.cozomvp.mainactivity.showdelivfragment.ShowDeliverersView
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.transition.TransitionUtils
import com.cozo.cozomvp.usercart.OrderModel
import com.cozo.cozomvp.userprofileactivity.UserProfileActivity
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : MvpActivity<MainView, MainPresenter>(), MainView, ListFragmentView.MainActivityListener,
        MapFragmentView.MainActivityListener, CheckoutView.MainActivityListener,
        ShowDeliverersView.MainActivityListener, DetailsInterface.MainActivityListener,
        NavigationView.OnNavigationItemSelectedListener {

    // variables to hold reference to fragments
    private lateinit var mListFragment : LocalListFragment
    private lateinit var mMapFragment : LocalMapFragment
    private lateinit var mCheckoutFragment: CheckoutFragment
    private lateinit var mShowDeliverersFragment: ShowDeliverersFragment

    // variables to control state
    private var isListFragmentReady = false
    private var isMapFragmentReady = false
    private var isCheckoutFragmentReady = false
    private var isShowDeliverersFragmentReady = false

    // other variables
    private lateinit var currentTransitionName: String
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userNameText: TextView
    private var containerLayout: ViewGroup? = null
    private var detailsScene: Scene? = null

    override fun addRecyclerViewToContainer(recyclerView : RecyclerView) {
        containerLayout?.removeAllViews()
        containerLayout?.addView(recyclerView)
    }

    override fun areFragmentsReady(): Boolean{
        return isListFragmentReady && isMapFragmentReady && isCheckoutFragmentReady && isShowDeliverersFragmentReady
    }

    override fun createPresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun displayMessage(message: String){
        toast(message)
    }

    override fun goToCartActivity() {
        startActivityForResult(Intent(this, CartActivity::class.java), CART_ACTIVITY_REQUEST_CODE)
    }

    override fun goToPaymentActivity() {
        startActivity(Intent(this, PaymentActivity::class.java))
    }

    override fun goToSettingsActivity() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun goToUserProfileActivity() {
        startActivity(Intent(this, UserProfileActivity::class.java))
    }

    override fun hideOrderDetailsMenu(sharedView: View?) {
        if (sharedView != null){
            DetailsLayout.hideScene(this, containerLayout!!, sharedView, "name")
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
                .replace(R.id.actionContainer, CheckoutFragment.newInstance(), CheckoutFragment.TAG)
                .addToBackStack(CheckoutFragment.TAG)
                .commit()
    }

    override fun launchShowDeliverersFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.actionContainer, ShowDeliverersFragment.newInstance(), ShowDeliverersFragment.TAG)
                .commit()
    }

    override fun displayContainer() {
        actionContainer.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            MY_GPS_RESOLUTION_STATUS_CODE -> {
                if (resultCode == RESULT_OK) {
                    presenter.onLocationServiceReady()
                } else {
                    linear_layout_permission_box.visibility = View.VISIBLE
                }
            }
            //when CartActivity calls finishActivity(), it will trigger this callback with resultCode CART_ACTIVITY_REQUEST_CODE.
            CART_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    //do something
                } else {
                    //do something
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

    override fun onItemAddedToCart(order: OrderModel) {
        val childPosition : Int = TransitionUtils.getItemPositionFromTransition(currentTransitionName)
        presenter.onItemAddedToCart(childPosition, order)
    }

    override fun onItemsCardDataReady() {
        presenter.onItemsCardDataReady()
    }

    override fun onItemCardViewClicked(sharedView: View, transitionName: String, data: NetworkModel.MenuMetadata) {
        currentTransitionName = transitionName
        if (containerLayout == null) {
            containerLayout = findViewById(R.id.recyclerContainer)
        }
        presenter.onRestaurantCardViewClicked(data.restaurantID, sharedView, data)
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
        presenter.onFragmentReady()
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

    override fun onCompleteShowDeliverersFragment(showDeliverersFragment: ShowDeliverersFragment) {
        mShowDeliverersFragment = showDeliverersFragment
        isShowDeliverersFragmentReady = true
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

        ButterKnife.bind(this)
    }

    override fun onListFragmentRequired(): LocalListFragment {
        return mListFragment
    }

    override fun onMapFragmentRequired(): LocalMapFragment {
        return mMapFragment
    }

    override fun onShowDeliverersFragmentRequired(): ShowDeliverersFragment = mShowDeliverersFragment

    override fun onCheckoutFragmentRequired(): CheckoutFragment {
        return mCheckoutFragment
    }

    override fun onMapMarkerClicked(restID: String) {
        presenter.onMapMarkerClicked(restID)
    }

    override fun onMinusButtonClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun onPlusButtonClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCheckoutClicked() {
        presenter.onCheckoutClicked()
    }

    override fun onFragmentClicked() {
        presenter.onShowDeliverersClicked()
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

    override fun onPartnerCardViewClicked(sharedView: View, transitionName: String, data: NetworkModel.PartnerMetadata) {
        presenter.onPartnerCardViewClicked(data.partnerID)
    }

    override fun onPartnersCardDataReady(locations: MutableMap<String, NetworkModel.Location>, encodedPolylines: Map<String, String>) {
        presenter.onPartnersCardDataReady(locations, encodedPolylines)
    }

    override fun onRestaurantCardViewClicked(sharedView: View, transitionName: String, data: NetworkModel.MenuMetadata) {
        currentTransitionName = transitionName
        if (containerLayout == null) {
            containerLayout = findViewById(R.id.recyclerContainer)
        }
        presenter.onRestaurantCardViewClicked(data.restaurantID, sharedView, data)
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
//            TODO: tratar caso de "não perguntar novamente"
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
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

    override fun showOrderDetailsMenu(sharedView: View, data: NetworkModel.MenuMetadata){
        // shows up detailed view
        detailsScene = DetailsLayout.showScene(this, containerLayout!!, sharedView, currentTransitionName, data)
    }

    override fun onItemCardViewSwiped(sharedView: View, transitionName: String, data: NetworkModel.MenuMetadata) {
        //
    }

    override fun onPartnerCardViewSwiped(sharedView: View, transitionName: String, data: NetworkModel.PartnerMetadata) {
        //
    }

    override fun onRestaurantCardViewSwiped(sharedView: View, transitionName: String, data: NetworkModel.MenuMetadata) {
        //
    }

    companion object {
        //request codes to be used in either finishActivity() by CartActivity or GRP Resolution intent
        //see: https://developer.android.com/reference/android/app/Activity
        const val MY_GPS_RESOLUTION_STATUS_CODE = 1
        const val CART_ACTIVITY_REQUEST_CODE = 2
        var MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0
    }
}