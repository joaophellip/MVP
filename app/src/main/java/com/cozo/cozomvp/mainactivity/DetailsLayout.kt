package com.cozo.cozomvp.mainactivity

import android.app.Activity
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.CardView
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkapi.CardMenuData
import com.cozo.cozomvp.transition.HideDetailsTransitionSet
import com.cozo.cozomvp.transition.ShowDetailsTransitionSet

class DetailsLayout(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs) {

    @BindView(R.id.cardview) lateinit var cardViewContainer: CardView
    @BindView(R.id.headerImage) lateinit var imageViewPlaceDetails: ImageView
    @BindView(R.id.title) lateinit var textViewTitle: TextView
    @BindView(R.id.description) lateinit var textViewDescription: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        ButterKnife.bind(this)
    }

    private fun setData(data: CardMenuData){
        textViewTitle.text = data.menu?.name
        imageViewPlaceDetails.setImageBitmap(data.image)
        textViewDescription.text = data.menu?.ingredients
    }

    companion object {

        private lateinit var mListenerMainActivity : DetailsInterface.MainActivityListener

        fun showScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String, data: CardMenuData) : Scene {
            mListenerMainActivity = activity as DetailsInterface.MainActivityListener
            val detailsLayout : DetailsLayout = activity.layoutInflater.inflate(R.layout.item_place, container, false) as DetailsLayout
            detailsLayout.setData(data)

            val set : Transition = ShowDetailsTransitionSet(activity, transitionName, sharedView, detailsLayout)

            //adds swipe functionality when transition ends
            set.addListener(object: Transition.TransitionListener{
                override fun onTransitionEnd(transition: Transition?) {
                    val test = detailsLayout as View
                    test.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                        Log.d("onTransitionEnd","scrolled not working")
                    }
                }
                override fun onTransitionResume(transition: Transition?) {}
                override fun onTransitionPause(transition: Transition?) {}
                override fun onTransitionCancel(transition: Transition?) {}
                override fun onTransitionStart(transition: Transition?) {}
            })

            val scene = Scene(container, detailsLayout as View)
            TransitionManager.go(scene, set)

            //set FAB button
            /*val orderFab: FloatingActionButton = detailsLayout.findViewById(R.id.floatingActionButton)
            orderFab.setOnClickListener{ _ -> mListenerMainActivity.onOrderButtonClicked()
            }*/

            return scene
        }

        fun hideScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String): Scene {
            val detailsLayout : DetailsLayout = container.findViewById(R.id.bali_details_container) as DetailsLayout
            val set : Transition = HideDetailsTransitionSet(activity, transitionName, sharedView, detailsLayout)
            val scene = Scene(container, detailsLayout as View)
            TransitionManager.go(scene, set)
            return scene
        }
    }
}