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
import com.cozo.cozomvp.mainactivity.bottomfragments.WhileChoosingItemsBottomFragment
import com.cozo.cozomvp.mainactivity.bottomfragments.WhileChoosingItemsBottomView
import com.cozo.cozomvp.mainactivity.listfragment.ListFragmentView
import com.cozo.cozomvp.mainactivity.listfragment.LocalListFragment
import com.cozo.cozomvp.mainactivity.mapfragment.LocalMapFragment
import com.cozo.cozomvp.mainactivity.mapfragment.MapFragmentView
import com.cozo.cozomvp.mainactivity.bottomfragments.WhileChoosingDeliveryPartnerFragment
import com.cozo.cozomvp.mainactivity.bottomfragments.WhileChoosingDeliveryPartnerView
import com.cozo.cozomvp.mainactivity.inflatedlayouts.InflatedLayoutsInterface
import com.cozo.cozomvp.mainactivity.inflatedlayouts.ItemDetailsMenu
import com.cozo.cozomvp.mainactivity.inflatedlayouts.ReviewCartMenu
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.mainactivity.inflatedlayouts.transition.TransitionUtils
import com.cozo.cozomvp.usercart.OrderModel
import com.cozo.cozomvp.userprofileactivity.UserProfileActivity
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : MvpActivity<MainView, MainPresenter>(), MainView, ListFragmentView.MainActivityListener,
        MapFragmentView.MainActivityListener, WhileChoosingItemsBottomView.MainActivityListener,
        WhileChoosingDeliveryPartnerView.MainActivityListener, InflatedLayoutsInterface.MainActivityListener,
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    // variables to hold reference to fragments
    private lateinit var mListFragment : LocalListFragment
    private lateinit var mMapFragment : LocalMapFragment
    private lateinit var whileChoosingItemsBottomFragment: WhileChoosingItemsBottomFragment
    private lateinit var whileChoosingDeliveryPartnerFragment: WhileChoosingDeliveryPartnerFragment

    // variables to control state
    private var isListFragmentReady = false
    private var isMapFragmentReady = false
    private var isWhileChoosingItemsBottomFragmentReady = false

    // variables to control inflate
    private lateinit var currentTransitionName: String
    private var recyclerViewContainerLayout: ViewGroup? = null
    private var bottomFragmentContainerLayout: ViewGroup? = null
    private var bottomFragmentInflationScene: Scene? = null
    private var recyclerViewInflationScene: Scene? = null

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userNameText: TextView

    override fun addRecyclerViewToContainer(recyclerView : RecyclerView) {
        recyclerViewContainerLayout?.removeAllViews()
        recyclerViewContainerLayout?.addView(recyclerView)
    }

    override fun areInitialFragmentsReady(): Boolean{
        return isListFragmentReady && isMapFragmentReady && isWhileChoosingItemsBottomFragmentReady
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
            ItemDetailsMenu.hideScene(this, recyclerViewContainerLayout!!, sharedView, "name")
            recyclerViewInflationScene = null
            recyclerViewContainerLayout?.removeAllViews()
        }
        else {
            // treat exception
        }
    }

    override fun hideReviewCartMenu(sharedView: View?) {
        ReviewCartMenu.hideScene(this, bottomFragmentContainerLayout!!, "name")
        bottomFragmentInflationScene = null
        /*if (sharedView != null){
            ReviewCartMenu.hideScene(this, bottomFragmentContainerLayout!!, sharedView, "name")
            bottomFragmentInflationScene = null
        }
        else {
            // treat exception
        }*/
    }

    override fun launchListFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.listContainer, LocalListFragment.newInstance(),
                        LocalListFragment.TAG)
                .addToBackStack(LocalListFragment.TAG)
                .commit()
    }

    override fun launchMapFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mapContainer, LocalMapFragment.newInstance(),
                        LocalMapFragment.TAG)
                .addToBackStack(LocalMapFragment.TAG)
                .commit()
    }

    override fun launchWhileChoosingItemsBottomFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.actionContainer, WhileChoosingItemsBottomFragment.newInstance(),
                        WhileChoosingItemsBottomFragment.TAG)
                .addToBackStack(WhileChoosingItemsBottomFragment.TAG)
                .commit()
    }

    override fun launchWhileChoosingDeliveryPartnerFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.actionContainer, WhileChoosingDeliveryPartnerFragment.newInstance(),
                        WhileChoosingDeliveryPartnerFragment.TAG)
                .commit()
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
        if (recyclerViewInflationScene != null) {
            val childPosition : Int = TransitionUtils.getItemPositionFromTransition(currentTransitionName)
            presenter.onBackPressedFromItemDetailsMenu(childPosition)
        } else if (bottomFragmentInflationScene != null) {
            val childPosition : Int = TransitionUtils.getItemPositionFromTransition(currentTransitionName)
            presenter.onBackPressedFromItemReviewCartMenu(childPosition)
        }
    }

    override fun onItemAddedToCart(order: OrderModel) {
        val childPosition : Int = TransitionUtils.getItemPositionFromTransition(currentTransitionName)
        presenter.onItemAddedToCart(childPosition, order)
    }

    override fun onItemsCardDataReady() {
        presenter.onItemsCardDataReady()
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

    override fun onCompleteWhileChoosingItemsBottomFragment(whileChoosingItemsBottomFragment: WhileChoosingItemsBottomFragment) {
        this.whileChoosingItemsBottomFragment = whileChoosingItemsBottomFragment
        isWhileChoosingItemsBottomFragmentReady = true
        bottomFragmentContainerLayout = findViewById(R.id.listContainer)
        presenter.onInitialFragmentReady()
    }

    override fun onCompleteListFragment(listFragment: LocalListFragment){
        mListFragment = listFragment
        isListFragmentReady = true
        recyclerViewContainerLayout = findViewById(R.id.recyclerContainer)
        presenter.onInitialFragmentReady()
    }

    override fun onCompleteMapFragment(mapFragment: LocalMapFragment){
        mMapFragment = mapFragment
        isMapFragmentReady = true
        presenter.onInitialFragmentReady()
    }

    override fun onCompleteWhileChoosingDeliveryPartnerFragment(whileChoosingDeliveryPartnerFragment: WhileChoosingDeliveryPartnerFragment) {
        this.whileChoosingDeliveryPartnerFragment = whileChoosingDeliveryPartnerFragment
        presenter.onWhileChoosingDeliveryPartnerFragmentReady()
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
        cartContainer.setOnClickListener(this)

        ButterKnife.bind(this)
    }

    override fun onListFragmentRequired(): LocalListFragment {
        return mListFragment
    }

    override fun onMapFragmentRequired(): LocalMapFragment {
        return mMapFragment
    }

    override fun onShowDeliverersFragmentRequired(): WhileChoosingDeliveryPartnerFragment = whileChoosingDeliveryPartnerFragment

    override fun onWhileChoosingItemsBottomFragmentRequired(): WhileChoosingItemsBottomFragment {
        return whileChoosingItemsBottomFragment
    }

    override fun onMapMarkerClicked(restID: String) {
        presenter.onMapMarkerClicked(restID)
    }

    override fun onChoosingItemsDeliveryPartnerButtonClicked() {
        presenter.onChoosingItemsDeliveryPartnerButtonClicked()
    }

    override fun onChoosingDeliveryPartnerConfirmButtonClicked() {
        //before changing this, work on swipe functionality for container + click on CartIcon
        //TODO: presenter.onSomething()
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

    override fun onPartnersCardDataReady(locations: MutableMap<String, NetworkModel.Location>, encodedPolylines: Map<String, String>) {
        presenter.onPartnersCardDataReady(locations, encodedPolylines)
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

    override fun showItemDetailsMenu(sharedView: View, data: NetworkModel.MenuMetadata){
        // show up itemDetailsMenu view
        recyclerViewContainerLayout?.removeAllViews()
        recyclerViewInflationScene = ItemDetailsMenu.showScene(this, recyclerViewContainerLayout!!, sharedView, currentTransitionName, data)
    }

    override fun showReviewCartMenu(sharedView: View) {
        // show up reviewCartMenu view
        recyclerViewContainerLayout?.removeAllViews()
        bottomFragmentContainerLayout?.removeAllViews()
        val fromView: View = findViewById(R.id.actionContainer)
        bottomFragmentInflationScene = ReviewCartMenu.showScene(this, bottomFragmentContainerLayout!!, fromView, currentTransitionName)

        // hide actionContainer
        actionContainer.visibility = View.GONE
    }

    override fun showPartnerDetailsMenu(sharedView: View, data: NetworkModel.PartnerMetadata) {
        //TODO as in showReviewCartMenu
    }

    override fun onItemCardViewClicked(sharedView: View, transitionName: String, data: NetworkModel.MenuMetadata) {
        currentTransitionName = transitionName
        presenter.onItemCardViewClicked(sharedView, data)
    }

    override fun onPartnerCardViewClicked(sharedView: View, transitionName: String, data: NetworkModel.PartnerMetadata) {
        currentTransitionName = transitionName
        presenter.onPartnerCardViewClicked(sharedView, data)
    }

    override fun onRestaurantCardViewClicked(sharedView: View, transitionName: String, data: NetworkModel.MenuMetadata) {
        currentTransitionName = transitionName
        presenter.onRestaurantCardViewClicked(sharedView, data)
    }

    override fun onItemCardViewSwiped(sharedView: View, transitionName: String, data: NetworkModel.MenuMetadata) {
        currentTransitionName = transitionName
        presenter.onItemCardViewSwiped(sharedView, data)
    }

    override fun onPartnerCardViewSwiped(sharedView: View, transitionName: String, data: NetworkModel.PartnerMetadata) {
        currentTransitionName = transitionName
        presenter.onPartnerCardViewSwiped(sharedView, data)
    }

    override fun onRestaurantCardViewSwiped(sharedView: View, transitionName: String, data: NetworkModel.MenuMetadata) {
        currentTransitionName = transitionName
        presenter.onRestaurantCardViewSwiped(sharedView, data)
    }

    override fun updateCartIconQuantityText(quantity: Int) {
        showQuantityText.text = quantity.toString()
    }

    override fun updateWhileChoosingItemsBottomFragmentPrice(currentPrice: String) {
        this.displayContainer()
        whileChoosingItemsBottomFragment.updateContainerPrice(currentPrice)
    }

    override fun updateWhileChoosingDeliveryPartnerFragmentReadyPrice(currentPrice: String) {
        this.displayContainer()
        whileChoosingDeliveryPartnerFragment.updateContainerPrice(currentPrice)
    }

    override fun onClick(v: View?) {
        when(v){
            cartContainer -> {
                presenter.onCartContainerClicked(v!!)
            }
        }
    }

    private fun displayContainer() {
        actionContainer.visibility = View.VISIBLE
        cartContainer.visibility = View.VISIBLE
    }

    companion object {
        //request codes to be used in either finishActivity() by CartActivity or GRP Resolution intent
        //see: https://developer.android.com/reference/android/app/Activity
        const val MY_GPS_RESOLUTION_STATUS_CODE = 1
        const val CART_ACTIVITY_REQUEST_CODE = 2
        var MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0
    }
}