package com.cozo.cozomvp.mainactivity.listfragment.recyclerview

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.transition.TransitionUtils

class RestaurantItemsRecyclerViewAdapter(private var listener: OnPlaceClickListener) : RecyclerView.Adapter<RestaurantItemsRecyclerViewAdapter.RecyclerViewHolder>() {

    private var itemList: List<NetworkModel.MenuMetadata> = listOf()

    interface OnPlaceClickListener {
        fun onItemCardViewClicked(sharedView: View, transitionName: String, position: Int, data: NetworkModel.MenuMetadata)
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

        @BindView(R.id.foodImage) lateinit var foodImage: ImageView
        @BindView(R.id.foodTitle) lateinit var foodTitle: TextView
        @BindView(R.id.foodPrice) lateinit var foodPrice: TextView
        @BindView(R.id.averagePrepTime) lateinit var foodAveragePrepTime: TextView
        @BindView(R.id.foodRating) lateinit var foodRating: RatingBar
        @BindView(R.id.ratedBy) lateinit var ratedBy: TextView
        @BindView(R.id.cardViewRoot) lateinit var root: CardView

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bindView(position: Int){

            this.foodTitle.text = itemList[position].name
            this.foodPrice.text = itemView.context.getString(R.string.card_price, String.format("%02.2f", itemList[position].price).replace(".",","))
            this.foodAveragePrepTime.text = itemView.context.getString(R.string.prepTime,itemList[position].prepTime)
            this.foodRating.rating = itemList[position].rating
            this.ratedBy.text = itemView.context.getString(R.string.ratedBy,itemList[position].ratedBy)
            this.root.setOnClickListener {_ -> listener.onItemCardViewClicked(this.root, TransitionUtils.getRecyclerViewTransitionName(position), position, itemList[position])}

            // launch asynchronous process to download image
            ImageDownload(itemView.context, this.foodImage, itemList[position].pictureRefID)
        }
    }


}