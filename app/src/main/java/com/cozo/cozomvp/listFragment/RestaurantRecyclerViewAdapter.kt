package com.cozo.cozomvp.listFragment

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
import com.cozo.cozomvp.networkAPI.CardMenuData
import com.cozo.cozomvp.transition.TransitionUtils

class RestaurantRecyclerViewAdapter() : RecyclerView.Adapter<RestaurantRecyclerViewAdapter.RecyclerViewHolder>() {

    lateinit var listener: OnPlaceClickListener
    lateinit var mCardViewLayoutParams: ViewGroup.MarginLayoutParams
    private var restaurantList: MutableMap<String, CardMenuData> = mutableMapOf()
    private var mPositionMap: MutableMap<Int,String> = mutableMapOf()

    constructor(listener: OnPlaceClickListener) : this(){
        this.listener = listener
    }

    interface OnPlaceClickListener {
        fun onRestaurantCardViewClicked(sharedView: View, transitionName: String, position: Int, data: CardMenuData, restID: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview, parent, false))
    }

    override fun getItemCount(): Int  = restaurantList.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bindView(position)
    }

    fun setRestaurantList(cards: MutableMap<String, CardMenuData>) {
        restaurantList = cards
        /*var counter = 0
        //restaurantList.forEach {
            //notifyItemInserted(counter)
            //mPositionMap[counter] = it.key
            //counter += counter
        //}
        */
        restaurantList.entries.forEachIndexed { index, mutableEntry ->
            mPositionMap[index] = mutableEntry.key
        }
    }

    fun currentRestID(position: Int) : String = mPositionMap[position]!!

    fun positionById(restID: String) : Int? {
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
        @BindView(R.id.cardViewRoot) lateinit var mRoot: CardView

        init {
            mCardViewLayoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams   //doing it every time. optimize later
            ButterKnife.bind(this, itemView)
        }

        fun bindView(position: Int){
            //adjust formatting later on to use xml file instead of local variables
            val ratedBy = "( ${restaurantList.entries.elementAt(position).value.menu?.ratedBy} )"
            val formattedPrice = "R$ " + String.format("%02.2f", restaurantList.entries.elementAt(position).value.menu?.price).replace(".",",")
            val formattedPrepTime = "${restaurantList.entries.elementAt(position).value.menu?.prepTime} min"

            this.mFoodImage.setImageBitmap(restaurantList.entries.elementAt(position).value.image)
            this.mFoodTitle.text = restaurantList.entries.elementAt(position).value.menu?.name
            this.mFoodPrice.text = formattedPrice
            this.mFoodAveragePrepTime.text = formattedPrepTime
            this.mFoodRating.rating = restaurantList.entries.elementAt(position).value.menu?.rating!!
            this.mRatedBy.text = ratedBy
            this.mRoot.setOnClickListener {_ -> listener.onRestaurantCardViewClicked(this.mRoot, TransitionUtils.getRecyclerViewTransitionName(position), position, restaurantList.entries.elementAt(position).value, restaurantList.entries.elementAt(position).key)}

            // sets raised elevation to first card by default
            when (position) {
                0 -> itemView.elevation = 24f
            }
        }
    }
}