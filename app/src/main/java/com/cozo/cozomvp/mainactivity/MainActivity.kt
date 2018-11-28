package com.cozo.cozomvp.mainactivity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.cozo.cozomvp.R
import com.cozo.cozomvp.paymentactivity.PaymentActivity
import com.cozo.cozomvp.SettingsActivity
import com.cozo.cozomvp.cartactivity.CartActivity
import com.cozo.cozomvp.helpers.IdleResourceInterceptor
import com.cozo.cozomvp.mainactivity.bottomfragments.WhileChoosingItemsBottomFragment
import com.cozo.cozomvp.mainactivity.bottomfragments.WhileChoosingItemsBottomView
import com.cozo.cozomvp.mainactivity.listfragment.ListFragmentView
import com.cozo.cozomvp.mainactivity.listfragment.LocalListFragment
import com.cozo.cozomvp.mainactivity.mapfragment.LocalMapFragment
import com.cozo.cozomvp.mainactivity.mapfragment.MapFragmentView
import com.cozo.cozomvp.mainactivity.bottomfragments.WhileChoosingDeliveryPartnerFragment
import com.cozo.cozomvp.mainactivity.bottomfragments.WhileChoosingDeliveryPartnerView
import com.cozo.cozomvp.mainactivity.inflatedlayouts.InflatedLayoutsInterface
import com.cozo.cozomvp.mainactivity.inflatedlayouts.ItemDetailsFragment
import com.cozo.cozomvp.mainactivity.inflatedlayouts.ReviewCartFragment
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.usercart.*
import com.cozo.cozomvp.userprofileactivity.UserProfileActivity
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : MvpActivity<MainView, MainPresenter>(), MainView, ListFragmentView.MainActivityListener,
        MapFragmentView.MainActivityListener, WhileChoosingItemsBottomView.MainActivityListener,
        WhileChoosingDeliveryPartnerView.MainActivityListener, InflatedLayoutsInterface.MainActivityListener,
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        FragmentManager.OnBackStackChangedListener  {

    // variables to hold reference to fragments
    private lateinit var mListFragment: LocalListFragment
    private lateinit var mMapFragment: LocalMapFragment
    private lateinit var whileChoosingItemsBottomFragment: WhileChoosingItemsBottomFragment
    private lateinit var whileChoosingDeliveryPartnerFragment: WhileChoosingDeliveryPartnerFragment
    private lateinit var currentListContainerFragment: String
    private var childListPosition: Int = -1

    // variables to control state
    private var isListFragmentReady = false
    private var isMapFragmentReady = false
    private var isWhileChoosingItemsBottomFragmentReady = false

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userNameText: TextView

    override fun addRecyclerViewToContainer(recyclerView : RecyclerView) {
        /*recyclerViewContainerLayout?.removeAllViews()
        recyclerViewContainerLayout?.addView(recyclerView)*/
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

    override fun showAlertDialog(message: String, title: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun goToChoosingItemDiffRestaurantsState() {
        // as of now, only containers are hide to force user to select a new item before continuing
        this.hideContainers()
    }

    override fun hideOrderDetailsMenu() {
        supportFragmentManager.popBackStack(ItemDetailsFragment.TAG, POP_BACK_STACK_INCLUSIVE)
        currentListContainerFragment = LocalListFragment.TAG
    }

    override fun hideReviewCartMenu() {
        supportFragmentManager.popBackStack(ReviewCartFragment.TAG, POP_BACK_STACK_INCLUSIVE)
        currentListContainerFragment = LocalListFragment.TAG

        actionContainer.visibility = View.VISIBLE
    }

    override fun onBackStackChanged() {
        presenter.onBackStackChanged()
    }

    override fun launchListFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.listContainer, LocalListFragment.newInstance(),
                        LocalListFragment.TAG)
                .addToBackStack(LocalListFragment.TAG)
                .commit()
        currentListContainerFragment = LocalListFragment.TAG
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
        when(currentListContainerFragment){
            LocalListFragment.TAG -> presenter.onBackPressedFromListFragment()
            ItemDetailsFragment.TAG -> presenter.onBackPressedFromItemDetailsMenu()
            ReviewCartFragment.TAG -> presenter.onBackPressedFromItemReviewCartMenu()
        }
    }

    override fun shownFragment(): String = currentListContainerFragment

    override fun onItemAddedToCart(order: OrderModel) {
        presenter.onItemAddedToCart(childListPosition, order)
    }

    override fun onItemRemovedFromCart() {
        presenter.onItemRemovedFromCart()
    }

    override fun onReviewCartChooseDeliveryPartnerButtonClicked() {
        presenter.onReviewCartChooseDeliveryPartnerButtonClicked()
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
        presenter.onInitialFragmentReady()
    }

    override fun onCompleteListFragment(listFragment: LocalListFragment){
        mListFragment = listFragment
        isListFragmentReady = true
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

        supportFragmentManager.addOnBackStackChangedListener(this)
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

    override fun showItemDetailsMenu(data: NetworkModel.MenuMetadata){
        // show up itemDetailsMenu view and store reference
        val itemDetailsFragment = ItemDetailsFragment()
        val bundle = Bundle()
        val stringedData: String = Gson().toJson(data)
        bundle.putString("card_data", stringedData)
        itemDetailsFragment.arguments = bundle
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.listContainer, itemDetailsFragment,
                        ItemDetailsFragment.TAG)
                .setTransition(TRANSIT_FRAGMENT_FADE)
                .addToBackStack(ItemDetailsFragment.TAG)
                .commit()
        currentListContainerFragment = ItemDetailsFragment.TAG
    }

    override fun showReviewCartMenu(data: List<OrderModel>, formattedAddress: String, userLocation: NetworkModel.Location, restLocation: NetworkModel.Location) {
        // show up reviewCartMenu view and store reference
        val reviewCartFragment = ReviewCartFragment()
        val bundle = Bundle()
        val stringedData: String = Gson().toJson(ReviewOrderModel(formattedAddress,null,null,data))
        val stringedUserLocation: String = Gson().toJson(userLocation)
        val stringedRestLocation: String = Gson().toJson(restLocation)

        bundle.putString("cardview_data", stringedData)
        bundle.putString("user_location", stringedUserLocation)
        bundle.putString("restaurant_location", stringedRestLocation)
        reviewCartFragment.arguments = bundle
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.listContainer, reviewCartFragment,
                        ReviewCartFragment.TAG)
                .setTransition(TRANSIT_FRAGMENT_FADE)
                .addToBackStack(ReviewCartFragment.TAG)
                .commit()
        currentListContainerFragment = ReviewCartFragment.TAG

        // hide actionContainer
        actionContainer.visibility = View.GONE
    }

    override fun showPartnerDetailsMenu(data: NetworkModel.PartnerMetadata) {
        //TODO as in showReviewCartMenu
    }

    override fun onItemCardViewClicked(childListPosition: Int, data: NetworkModel.MenuMetadata) {
        this.childListPosition = childListPosition
        presenter.onItemCardViewClicked(data)
    }

    override fun onPartnerCardViewClicked(childListPosition: Int, data: NetworkModel.PartnerMetadata) {
        this.childListPosition = childListPosition
        presenter.onPartnerCardViewClicked(data)
    }

    override fun onRestaurantCardViewClicked(childListPosition: Int, data: NetworkModel.MenuMetadata) {
        this.childListPosition = childListPosition
        presenter.onRestaurantCardViewClicked(data)
    }

    override fun onItemCardViewSwiped(sharedView: View, data: NetworkModel.MenuMetadata) {
        presenter.onItemCardViewSwiped(sharedView, data)
    }

    override fun onPartnerCardViewSwiped(sharedView: View, data: NetworkModel.PartnerMetadata) {
        presenter.onPartnerCardViewSwiped(sharedView, data)
    }

    override fun onRestaurantCardViewSwiped(sharedView: View, data: NetworkModel.MenuMetadata) {
        presenter.onRestaurantCardViewSwiped(sharedView, data)
    }

    override fun updateCartIconQuantityText(quantity: Int) {
        showQuantityText.text = quantity.toString()
    }

    override fun updateWhileChoosingItemsBottomFragmentPrice(currentPrice: String) {
        this.displayContainers()
        whileChoosingItemsBottomFragment.updateContainerPrice(currentPrice)
    }

    override fun updateWhileChoosingDeliveryPartnerFragmentReadyPrice(currentPrice: String) {
        this.displayContainers()
        whileChoosingDeliveryPartnerFragment.updateContainerPrice(currentPrice)
    }

    override fun onClick(v: View?) {
        when(v){
            cartContainer -> {
                presenter.onCartContainerClicked(v!!)
            }
        }
    }

    /*override fun showAlertDialog(message: String, title: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
                .setTitle(title)
        val positiveCallback = DialogInterface.OnClickListener { dialogState, _ ->
            //presenter.onAlertPositiveButtonClicked(dialogState)
        }
        val negativeCallback = DialogInterface.OnClickListener { dialogState, _ ->
            //presenter.onAlertNegativeButtonClicked(dialogState)
        }
        builder.setPositiveButton("Voltar", positiveCallback)
        builder.setNegativeButton("Continuar", negativeCallback)
        }

        val myDialog = builder.create()
        myDialog.setCanceledOnTouchOutside(false)
        myDialog.show()
    }*/

    private fun displayContainers() {
        actionContainer.visibility = View.VISIBLE
        cartContainer.visibility = View.VISIBLE
    }

    override fun hideContainers(){
        actionContainer.visibility = View.GONE
        cartContainer.visibility = View.GONE
    }

    override fun currentInflatedFragment() : String = currentListContainerFragment

    override fun onStart() {
        super.onStart()
        IdleResourceInterceptor.getInstance().popCall("MainActivity - onStart")
    }

    companion object {
        //request codes to be used in either finishActivity() by CartActivity or GRP Resolution intent
        //see: https://developer.android.com/reference/android/app/Activity
        const val MY_GPS_RESOLUTION_STATUS_CODE = 1
        const val CART_ACTIVITY_REQUEST_CODE = 2
        var MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0
    }
}