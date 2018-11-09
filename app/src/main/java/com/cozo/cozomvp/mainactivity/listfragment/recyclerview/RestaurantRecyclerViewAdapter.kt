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

class RestaurantRecyclerViewAdapter(private var listener: OnPlaceClickListener) : RecyclerView.Adapter<RestaurantRecyclerViewAdapter.RecyclerViewHolder>() {

    lateinit var mCardViewLayoutParams: ViewGroup.MarginLayoutParams
    private var restaurantList: List<NetworkModel.MenuMetadata> = listOf()

    interface OnPlaceClickListener {
        fun onRestaurantCardViewClicked(sharedView: View, transitionName: String, position: Int, data: NetworkModel.MenuMetadata)
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

        @BindView(R.id.foodImage) lateinit var foodImage: ImageView
        @BindView(R.id.foodTitle) lateinit var foodTitle: TextView
        @BindView(R.id.foodPrice) lateinit var foodPrice: TextView
        @BindView(R.id.averagePrepTime) lateinit var foodAveragePrepTime: TextView
        @BindView(R.id.foodRating) lateinit var foodRating: RatingBar
        @BindView(R.id.ratedBy) lateinit var ratedBy: TextView
        @BindView(R.id.cardViewRoot) lateinit var root: CardView

        init {
            mCardViewLayoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams   //doing it every time. optimize later
            ButterKnife.bind(this, itemView)
        }

        fun bindView(position: Int){

            this.foodTitle.text = restaurantList[position].name
            this.foodPrice.text = itemView.context.getString(R.string.price, String.format("%02.2f", restaurantList[position].price).replace(".",","))
            this.foodAveragePrepTime.text = itemView.context.getString(R.string.prepTime,restaurantList[position].prepTime)
            this.foodRating.rating = restaurantList[position].rating
            this.ratedBy.text = itemView.context.getString(R.string.ratedBy,restaurantList[position].ratedBy)
            this.root.setOnClickListener {listener.onRestaurantCardViewClicked(this.root, TransitionUtils.getRecyclerViewTransitionName(position), position, restaurantList[position])}

            // launch asynchronous process to download image
            ImageDownload(itemView.context, this.foodImage, restaurantList[position].pictureRefID)

            // sets raised elevation to first card by default
            when (position) {
                0 -> itemView.elevation = 24f
            }
        }
    }
}