package com.cozo.cozomvp.mainactivity

import android.util.Log
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

    override fun onBackPressedFromItemDetailsMenu(listPosition: Int) {
        ifViewAttached {

            // force map to update zoom level and add markers back
            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mMapFragment.onBackPressed()

            // force recycler view to request layout again
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.requestLayout()

            // ask activity to hide OrderDetailsMenu
            it.hideOrderDetailsMenu(mListFragment.sharedViewByPosition(listPosition))

            // ask activity to show recycler view again
            it.addRecyclerViewToContainer(mListFragment.onRecyclerViewRequired())
        }
    }

    override fun onBackPressedFromItemReviewCartMenu(listPosition: Int) {
        ifViewAttached {

            // force recycler view to request layout again
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.requestLayout()

            // ask activity to hide ReviewCartMenu
            it.hideReviewCartMenu(mListFragment.sharedViewByPosition(listPosition))

            // ask activity to show recycler view again
            it.addRecyclerViewToContainer(mListFragment.onRecyclerViewRequired())
        }
    }

    override fun onInitialFragmentReady(){
        ifViewAttached {
            if (it.areInitialFragmentsReady()) {

                // relay location to fragments
                relayUserLocationToListFragment(mUserLocation)
                relayUserLocationToMapFragment(mUserLocation)

                // display welcome message to user
                it.displayMessage("Bem vindo " + mUser?.displayName!!)

                // set up navigation drawer
                it.setUpNavigationDrawer(mUser.displayName!!)

                // update cart related elements in UI
                updateCartElementsInUI()
            }
        }

    }

    override fun onWhileChoosingDeliveryPartnerFragmentReady() {
        ifViewAttached {
            if(areThereOrdersInCart()) {
                var currentPrice = 0f
                CartServiceImpl.myInstance.getOrders().forEach {
                    currentPrice += it.totalPrice
                }

                // update price text in whileChoosingDeliveryPartnerFragment
                it.updateWhileChoosingDeliveryPartnerFragmentReadyPrice(String.format("%02.2f", currentPrice).replace(".", ","))
            }
        }
    }

    override fun onItemAddedToCart(position: Int, order: OrderModel) {
        ifViewAttached {
            // add order to CartService singleton
            CartServiceImpl.myInstance.addOrder(order)

            // hide OrderDetailsMenu
            it.hideOrderDetailsMenu(it.onListFragmentRequired().sharedViewByPosition(position))

            // inform list fragment that item was added to cart
            it.onListFragmentRequired().dishOrderCreation(position)

            // update cart related elements in UI
            updateCartElementsInUI()
        }
    }

    override fun onItemsCardDataReady() {
        ifViewAttached {
            // force recycler view to request layout
            val listFragment: LocalListFragment = it.onListFragmentRequired()
            listFragment.requestLayout()

            // add recycler view to container
            it.addRecyclerViewToContainer(listFragment.onRecyclerViewRequired())
        }
    }

    override fun onLocationServiceReady() {
        // retrieves user location from model
        retrieveUserLocation("", true)
    }

    override fun onMapMarkerClicked(restID: String) {
        ifViewAttached {
            val mListFragment : LocalListFragment = it.onListFragmentRequired()
            mListFragment.highlightRestCardView(restID)
        }
    }

    override fun onChoosingItemsDeliveryPartnerButtonClicked() {
        ifViewAttached {
            // send restaurant location to listFragment
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            val restID: String = mListFragment.currentRestID(0)
            provideRestLocation(restID)

            // launch WhileChoosingDeliveryPartnerFragment
            it.launchWhileChoosingDeliveryPartnerFragment()
        }
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

    override fun onCartContainerClicked(sharedView: View) {
        ifViewAttached {
            // start transition from actionContainer to reviewCart coordinator layout
            it.showReviewCartMenu(sharedView)
        }
    }

    override fun onPartnersCardDataReady(locations: MutableMap<String, NetworkModel.Location>, encodedPolylines: Map<String, String>) {
        ifViewAttached {
            // send list to mapFragment
            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mMapFragment.onPartLocationDataAvailable(locations, encodedPolylines)

            // force recycler view to request layout
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.requestLayout()

            // add recycler view to container
            it.addRecyclerViewToContainer(mListFragment.onRecyclerViewRequired())

            //

        }
    }

    override fun onRestaurantCardViewClicked(sharedView: View, data: NetworkModel.MenuMetadata) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onRestaurantCardViewClicked(data.restaurantID)
            it.showItemDetailsMenu(sharedView, data)
        }
    }

    override fun onItemCardViewClicked(sharedView: View, data: NetworkModel.MenuMetadata) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onRestaurantCardViewClicked(data.restaurantID)
            it.showItemDetailsMenu(sharedView, data)
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
            it.showItemDetailsMenu(sharedView, data)
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
            it.showItemDetailsMenu(sharedView, data)
        }
    }

    private fun areThereOrdersInCart() : Boolean{
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
                // store location
                mUserLocation = location

                // launch initial fragments
                launchInitialFragments()
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

    private fun launchInitialFragments(){
        ifViewAttached {
            // launches map fragment
            it.launchMapFragment()

            // launches list fragment
            it.launchListFragment()

            // launches whileChoosingItemsBottomFragment
            it.launchWhileChoosingItemsBottomFragment()
        }
    }

    private fun updateCartElementsInUI(){
        ifViewAttached {
            if(areThereOrdersInCart()){
                var currentPrice = 0f
                CartServiceImpl.myInstance.getOrders().forEach {
                    currentPrice += it.totalPrice
                }

                // update price text in whileChoosingItemsBottomFragment
                it.updateWhileChoosingItemsBottomFragmentPrice(String.format("%02.2f", currentPrice).replace(".",","))

                // update item count in cartContainer inside MainActivity
                it.updateCartIconQuantityText(CartServiceImpl.myInstance.getOrders().size)
            }
        }
    }

}