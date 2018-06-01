package com.cozo.cozomvp.listFragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cozo.cozomvp.CardMenuData
import com.cozo.cozomvp.R

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    private var restaurantList: List<CardMenuData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.cardview, parent, false))
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder?, position: Int) {
        (holder as RecyclerViewHolder).bindView(position)
    }

    fun setRestaurantList(cards: List<CardMenuData>) {
        restaurantList = cards
        for (i in restaurantList.indices) {
            notifyItemInserted(i)
        }
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        @BindView(R.id.foodImage) lateinit var mFoodImage: ImageView
        @BindView(R.id.foodTitle) lateinit var mFoodTitle: TextView
        @BindView(R.id.foodPrice) lateinit var mFoodPrice: TextView
        @BindView(R.id.averagePrepTime) lateinit var mFoodAveragePrepTime: TextView
        @BindView(R.id.foodRating) lateinit var mFoodRating: RatingBar
        @BindView(R.id.ratedBy) lateinit var mRatedBy: TextView

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bindView(position: Int){
            //adjust formatting later on to use xml file instead of local vals
            val ratedBy = "( ${restaurantList[position].menu.ratedBy} )"
            val formattedPrice = "R$ " + String.format("%02.2f", restaurantList[position].menu.price).replace(".",",")
            val formattedPrepTime = "${restaurantList[position].menu.prepTime} min"

            this.mFoodImage.setImageBitmap(restaurantList[position].image)
            this.mFoodTitle.text = restaurantList[position].menu.name
            this.mFoodPrice.text = formattedPrice
            this.mFoodAveragePrepTime.text = formattedPrepTime
            this.mFoodRating.rating = restaurantList[position].menu.rating
            this.mRatedBy.text = ratedBy
        }
    }
}