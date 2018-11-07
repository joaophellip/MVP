package com.cozo.cozomvp.mainactivity.listfragment.recyclerview

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
import com.cozo.cozomvp.networkapi.NetworkModel

class PartnersRecyclerViewAdapter(private var listener: OnPlaceClickListener) : RecyclerView.Adapter<PartnersRecyclerViewAdapter.RecyclerViewHolder>() {

    lateinit var mCardViewLayoutParams: ViewGroup.MarginLayoutParams
    private var partnerList: List<CardInfoData> = listOf()
    //private var mPositionMap: MutableMap<Int,String> = mutableMapOf()

    interface OnPlaceClickListener {
        fun onPartnerCardViewClicked(partnerID: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview_partner, parent, false))
    }

    override fun getItemCount(): Int  = partnerList.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bindView(position)
    }

    fun currentPartnerID(position: Int) : String = partnerList[position].info!!.partnerID

    fun positionById(restID: String) : Int? {
        partnerList.forEachIndexed { index, cardInfoData ->
            when(cardInfoData.info!!.partnerID){
                restID -> return index
            }
        }
        return -1
    }

    fun setPartnerList(items: List<NetworkModel.PartnerMetadata>){
        partnerList = items.map { it ->
            CardInfoData(null, it)
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
            partnerList[position].info?.route?.forEach{
                totalDeliveryTimeInSeconds += it.duration.value
            }

            this.mPartnerName.text = partnerList[position].info!!.name
            this.mTotalPartnerPrice.text = itemView.context.getString(R.string.deliv_price, String.format("%02.2f", partnerList[position].info!!.totalPrice).replace(".",","))
            this.mTimeToDelivery.text = itemView.context.getString(R.string.deliv_time, String.format("%2.0f", totalDeliveryTimeInSeconds.toFloat()/60).replace(".",","))
            this.mRoot.setOnClickListener {_ -> listener.onPartnerCardViewClicked(partnerList[position].info!!.partnerID)}

            // launch asynchronous process to download image
            ImageDownload(itemView.context, this.mPartnerImage, partnerList[position].info!!.pictureRefID)

        }
    }
}