package com.cozo.cozomvp.listfragment

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkapi.CardInfoData

class PartnersRecyclerViewAdapter() : RecyclerView.Adapter<PartnersRecyclerViewAdapter.RecyclerViewHolder>() {

    lateinit var listener: OnPlaceClickListener
    lateinit var mCardViewLayoutParams: ViewGroup.MarginLayoutParams
    private var partnerList: MutableMap<String, CardInfoData> = mutableMapOf()
    private var mPositionMap: MutableMap<Int,String> = mutableMapOf()

    constructor(listener: OnPlaceClickListener) : this(){
        this.listener = listener
    }

    interface OnPlaceClickListener {
        fun onPartnerCardViewClicked(partnerID: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnersRecyclerViewAdapter.RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview_partner, parent, false))
    }

    override fun getItemCount(): Int  = partnerList.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bindView(position)
    }

    fun setPartnerList(cards: MutableMap<String, CardInfoData>){
        partnerList = cards
        partnerList.entries.forEachIndexed { index, mutableEntry ->
            mPositionMap[index] = mutableEntry.key
        }

    }

    fun currentPartnerID(position: Int) : String = mPositionMap[position]!!

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

        @BindView(R.id.partnerImage) lateinit var mPartnerImage: ImageView
        @BindView(R.id.partnerName) lateinit var mPartnerName: TextView
        @BindView(R.id.totalPartnerPrice) lateinit var mTotalPartnerPrice: TextView
        @BindView(R.id.timeToDelivery) lateinit var mTimeToDelivery: TextView
        @BindView(R.id.cardViewRoot) lateinit var mRoot: CardView

        init {
            mCardViewLayoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams   //doing it every time. optimize later
            ButterKnife.bind(this, itemView)
        }

        fun bindView(position: Int){
            var totalDeliveryTimeInSeconds : Long = 0
            partnerList.entries.elementAt(position).value.info?.route?.forEach {
                totalDeliveryTimeInSeconds += it.duration.value
            }

            //adjust formatting later on to use xml file instead of local variables
            val formattedPrice: String = "R$ " + String.format("%02.2f", partnerList.entries.elementAt(position).value.info?.totalPrice).replace(".",",")
            val formattedDeliveryTime = String.format("%2.0f", totalDeliveryTimeInSeconds.toFloat()/60).replace(".",",") + " min"

            this.mPartnerImage.setImageBitmap(partnerList.entries.elementAt(position).value.image)
            this.mPartnerName.text = partnerList.entries.elementAt(position).value.info?.name
            this.mTotalPartnerPrice.text = formattedPrice
            this.mTimeToDelivery.text = formattedDeliveryTime
            this.mRoot.setOnClickListener {_ -> listener.onPartnerCardViewClicked(partnerList.entries.elementAt(position).key)}
             // sets raised elevation to first card by default
            /*when (position) {
                0 -> itemView.elevation = 24f
            }*/
        }
    }
}