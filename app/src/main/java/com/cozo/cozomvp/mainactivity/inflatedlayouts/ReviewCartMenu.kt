package com.cozo.cozomvp.mainactivity.inflatedlayouts

import android.app.Activity
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Scene
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.mainactivity.inflatedlayouts.recyclerview.CartItemsRecyclerViewAdapter
import com.cozo.cozomvp.usercart.CartServiceImpl
import com.cozo.cozomvp.usercart.OrderModel
import kotlinx.android.synthetic.main.coordinator_layout_review_cart_menu.view.*

class ReviewCartMenu(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartItemsRecyclerViewAdapter: CartItemsRecyclerViewAdapter

    private fun setupRecyclerView(data: List<OrderModel>){

        // initialize RecyclerView object
        val view: View = this
        recyclerView = view.findViewById(R.id.reviewCartRecyclerView) as RecyclerView

        // provide layout
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // provide data
        cartItemsRecyclerViewAdapter = CartItemsRecyclerViewAdapter()
        cartItemsRecyclerViewAdapter.setOrderList(data)
        recyclerView.adapter = cartItemsRecyclerViewAdapter

    }

    companion object {

        private lateinit var listenerMainActivity : InflatedLayoutsInterface.MainActivityListener

        fun showScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String) : Scene {
            listenerMainActivity = activity as InflatedLayoutsInterface.MainActivityListener
            val reviewCartMenu : ReviewCartMenu = activity.layoutInflater.inflate(R.layout.coordinator_layout_review_cart_menu, container, false) as ReviewCartMenu

            reviewCartMenu.setupRecyclerView(CartServiceImpl.myInstance.getOrders())

            var totalPrice = 0f
            CartServiceImpl.myInstance.getOrders().forEach {
                totalPrice += it.totalPrice
            }

            reviewCartMenu.reviewCartPrice.text = activity.applicationContext.getString(R.string.fragment_review_cart_price, String.format("%02.2f", totalPrice).replace(".",","))

            val scene = Scene(container, reviewCartMenu as View)
            TransitionManager.go(scene)

            return scene
        }

        fun hideScene(activity: Activity, container: ViewGroup, transitionName: String): Scene {
            val reviewCartMenu : ReviewCartMenu = container.findViewById(R.id.reviewCartContainer) as ReviewCartMenu

            val scene = Scene(container, reviewCartMenu as View)
            TransitionManager.go(scene)
            return scene
        }

    }
}