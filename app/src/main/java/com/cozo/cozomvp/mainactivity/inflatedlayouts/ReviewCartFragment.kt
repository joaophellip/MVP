package com.cozo.cozomvp.mainactivity.inflatedlayouts

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.authentication.validationservice.PhoneValidationServiceImpl
import com.cozo.cozomvp.mainactivity.inflatedlayouts.recyclerview.CartItemsRecyclerViewAdapter
import com.cozo.cozomvp.networkapi.APIServices
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.usercart.CartServiceImpl
import com.cozo.cozomvp.usercart.PriceRange
import com.cozo.cozomvp.usercart.ReviewOrderModel
import com.cozo.cozomvp.usercart.TimeRange
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_review_cart.*

class ReviewCartFragment : Fragment(), View.OnClickListener,
        CartItemsRecyclerViewAdapter.RecyclerListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartItemsRecyclerViewAdapter: CartItemsRecyclerViewAdapter
    private lateinit var cardviewData: ReviewOrderModel
    private lateinit var userLocation : NetworkModel.Location
    private lateinit var restLocation: NetworkModel.Location
    private lateinit var listenerMainActivity: InflatedLayoutsInterface.MainActivityListener
    private lateinit var disposable: Disposable

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listenerMainActivity = activity as InflatedLayoutsInterface.MainActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement InflatedLayoutsInterface.MainActivityListener ")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val bundle = arguments
        val stringedData = bundle?.getString("cardview_data")
        val stringedUserLocation = bundle?.getString("user_location")
        val stringedRestLocation = bundle?.getString("restaurant_location")

        cartItemsRecyclerViewAdapter = CartItemsRecyclerViewAdapter(this)

        cardviewData = Gson().fromJson(stringedData, ReviewOrderModel::class.java)
        userLocation = Gson().fromJson(stringedUserLocation, NetworkModel.Location::class.java)
        restLocation = Gson().fromJson(stringedRestLocation, NetworkModel.Location::class.java)

        // get preview delivery time and price from back-end
        PhoneValidationServiceImpl.getInstance().getCurrentToken().addOnSuccessListener { result ->
            val cIdToken: String? = result.token
            disposable = APIServices.create().userPreviewDeliveryInfo(
                    cIdToken!!, userLocation.latitude, userLocation.longitude,
                    restLocation.latitude, restLocation.longitude)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ delivInfo ->
                        cardviewData.priceRange = PriceRange(delivInfo.minPrice, delivInfo.maxPrice)
                        cardviewData.timeRange = TimeRange(delivInfo.minTime, delivInfo.maxTime)
                        cartItemsRecyclerViewAdapter.setOrderList(cardviewData)
                    },{
                    })
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View? =  inflater.inflate(R.layout.fragment_review_cart, container, false)
        recyclerView = view?.findViewById(R.id.reviewCartRecyclerView) as RecyclerView
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        cartItemsRecyclerViewAdapter.setOrderList(cardviewData)
        recyclerView.adapter = cartItemsRecyclerViewAdapter

        // update price
        updatePrice()

        activityCartRestaurantName.text = cardviewData.orders[0].item.restaurantName

        activityCartConfirmOrderButton.setOnClickListener(this)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when(v){
            activityCartConfirmOrderButton -> {
                listenerMainActivity.onReviewCartChooseDeliveryPartnerButtonClicked()
            }
        }
    }

    override fun onRemoveItemClicked(position: Int) {

        // remove item from singleton
        val removedItem = CartServiceImpl.myInstance.removeOrder(CartServiceImpl.myInstance.getOrders()[position].id)
        // update cardview
        if (removedItem){
            cardviewData.orders = CartServiceImpl.myInstance.getOrders()
            cartItemsRecyclerViewAdapter.updateItemList(CartServiceImpl.myInstance.getOrders())
        }
        // update price
        updatePrice()

        // call activity
        listenerMainActivity.onItemRemovedFromCart()
    }

    private fun updatePrice(){
        var totalPrice = 0f
        cardviewData.orders.forEach {
            totalPrice += it.totalPrice
        }
        reviewCartPrice.text = context?.getString(R.string.fragment_review_cart_price, String.format("%02.2f", totalPrice).replace(".",","))
    }

    companion object {
        val TAG : String = ReviewCartFragment::class.java.simpleName
    }

}