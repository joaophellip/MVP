package com.cozo.cozomvp.cartactivity.listfragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.usercart.OrderModel
import kotlinx.android.synthetic.main.cardview_order.view.*

class RecyclerViewAdapter(private val listener: OnRecyclerListener) : RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    interface OnRecyclerListener{
        fun onMinusButtonClicked(orderID: Int, quantity: Int, notes: String)
        fun onPlusButtonClicked(orderID: Int, quantity: Int, notes: String)
        fun onDeleteButtonClicked(order: OrderModel)
    }

    lateinit var orders : List<OrderModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview_order, parent, false))
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bindView(position)
    }

    fun setData(orders: List<OrderModel>){
        this.orders = orders
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindView(position: Int){

            itemView.orderItemName.text = orders[position].item.name
            itemView.orderItemQuantity.text = orders[position].quantity.toString()

            // launch asynchronous process to download image
            //ImageDownload(itemView.context, itemView.foodImage, orders[position].item.pictureRefID)

            // set buttons' triggers
            //itemView.MinusButton.setOnClickListener {listener.onMinusButtonClicked(orders[position].id,orders[position].quantity-1,orders[position].notes) }
            //itemView.PlusButton.setOnClickListener {listener.onPlusButtonClicked(orders[position].id,orders[position].quantity+1,orders[position].notes) }

        }
    }
}