package com.cozo.cozomvp.cartactivity.listfragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.cozo.cozomvp.R
import com.cozo.cozomvp.usercart.OrderModel

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

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

        @BindView(R.id.foodImage) lateinit var foodImage: ImageView
        @BindView(R.id.itemName) lateinit var itemName: TextView
        @BindView(R.id.MinusButton) lateinit var minusButton: Button
        @BindView(R.id.txtQuantity) lateinit var txtQuantity: TextView
        @BindView(R.id.PlusButton) lateinit var plusButton: Button
        @BindView(R.id.deleteItemButton) lateinit var deleteItemButton: ImageButton

        fun bindView(position: Int){

        }
    }
}