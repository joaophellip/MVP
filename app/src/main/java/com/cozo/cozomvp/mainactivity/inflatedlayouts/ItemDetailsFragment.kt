package com.cozo.cozomvp.mainactivity.inflatedlayouts

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.mainactivity.listfragment.recyclerview.ImageDownload
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.usercart.CartServiceImpl
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_details.*

class ItemDetailsFragment : Fragment() {

    private lateinit var listenerMainActivity : InflatedLayoutsInterface.MainActivityListener

    private lateinit var viewData : NetworkModel.MenuMetadata

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
        val stringedData = bundle?.getString("card_data")
        viewData = Gson().fromJson(stringedData, NetworkModel.MenuMetadata::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.text = viewData.name
        description.text = viewData.ingredients
        txtQuantity.text = "1"

        // launch asynchronous process to download image
        ImageDownload(this.context!!, headerImage, viewData.pictureRefID)

        //set Minus Button
        MinusButton.setOnClickListener { decrementQuantity() }

        //set Plus Button
        PlusButton.setOnClickListener { incrementQuantity() }

        //set Order Button
        OrderButton.setOnClickListener {
            listenerMainActivity.onItemAddedToCart(CartServiceImpl.createOrder(viewData, getQuantity(), notesText.text.toString()))
        }
    }

    private fun incrementQuantity(){
        var auxQuantity : Int = txtQuantity.text.toString().toInt()
        if(auxQuantity < 10){
            auxQuantity++
            txtQuantity.text = auxQuantity.toString()
        }
    }

    private fun decrementQuantity(){
        var auxQuantity : Int = txtQuantity.text.toString().toInt()
        if(auxQuantity > 1){
            auxQuantity--
            txtQuantity.text = auxQuantity.toString()
        }
    }

    private fun getQuantity() : Int{
        return txtQuantity.text.toString().toInt()
    }

    companion object {
        val TAG : String = ItemDetailsFragment::class.java.simpleName
    }

}