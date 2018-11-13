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

class ReviewCartMenu(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartItemsRecyclerViewAdapter: CartItemsRecyclerViewAdapter

    override fun onFinishInflate() {
        super.onFinishInflate()
        val view: View = this
        recyclerView = view.findViewById(R.id.reviewCartRecyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        cartItemsRecyclerViewAdapter = CartItemsRecyclerViewAdapter()
        recyclerView.adapter = cartItemsRecyclerViewAdapter
    }

    companion object {

        private lateinit var listenerMainActivity : InflatedLayoutsInterface.MainActivityListener

        fun showScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String) : Scene {
            listenerMainActivity = activity as InflatedLayoutsInterface.MainActivityListener
            val reviewCartMenu : ReviewCartMenu = activity.layoutInflater.inflate(R.layout.coordinator_layout_review_cart_menu, container, false) as ReviewCartMenu

            val scene = Scene(container, reviewCartMenu as View)
            TransitionManager.go(scene)

            return scene
        }

        fun hideScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String): Scene {
            val reviewCartMenu : ReviewCartMenu = container.findViewById(R.id.reviewCartContainer) as ReviewCartMenu

            val scene = Scene(container, reviewCartMenu as View)
            TransitionManager.go(scene)
            return scene
        }

    }
}