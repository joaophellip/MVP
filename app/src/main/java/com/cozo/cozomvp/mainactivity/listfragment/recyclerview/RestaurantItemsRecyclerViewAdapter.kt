package com.cozo.cozomvp.mainactivity.listfragment.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkapi.NetworkModel
import kotlinx.android.synthetic.main.cardview_items.view.*

class RestaurantItemsRecyclerViewAdapter(private var listener: OnPlaceClickListener) : RecyclerView.Adapter<RestaurantItemsRecyclerViewAdapter.RecyclerViewHolder>() {

    private var itemList: List<NetworkModel.MenuMetadata> = listOf()

    interface OnPlaceClickListener {
        fun onItemCardViewClicked(sharedView: View, position: Int, data: NetworkModel.MenuMetadata)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantItemsRecyclerViewAdapter.RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview_items, parent, false))
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: RestaurantItemsRecyclerViewAdapter.RecyclerViewHolder, position: Int) {
        holder.bindView(position)
    }

    fun cardData(restID: String): NetworkModel.MenuMetadata? {
        itemList.forEach {
            if (restID == it.restaurantID){
                return it
            }
        }
        return null
    }

    fun currentRestID(position: Int) : String = itemList[position].restaurantID

    fun setItemList(items: List<NetworkModel.MenuMetadata>) {
        itemList = items
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindView(position: Int){

            itemView.foodTitle.text = itemList[position].name
            itemView.foodPrice.text = itemView.context.getString(R.string.card_price, String.format("%02.2f", itemList[position].price).replace(".",","))
            itemView.averagePrepTime.text = itemView.context.getString(R.string.prepTime,itemList[position].prepTime)
            itemView.foodRating.rating = itemList[position].rating
            itemView.ratedBy.text = itemView.context.getString(R.string.ratedBy,itemList[position].ratedBy)
            itemView.cardViewRoot.setOnClickListener {listener.onItemCardViewClicked(itemView.cardViewRoot, position, itemList[position])}

            // launch asynchronous process to download image
            ImageDownload(itemView.context, itemView.foodImage, itemList[position].pictureRefID)
        }
    }


}