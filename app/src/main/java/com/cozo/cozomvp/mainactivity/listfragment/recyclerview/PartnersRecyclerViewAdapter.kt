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
import com.cozo.cozomvp.networkapi.NetworkModel
import com.cozo.cozomvp.transition.TransitionUtils

class PartnersRecyclerViewAdapter(private var listener: OnPlaceClickListener) : RecyclerView.Adapter<PartnersRecyclerViewAdapter.RecyclerViewHolder>() {

    lateinit var mCardViewLayoutParams: ViewGroup.MarginLayoutParams
    private var partnerList: List<NetworkModel.PartnerMetadata> = listOf()

    interface OnPlaceClickListener {
        fun onPartnerCardViewClicked(sharedView: View, transitionName: String, position: Int, data: NetworkModel.PartnerMetadata)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview_partner, parent, false))
    }

    override fun getItemCount(): Int  = partnerList.size

    fun currentPartnerID(position: Int) : String = partnerList[position].partnerID

    fun cardData(partnerID: String): NetworkModel.PartnerMetadata? {
        partnerList.forEach {
            if (partnerID == it.partnerID){
                return it
            }
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bindView(position)
    }

    fun setPartnerList(items: List<NetworkModel.PartnerMetadata>){
        partnerList = items
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

            val totalDeliveryTimeInSeconds = partnerList[position].totalDeliveryTime
            this.mPartnerName.text = partnerList[position].name
            this.mTotalPartnerPrice.text = itemView.context.getString(R.string.deliv_price, String.format("%02.2f", partnerList[position].totalPrice).replace(".",","))
            this.mTimeToDelivery.text = itemView.context.getString(R.string.deliv_time, String.format("%2.0f", totalDeliveryTimeInSeconds/60).replace(".",","))
            this.mRoot.setOnClickListener {listener.onPartnerCardViewClicked(this.mRoot, TransitionUtils.getRecyclerViewTransitionName(position), position, partnerList[position])}

            // launch asynchronous process to download image
            ImageDownload(itemView.context, this.mPartnerImage, partnerList[position].pictureRefID)

        }
    }
}