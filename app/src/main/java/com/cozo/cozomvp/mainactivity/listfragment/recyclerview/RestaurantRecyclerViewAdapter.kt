package com.cozo.cozomvp.mainactivity.listfragment.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkapi.NetworkModel
import kotlinx.android.synthetic.main.cardview.view.*

class RestaurantRecyclerViewAdapter(private var listener: OnPlaceClickListener) : RecyclerView.Adapter<RestaurantRecyclerViewAdapter.RecyclerViewHolder>() {

    lateinit var mCardViewLayoutParams: ViewGroup.MarginLayoutParams
    private var restaurantList: List<NetworkModel.MenuMetadata> = listOf()

    interface OnPlaceClickListener {
        fun onRestaurantCardViewClicked(sharedView: View, position: Int, data: NetworkModel.MenuMetadata)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview, parent, false))
    }

    override fun getItemCount(): Int  = restaurantList.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bindView(position)
    }

    fun cardData(restID: String): NetworkModel.MenuMetadata? {
        restaurantList.forEach {
            if (restID == it.restaurantID){
                return it
            }
        }
        return null
    }

    fun currentRestID(position: Int) : String = restaurantList[position].restaurantID

    fun positionById(restID: String) : Int?{
        restaurantList.forEachIndexed { index, menuMenuData ->
            when(menuMenuData.restaurantID){
                restID -> return index
            }
        }
        return -1
    }

    fun setRestaurantList(items: List<NetworkModel.MenuMetadata>) {
        restaurantList = items
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        init {
            mCardViewLayoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams   //doing it every time. optimize later
        }

        fun bindView(position: Int){

            itemView.foodTitle.text = restaurantList[position].name
            itemView.foodPrice.text = itemView.context.getString(R.string.card_price, String.format("%02.2f", restaurantList[position].price).replace(".",","))
            itemView.averagePrepTime.text = itemView.context.getString(R.string.prepTime,restaurantList[position].prepTime)
            itemView.foodRating.rating = restaurantList[position].rating
            itemView.ratedBy.text = itemView.context.getString(R.string.ratedBy,restaurantList[position].ratedBy)
            itemView.cardViewRoot.setOnClickListener {listener.onRestaurantCardViewClicked(itemView.cardViewRoot, position, restaurantList[position])}

            // launch asynchronous process to download image
            ImageDownload(itemView.context, itemView.foodImage, restaurantList[position].pictureRefID)

            // sets raised elevation to first card by default
            when (position) {
                0 -> itemView.elevation = 24f
            }
        }
    }
}