package com.cozo.cozomvp.mainactivity

import android.view.View
import com.cozo.cozomvp.dataprovider.DataProvider
import com.cozo.cozomvp.dataprovider.DataProviderInterface
import com.cozo.cozomvp.mainactivity.listfragment.LocalListFragment
import com.cozo.cozomvp.mainactivity.mapfragment.LocalMapFragment
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.usercart.CartServiceImpl
import com.cozo.cozomvp.usercart.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import retrofit2.HttpException

class MainPresenter : MvpBasePresenter<MainView>(), MainInterfaces {

    private var mDataProvider: DataProvider? = null

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()!!
    private lateinit var mUserLocation: NetworkModel.Location
    private val mUser: FirebaseUser? = mAuth.currentUser

    override fun onActivityCreated(isFirstTimeLogged: Boolean) {
        ifViewAttached {

            when(isFirstTimeLogged) {
                true -> {
                    // ask permission for user to get device location
                    it.requestUserPermissionForLocation()
                }
                false -> {
                    // tries to retrieve user location from model using Firebase TokenID for backend authentication
                    mUser?.getIdToken(true)?.addOnSuccessListener { result ->
                        val idToken: String? = result.token
                        retrieveUserLocation(idToken!!)
                    }
                }
            }
        }
    }

    /*
    Informs map fragment that all markers need to be visible
      */
    override fun onBackPressed(listPosition: Int) {
        ifViewAttached {

            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mMapFragment.onBackPressed()
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.requestLayout()

            it.hideOrderDetailsMenu(mListFragment.sharedViewByPosition(listPosition))
            it.addRecyclerViewToContainer(mListFragment.onRecyclerViewRequired())
        }
    }

    override fun onFragmentReady(){
        ifViewAttached {
            if (it.areFragmentsReady()) {
                // relays location to fragments
                relayUserLocationToListFragment(mUserLocation)
                relayUserLocationToMapFragment(mUserLocation)

                // displays welcome message to user
                it.displayMessage("Bem vindo " + mUser?.displayName!!)

                // sets up navigation drawer
                it.setUpNavigationDrawer(mUser.displayName!!)

                // informs checkout fragment that item was added to cart
                if(checkCheckoutStatus()){
                    it.onCheckoutFragmentRequired().updateContainer()
                }
            }
        }

    }

    override fun onItemAddedToCart(position: Int, order: OrderModel) {
        ifViewAttached {
            // add order to CartService singleton
            CartServiceImpl.myInstance.addOrder(order)

            // hide OrderDetailsMenu
            it.hideOrderDetailsMenu(it.onListFragmentRequired().sharedViewByPosition(position))

            // informs list fragment that item was added to cart
            it.onListFragmentRequired().dishOrderCreation(position)

            // informs show deliverer fragment that item was added to cart
            if(checkCheckoutStatus()){
                var currentPrice = 0f
                CartServiceImpl.myInstance.getOrders().forEach {
                    currentPrice += it.totalPrice
                }
                it.updateContainerCheckoutPrice(String.format("%02.2f", currentPrice).replace(".",","))
                it.updateContainerQuantityText(CartServiceImpl.myInstance.getOrders().size)
            }
        }
    }

    override fun onItemsCardDataReady() {
        ifViewAttached {
            val listFragment: LocalListFragment = it.onListFragmentRequired()
            listFragment.requestLayout()
            it.addRecyclerViewToContainer(listFragment.onRecyclerViewRequired())
        }
    }

    override fun onLocationServiceReady() {
        // retrieves user location from model
        retrieveUserLocation("", true)
    }

    /*
    Informs list fragment that card needs to be highlighted
      */
    override fun onMapMarkerClicked(restID: String) {
        ifViewAttached {
            val mListFragment : LocalListFragment = it.onListFragmentRequired()
            mListFragment.highlightRestCardView(restID)
        }
    }

    override fun onChoosingItemsDeliveryPartnerButtonClicked() {
        ifViewAttached {
            // update listFragment with delivery partners now. in order to do that,
            // send restaurant location to listFragment
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            val restID: String = mListFragment.currentRestID(0)
            provideRestLocation(restID)
        }
        /*ifViewAttached{
            // launch cart activity
            it.goToCartActivity()
        }*/
    }

    override fun onShowDeliverersClicked() {
        ifViewAttached {
            // update listFragment with delivery partners now. in order to do that,
            // send restaurant location to listFragment
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            val restID: String = mListFragment.currentRestID(0)
            provideRestLocation(restID)
        }
    }

    override fun onPartnerCardViewClicked(sharedView: View, data: NetworkModel.PartnerMetadata) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onPartnerCardViewClicked(data.partnerID)
        }
    }

    override fun onPaymentMenuClicked() {
        ifViewAttached {
            it.goToPaymentActivity()
        }
    }

    override fun onSettingsMenuClicked() {
        ifViewAttached {
            it.goToUserProfileActivity()
        }
    }

    override fun onPartnersCardDataReady(locations: MutableMap<String, NetworkModel.Location>, encodedPolylines: Map<String, String>) {
        ifViewAttached {
            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mMapFragment.onPartLocationDataAvailable(locations, encodedPolylines)

            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.requestLayout()

            it.addRecyclerViewToContainer(mListFragment.onRecyclerViewRequired())
        }
    }

    override fun onRestaurantCardViewClicked(sharedView: View, data: NetworkModel.MenuMetadata) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onRestaurantCardViewClicked(data.restaurantID)
            it.showOrderDetailsMenu(sharedView, data)
        }
    }

    override fun onItemCardViewClicked(sharedView: View, data: NetworkModel.MenuMetadata) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onRestaurantCardViewClicked(data.restaurantID)
            it.showOrderDetailsMenu(sharedView, data)
        }
    }

    override fun onRestCardViewHighlighted(restID: String){
        ifViewAttached {
            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mMapFragment.highlightMapMarker(restID)
        }
    }

    override fun onItemCardViewSwiped(sharedView: View, data: NetworkModel.MenuMetadata) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onRestaurantCardViewClicked(data.restaurantID)
            it.showOrderDetailsMenu(sharedView, data)
        }
    }

    override fun onPartnerCardViewSwiped(sharedView: View, data: NetworkModel.PartnerMetadata) {
        ifViewAttached {
            it.showPartnerDetailsMenu(sharedView, data)
        }
    }

    override fun onRestaurantCardViewSwiped(sharedView: View, data: NetworkModel.MenuMetadata) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onRestaurantCardViewClicked(data.restaurantID)
            it.showOrderDetailsMenu(sharedView, data)
        }
    }

    private fun checkCheckoutStatus() : Boolean{
        return (!CartServiceImpl.myInstance.getOrders().isEmpty())
    }

    private fun provideRestLocation(restID: String) {
        ifViewAttached {
            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            relayChosenRestaurantLocationToListFragment(mMapFragment.restLocation(restID))
        }
    }

    private fun retrieveUserLocation(idToken: String, isUserDeviceLocationNeeded: Boolean = false){
        mDataProvider = DataProvider(object : DataProviderInterface.MainActivityListener {
            override fun getActivity(): MainActivity? {
                var mActivity : MainActivity? = null
                ifViewAttached {
                    mActivity = it.onActivityRequired()
                }
                return mActivity
            }
            override fun onUserLocationRequestCompleted(location: NetworkModel.Location) {
                ifViewAttached {
                    // stores location
                    mUserLocation = location

                    // launches map fragment
                    it.launchMapFragment()

                    // launches list fragment
                    it.launchListFragment()

                    // launches checkout fragment
                    it.launchCheckoutFragment()

                    // launches show deliverers fragment
                    it.launchShowDeliverersFragment()
                }
            }
            override fun onUserLocationRequestFailed(e: Throwable) {
                when (e) {
                    is HttpException -> {
                        when(e.code()){
                            404 -> {
                                // user hadn't saved any preferable place to be ordered to
                                // ask permission for user to get device location
                                ifViewAttached {
                                    it.requestUserPermissionForLocation()
                                }
                            }
                        }
                    }
                }
            }
        })
        mDataProvider?.provideUserLatLng(idToken,isUserDeviceLocationNeeded)
    }

    private fun relayChosenRestaurantLocationToListFragment(location: NetworkModel.Location) {
        ifViewAttached {
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.onRestLocationDataAvailable(location)
        }
    }

    private fun relayUserLocationToListFragment(location: NetworkModel.Location){
        ifViewAttached {
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.onUserLocationDataAvailable(location)
        }
    }

    private fun relayUserLocationToMapFragment(location: NetworkModel.Location){
        ifViewAttached {
            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mMapFragment.onUserLocationDataAvailable(location)
        }
    }


}