package com.cozo.cozomvp.mainactivity.inflatedlayouts

import android.app.Activity
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.mainactivity.inflatedlayouts.transition.HideDetailsTransitionSet
import com.cozo.cozomvp.mainactivity.inflatedlayouts.transition.ShowDetailsTransitionSet

class ReviewCartMenu(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs) {

    companion object {

        private lateinit var listenerMainActivity : InflatedLayoutsInterface.MainActivityListener

        fun showScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String) : Scene {
            listenerMainActivity = activity as InflatedLayoutsInterface.MainActivityListener
            val reviewCartMenu : ReviewCartMenu = activity.layoutInflater.inflate(R.layout.coordinator_layout_review_cart_menu, container, false) as ReviewCartMenu

            //val set : Transition = ShowDetailsTransitionSet(activity, transitionName, sharedView, reviewCartMenu)

            val scene = Scene(container, reviewCartMenu as View)
            //TransitionManager.go(scene, set)
            TransitionManager.go(scene)

            return scene
        }

        fun hideScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String): Scene {
            val reviewCartMenu : ReviewCartMenu = container.findViewById(R.id.reviewCartContainer) as ReviewCartMenu
            //val set : Transition = HideDetailsTransitionSet(activity, transitionName, sharedView, reviewCartMenu)
            val scene = Scene(container, reviewCartMenu as View)
            //TransitionManager.go(scene, set)
            TransitionManager.go(scene)
            return scene
        }

    }
}