package com.cozo.cozomvp.mainactivity

import android.view.View
import com.cozo.cozomvp.authentication.validationservice.PhoneValidationServiceImpl
import com.cozo.cozomvp.dataprovider.DataProvider
import com.cozo.cozomvp.dataprovider.DataProviderInterface
import com.cozo.cozomvp.mainactivity.inflatedlayouts.ItemDetailsFragment
import com.cozo.cozomvp.mainactivity.inflatedlayouts.ReviewCartFragment
import com.cozo.cozomvp.mainactivity.listfragment.LocalListFragment
import com.cozo.cozomvp.mainactivity.mapfragment.LocalMapFragment
import com.cozo.cozomvp.networkapi.APIServices
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.usercart.CartServiceImpl
import com.cozo.cozomvp.usercart.OrderModel
import com.cozo.cozomvp.userprofile.ProfileServiceImpl
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class MainPresenter : MvpBasePresenter<MainView>(), MainInterfaces {

    private var mDataProvider: DataProvider? = null

    private lateinit var mUserLocation: NetworkModel.Location
    private lateinit var mUserFormattedAddress: String

    private lateinit var disposable: Disposable

    // variable to hold current state of listFragment
    private var currentListState: Int = OTHER_REST

    // variable to hold amount of times back button is pressed successively
    private var backPressCount = 0

    // variable to be used when user clicks on "Choose Deliver Partner" inside ReviewMenu or ItemsMenu.
    // The socketIO setup can't be done until supportFragment popBackStack function is finished.
    private var backStackDeliv = false

    override fun onActivityCreated(isFirstTimeLogged: Boolean) {
        ifViewAttached {

            when(isFirstTimeLogged) {
                true -> {
                    // ask permission for user to get device location
                    it.requestUserPermissionForLocation()
                }
                false -> {
                    // tries to retrieve user location from model using Firebase TokenID for backend authentication
                    PhoneValidationServiceImpl.getInstance().getCurrentToken().addOnSuccessListener { result ->
                        val idToken: String? = result.token
                        retrieveUserLocation(idToken!!)
                    }
                }
            }
        }
    }

    override fun onBackPressedFromListFragment() {
        when(currentListState){
            SAME_REST -> {
                backPressCount += 1
                if (backPressCount <= 1) {
                    ifViewAttached {
                        it.displayMessage("Clique novamente para esvaziar o carrinho e voltar ao menu anterior.")
                    }
                }
                else {
                    goBackToInitialListState()
                }
            }
        }
    }

    override fun onBackPressedFromItemDetailsMenu() {
        backPressCount = 0
        ifViewAttached {

            // force map to update zoom level and add markers back
            val mMapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mMapFragment.onBackPressed()

            // force recycler view to request layout again
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.requestLayout()

            // ask activity to hide OrderDetailsMenu
            it.hideOrderDetailsMenu()

            // ask activity to show recycler view again
            it.addRecyclerViewToContainer(mListFragment.onRecyclerViewRequired())
        }
    }

    override fun onBackPressedFromItemReviewCartMenu() {
        backPressCount = 0
        ifViewAttached {

            // force recycler view to request layout again
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            mListFragment.requestLayout()

            // ask activity to hide ReviewCartMenu
            it.hideReviewCartMenu()

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
                it.displayMessage("Bem vindo " + ProfileServiceImpl.getInstance().getUserProfile()?.name)

                // set up navigation drawer
                it.setUpNavigationDrawer(ProfileServiceImpl.getInstance().getUserProfile()?.name!!)

                // update cart related elements in UI
                updateCartElementsInUI()
            }
        }

    }

    override fun onWhileChoosingDeliveryPartnerFragmentReady() {
        ifViewAttached {
            if(areThereOrdersInCart()) {
                var currentPrice = 0f
                CartServiceImpl.myInstance.getOrders().forEach { order ->
                    currentPrice += order.totalPrice
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
            it.hideOrderDetailsMenu()

            // inform list fragment that item was added to cart
            it.onListFragmentRequired().dishOrderCreation(position)

            // update cart related elements in UI
            updateCartElementsInUI()

            // update listState
            currentListState = SAME_REST
        }
    }

    override fun onItemRemovedFromCart() {
        ifViewAttached {
            // update iconCart quantity text
            val quantity = CartServiceImpl.myInstance.getOrders().size
            if (quantity > 0){
                it.updateCartIconQuantityText(quantity)
            } else {
                // mimic behavior of reviewCartFragment
                this.onBackPressedFromItemReviewCartMenu()

                //go back to previous state of choosing items from different restaurants
                it.goToChoosingItemDiffRestaurantsState()

                goBackToInitialListState()

            }
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
            when(it.currentInflatedFragment()){
                ItemDetailsFragment.TAG -> {
                    // hide itemsMenuCart if shown
                    it.hideOrderDetailsMenu()

                    // set var to be used when fragment popBackStack finishes
                    backStackDeliv = true
                }
                LocalListFragment.TAG -> {
                    showDeliveryPartnerCards()
                }
            }
        }
    }

    override fun onReviewCartChooseDeliveryPartnerButtonClicked() {
        ifViewAttached {
            // hide reviewCartFragment
            it.hideReviewCartMenu()

            // set var to be used when fragment popBackStack finishes
            backStackDeliv = true
        }
    }

    override fun onBackStackChanged() {
        if(backStackDeliv){
            // mimic behavior of click on bottom fragment when state is choosing items
            showDeliveryPartnerCards()
            this.onChoosingItemsDeliveryPartnerButtonClicked()
            backStackDeliv = false
        }
    }

    override fun onPartnerCardViewClicked(data: NetworkModel.PartnerMetadata) {
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
                when(it.shownFragment()){
                LocalListFragment.TAG -> {
                    // pop up reviewCartFragment
                    it.showReviewCartMenu(CartServiceImpl.myInstance.getOrders(), mUserFormattedAddress,
                            mUserLocation,
                            it.onMapFragmentRequired().restLocation(CartServiceImpl.myInstance.getOrders()[0].item.restaurantID))
                }
                ItemDetailsFragment.TAG -> {
                    // pop up reviewCartFragment
                    it.showReviewCartMenu(CartServiceImpl.myInstance.getOrders(), mUserFormattedAddress,
                            mUserLocation,
                            it.onMapFragmentRequired().restLocation(CartServiceImpl.myInstance.getOrders()[0].item.restaurantID))
                }
                ReviewCartFragment.TAG -> {
                    // force recycler view to request layout again
                    val mListFragment: LocalListFragment = it.onListFragmentRequired()
                    mListFragment.requestLayout()

                    // hide ReviewCartMenu
                    it.hideReviewCartMenu()

                    // show recycler view again
                    it.addRecyclerViewToContainer(mListFragment.onRecyclerViewRequired())
                }
            }
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

    override fun onRestaurantCardViewClicked(data: NetworkModel.MenuMetadata) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onRestaurantCardViewClicked(data.restaurantID)
            it.showItemDetailsMenu(data)
        }
    }

    override fun onItemCardViewClicked(data: NetworkModel.MenuMetadata) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onRestaurantCardViewClicked(data.restaurantID)
            it.showItemDetailsMenu(data)
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
            it.showItemDetailsMenu(data)
        }
    }

    override fun onPartnerCardViewSwiped(sharedView: View, data: NetworkModel.PartnerMetadata) {
        ifViewAttached {
            it.showPartnerDetailsMenu(data)
        }
    }

    override fun onRestaurantCardViewSwiped(sharedView: View, data: NetworkModel.MenuMetadata) {
        ifViewAttached {
            val mapFragment: LocalMapFragment = it.onMapFragmentRequired()
            mapFragment.onRestaurantCardViewClicked(data.restaurantID)
            it.showItemDetailsMenu(data)
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

                // get formatted address from current latlng location through back-end
                PhoneValidationServiceImpl.getInstance().getCurrentToken().addOnSuccessListener { result ->
                    val cIdToken: String? = result.token
                    disposable = APIServices.create().userReverseGeocoding(
                            cIdToken!!,location.latitude,location.longitude)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                mUserFormattedAddress = it.formattedAddress
                            },{
                            })
                }

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
            if(areThereOrdersInCart()) {
                var currentPrice = 0f
                CartServiceImpl.myInstance.getOrders().forEach { order ->
                    currentPrice += order.totalPrice
                }

                // update price text in whileChoosingItemsBottomFragment
                it.updateWhileChoosingItemsBottomFragmentPrice(String.format("%02.2f", currentPrice).replace(".", ","))

                // update item count in cartContainer inside MainActivity
                it.updateCartIconQuantityText(CartServiceImpl.myInstance.getOrders().size)
            } else {
                // change visibility of cartContainer and actionContainer to gone
                it.hideContainers()
            }
        }
    }

    private fun goBackToInitialListState(){
        ifViewAttached {
            // remove all orders from CartService singleton
            val ids = mutableListOf<Int>()
            CartServiceImpl.myInstance.getOrders().forEach { order ->
                ids.add(order.id)
            }
            for(id in ids){
                CartServiceImpl.myInstance.removeOrder(id)
            }

            // update UI elements
            updateCartElementsInUI()

            // listFragment to update list to items from diff restaurants
            it.onListFragmentRequired().showItemsOtherRestaurants()

            // reset backPressCount and listState
            backPressCount = 0
            currentListState = OTHER_REST
        }
    }

    private fun showDeliveryPartnerCards(){
        ifViewAttached {
            // send restaurant location to listFragment
            val mListFragment: LocalListFragment = it.onListFragmentRequired()
            val restID: String = mListFragment.currentRestID(0) //fix this hardCode
            provideRestLocation(restID)

            // launch WhileChoosingDeliveryPartnerFragment
            it.launchWhileChoosingDeliveryPartnerFragment()
        }
    }
    companion object {
        const val OTHER_REST = 0
        const val SAME_REST = 1
    }
}