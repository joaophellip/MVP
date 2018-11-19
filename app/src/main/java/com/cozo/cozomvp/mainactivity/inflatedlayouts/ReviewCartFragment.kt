package com.cozo.cozomvp.mainactivity.inflatedlayouts

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.mainactivity.inflatedlayouts.recyclerview.CartItemsRecyclerViewAdapter
import com.cozo.cozomvp.usercart.ReviewOrderModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_review_cart.*

class ReviewCartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartItemsRecyclerViewAdapter: CartItemsRecyclerViewAdapter
    private lateinit var cardviewData: ReviewOrderModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val bundle = arguments
        val stringedData = bundle?.getString("cardview_data")
        cardviewData = Gson().fromJson(stringedData, ReviewOrderModel::class.java)
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

        cartItemsRecyclerViewAdapter = CartItemsRecyclerViewAdapter()
        cartItemsRecyclerViewAdapter.setOrderList(cardviewData)
        recyclerView.adapter = cartItemsRecyclerViewAdapter

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