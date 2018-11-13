package com.cozo.cozomvp.mainactivity.inflatedlayouts.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R

class CartItemsRecyclerViewAdapter : RecyclerView.Adapter<CartItemsRecyclerViewAdapter.RecyclerViewHolder>() {

    private var itemList: List<String> = listOf("2")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview_order, parent, false))
    }

    override fun getItemCount(): Int  = itemList.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bindView(position)
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindView(position: Int){}
    }
}