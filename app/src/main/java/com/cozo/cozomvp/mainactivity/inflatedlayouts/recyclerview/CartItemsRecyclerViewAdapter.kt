package com.cozo.cozomvp.mainactivity.inflatedlayouts.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.usercart.OrderModel
import kotlinx.android.synthetic.main.cardview_order.view.*

class CartItemsRecyclerViewAdapter : RecyclerView.Adapter<CartItemsRecyclerViewAdapter.RecyclerViewHolder>() {

    private var itemList: List<OrderModel> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View = when(viewType){
            VIEW_TYPE_ADDRESS -> LayoutInflater.from(parent.context).inflate(R.layout.cardview_address, parent, false)
            VIEW_TYPE_PREVIEW_DELIVERY -> LayoutInflater.from(parent.context).inflate(R.layout.cardview_preview_delivery, parent, false)
            VIEW_TYPE_ORDERS_LABEL -> LayoutInflater.from(parent.context).inflate(R.layout.cardview_order_label, parent, false)
            VIEW_TYPE_ORDERS -> LayoutInflater.from(parent.context).inflate(R.layout.cardview_order, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.cardview_order, parent, false)
        }
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int  = itemList.size + 3

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> VIEW_TYPE_ADDRESS
            1 -> VIEW_TYPE_PREVIEW_DELIVERY
            2 -> VIEW_TYPE_ORDERS_LABEL
            3 -> VIEW_TYPE_ORDERS
            else -> VIEW_TYPE_ORDERS
        }
    }

    fun setOrderList(items: List<OrderModel>){
        itemList = items
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bindView(position)
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindView(position: Int){
            when(position){
                0 -> {}
                1 -> {}
                2 -> {}
                3 -> {
                    itemView.orderItemName.text = itemList[position-3].item.name
                    itemView.orderItemQuantity.text = itemView.context.getString(R.string.cardview_order_quantity, itemList[position-3].quantity.toString())
                    itemView.orderItemsPrice.text = itemView.context.getString(R.string.cardview_order_items_price, String.format("%02.2f", itemList[position-3].totalPrice).replace(".",","))
                }
            }
        }
    }

    companion object {
        const val VIEW_TYPE_ADDRESS = 0
        const val VIEW_TYPE_PREVIEW_DELIVERY = 1
        const val VIEW_TYPE_ORDERS_LABEL = 2
        const val VIEW_TYPE_ORDERS = 3
    }
}