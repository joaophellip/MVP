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
import com.cozo.cozomvp.mainactivity.listfragment.recyclerview.ImageDownload
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.transition.HideDetailsTransitionSet
import com.cozo.cozomvp.transition.ShowDetailsTransitionSet
import com.cozo.cozomvp.usercart.CartServiceImpl

class MenuDetailsLayout(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs) {

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

    private fun setData(data: NetworkModel.MenuMetadata){
        textViewTitle.text = data.name
        textViewDescription.text = data.ingredients
        quantityTextView.text = "1"

        // launch asynchronous process to download image
        ImageDownload(context, imageViewPlaceDetails, data.pictureRefID)
    }

    companion object {

        private lateinit var listenerMainActivity : DetailsInterface.MainActivityListener

        fun showScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String, data: NetworkModel.MenuMetadata) : Scene {
            listenerMainActivity = activity as DetailsInterface.MainActivityListener
            val menuDetailsLayout : MenuDetailsLayout = activity.layoutInflater.inflate(R.layout.coordinator_layout_menu, container, false) as MenuDetailsLayout

            menuDetailsLayout.setData(data)

            val set : Transition = ShowDetailsTransitionSet(activity, transitionName, sharedView, menuDetailsLayout)

            val scene = Scene(container, menuDetailsLayout as View)
            TransitionManager.go(scene, set)

            //set Minus Button
            val minusButton: Button = menuDetailsLayout.findViewById(R.id.MinusButton)
            minusButton.setOnClickListener { decrementQuantity(menuDetailsLayout) }

            //set Plus Button
            val plusButton: Button = menuDetailsLayout.findViewById(R.id.PlusButton)
            plusButton.setOnClickListener { incrementQuantity(menuDetailsLayout)}

            //set Order Button
            val orderButton : Button = menuDetailsLayout.findViewById(R.id.OrderButton)
            orderButton.setOnClickListener {
                listenerMainActivity.onItemAddedToCart(CartServiceImpl.createOrder(data,getQuantity(menuDetailsLayout), menuDetailsLayout.notesText.text.toString()))
            }

            return scene
        }

        fun hideScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String): Scene {
            val menuDetailsLayout : MenuDetailsLayout = container.findViewById(R.id.menu_details_container) as MenuDetailsLayout
            val set : Transition = HideDetailsTransitionSet(activity, transitionName, sharedView, menuDetailsLayout)
            val scene = Scene(container, menuDetailsLayout as View)
            TransitionManager.go(scene, set)
            return scene
        }

        private fun incrementQuantity(menuDetailsLayout: MenuDetailsLayout){
            var auxQuantity : Int = menuDetailsLayout.quantityTextView.text.toString().toInt()
            if(auxQuantity < 10){
                auxQuantity++
                menuDetailsLayout.quantityTextView.text = auxQuantity.toString()
            }
        }

        private fun decrementQuantity(menuDetailsLayout: MenuDetailsLayout){
            var auxQuantity : Int = menuDetailsLayout.quantityTextView.text.toString().toInt()
            if(auxQuantity > 1){
                auxQuantity--
                menuDetailsLayout.quantityTextView.text = auxQuantity.toString()
            }
        }

        private fun getQuantity(menuDetailsLayout: MenuDetailsLayout) : Int{
            return menuDetailsLayout.quantityTextView.text.toString().toInt()
        }
    }
}