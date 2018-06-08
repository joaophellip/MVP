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
import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkAPI.ListPresenterData

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    private var restaurantList: List<ListPresenterData> = ArrayList()
    private var mPositionMap: MutableMap<Int,String> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.cardview, parent, false))
    }

    override fun getItemCount(): Int  = restaurantList.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        (holder as RecyclerViewHolder).bindView(position)
    }

    fun setRestaurantList(cards: List<ListPresenterData>) {
        restaurantList = cards
        for (i in restaurantList.indices) {
            notifyItemInserted(i)
            mPositionMap[i] = restaurantList[i].restID
        }
    }

    fun currentRestID(position: Int) : String = mPositionMap[position]!!

    fun positionById(restID: String) : Int? {
        // do a few error handling here...
        return when (mPositionMap.containsValue(restID)){
            true -> {
                val mMap = mPositionMap.filterValues{
                    it == restID
                }
                return when(mMap.size == 1){
                    true -> mMap.keys.elementAt(0)
                    else -> -2
                }
            }
            false -> -1
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
            //adjust formatting later on to use xml file instead of local variables
            val ratedBy = "( ${restaurantList[position].cardMenu.menu.ratedBy} )"
            val formattedPrice = "R$ " + String.format("%02.2f", restaurantList[position].cardMenu.menu.price).replace(".",",")
            val formattedPrepTime = "${restaurantList[position].cardMenu.menu.prepTime} min"

            this.mFoodImage.setImageBitmap(restaurantList[position].cardMenu.image)
            this.mFoodTitle.text = restaurantList[position].cardMenu.menu.name
            this.mFoodPrice.text = formattedPrice
            this.mFoodAveragePrepTime.text = formattedPrepTime
            this.mFoodRating.rating = restaurantList[position].cardMenu.menu.rating
            this.mRatedBy.text = ratedBy
        }
    }
}