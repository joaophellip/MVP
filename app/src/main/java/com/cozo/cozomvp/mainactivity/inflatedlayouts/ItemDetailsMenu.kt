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
import android.widget.Button
import com.cozo.cozomvp.R
import com.cozo.cozomvp.mainactivity.listfragment.recyclerview.ImageDownload
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.mainactivity.inflatedlayouts.transition.HideDetailsTransitionSet
import com.cozo.cozomvp.mainactivity.inflatedlayouts.transition.ShowDetailsTransitionSet
import com.cozo.cozomvp.usercart.CartServiceImpl
import kotlinx.android.synthetic.main.coordinator_layout_item_details_menu.view.*

class ItemDetailsMenu(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs) {

    private fun setData(data: NetworkModel.MenuMetadata){
        title.text = data.name
        title.text = data.name
        description.text = data.ingredients
        txtQuantity.text = "1"

        // launch asynchronous process to download image
        ImageDownload(context, headerImage, data.pictureRefID)
    }

    companion object {

        private lateinit var listenerMainActivity : InflatedLayoutsInterface.MainActivityListener

        fun showScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String, data: NetworkModel.MenuMetadata) : Scene {
            listenerMainActivity = activity as InflatedLayoutsInterface.MainActivityListener
            val itemDetailsMenu : ItemDetailsMenu = activity.layoutInflater.inflate(R.layout.coordinator_layout_item_details_menu, container, false) as ItemDetailsMenu

            itemDetailsMenu.setData(data)

            val set : Transition = ShowDetailsTransitionSet(activity, transitionName, sharedView, itemDetailsMenu)

            val scene = Scene(container, itemDetailsMenu as View)
            TransitionManager.go(scene, set)

            //set Minus Button
            val minusButton: Button = itemDetailsMenu.findViewById(R.id.MinusButton)
            minusButton.setOnClickListener { decrementQuantity(itemDetailsMenu) }

            //set Plus Button
            val plusButton: Button = itemDetailsMenu.findViewById(R.id.PlusButton)
            plusButton.setOnClickListener { incrementQuantity(itemDetailsMenu) }

            //set Order Button
            val orderButton : Button = itemDetailsMenu.findViewById(R.id.OrderButton)
            orderButton.setOnClickListener {
                listenerMainActivity.onItemAddedToCart(CartServiceImpl.createOrder(data, getQuantity(itemDetailsMenu), itemDetailsMenu.notesText.text.toString()))
            }

            return scene
        }

        fun hideScene(activity: Activity, container: ViewGroup, sharedView: View, transitionName: String): Scene {
            val itemDetailsMenu : ItemDetailsMenu = container.findViewById(R.id.menu_details_container) as ItemDetailsMenu
            val set : Transition = HideDetailsTransitionSet(activity, transitionName, sharedView, itemDetailsMenu)
            val scene = Scene(container, itemDetailsMenu as View)
            TransitionManager.go(scene, set)
            return scene
        }

        private fun incrementQuantity(itemDetailsMenu: ItemDetailsMenu){
            var auxQuantity : Int = itemDetailsMenu.txtQuantity.text.toString().toInt()
            if(auxQuantity < 10){
                auxQuantity++
                itemDetailsMenu.txtQuantity.text = auxQuantity.toString()
            }
        }

        private fun decrementQuantity(itemDetailsMenu: ItemDetailsMenu){
            var auxQuantity : Int = itemDetailsMenu.txtQuantity.text.toString().toInt()
            if(auxQuantity > 1){
                auxQuantity--
                itemDetailsMenu.txtQuantity.text = auxQuantity.toString()
            }
        }

        private fun getQuantity(itemDetailsMenu: ItemDetailsMenu) : Int{
            return itemDetailsMenu.txtQuantity.text.toString().toInt()
        }
    }
}