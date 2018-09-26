package com.cozo.cozomvp.mainactivity

import android.app.Activity
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.CardView
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkapi.CardMenuData
import com.cozo.cozomvp.transition.HideDetailsTransitionSet
import com.cozo.cozomvp.transition.ShowDetailsTransitionSet
import com.cozo.cozomvp.usercart.CartServiceImpl

class DetailsLayout(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs) {

    @BindView(R.id.cardview) lateinit var cardViewContainer: CardView
    @BindView(R.id.headerImage) lateinit var imageViewPlaceDetails: ImageView
    @BindView(R.id.title) lateinit var textViewTitle: TextView
    @BindView(R.id.description) lateinit var textViewDescription: TextView
    @BindView(R.id.txtQuantity) lateinit var quantityTextView: TextView
    @BindView(R.id.notesText) lateinit var notesText: EditText

    override fun onFinishInflate() {
        super.onFinishInflate()
        ButterKnife.bind(this)
    }

    private fun setData(data: CardMenuData){
        textViewTitle.text = data.menu?.name
        imageViewPlaceDetails.setImageBitmap(data.image)
        textViewDescription.text = data.menu?.ingredients
        quantityTextView.text = "1"
    }

    companion object {

        private lateinit var mListenerMainActivity : DetailsInterface.MainActivityListener

        fun showScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String, data: CardMenuData) : Scene {
            mListenerMainActivity = activity as DetailsInterface.MainActivityListener
            val detailsLayout : DetailsLayout = activity.layoutInflater.inflate(R.layout.item_place, container, false) as DetailsLayout
            detailsLayout.setData(data)

            val set : Transition = ShowDetailsTransitionSet(activity, transitionName, sharedView, detailsLayout)

            val scene = Scene(container, detailsLayout as View)
            TransitionManager.go(scene, set)

            //set Minus Button
            val minusButton: Button = detailsLayout.findViewById(R.id.MinusButton)
            minusButton.setOnClickListener { decrementQuantity(detailsLayout) }

            //set Plus Button
            val plusButton: Button = detailsLayout.findViewById(R.id.PlusButton)
            plusButton.setOnClickListener { incrementQuantity(detailsLayout)}

            //set Order Button
            val orderButton : Button = detailsLayout.findViewById(R.id.OrderButton)
            orderButton.setOnClickListener {
                mListenerMainActivity.onItemAddedToCart(CartServiceImpl.createOrder(data.menu!!,getQuantity(detailsLayout), detailsLayout.notesText.text.toString()))
            }

            return scene
        }

        fun hideScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String): Scene {
            val detailsLayout : DetailsLayout = container.findViewById(R.id.bali_details_container) as DetailsLayout
            val set : Transition = HideDetailsTransitionSet(activity, transitionName, sharedView, detailsLayout)
            val scene = Scene(container, detailsLayout as View)
            TransitionManager.go(scene, set)
            return scene
        }

        private fun incrementQuantity(detailsLayout: DetailsLayout){
            var auxQuantity : Int = detailsLayout.quantityTextView.text.toString().toInt()
            if(auxQuantity < 10){
                auxQuantity++
                detailsLayout.quantityTextView.text = auxQuantity.toString()
            }
        }

        private fun decrementQuantity(detailsLayout: DetailsLayout){
            var auxQuantity : Int = detailsLayout.quantityTextView.text.toString().toInt()
            if(auxQuantity > 1){
                auxQuantity--
                detailsLayout.quantityTextView.text = auxQuantity.toString()
            }
        }

        private fun getQuantity(detailsLayout: DetailsLayout) : Int{
            return detailsLayout.quantityTextView.text.toString().toInt()
        }
    }
}