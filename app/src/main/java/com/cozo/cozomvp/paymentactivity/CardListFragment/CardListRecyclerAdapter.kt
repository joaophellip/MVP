package com.cozo.cozomvp.paymentactivity.CardListFragment

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
import com.cozo.cozomvp.paymentactivity.PaymentActivity
import org.jetbrains.anko.image
import org.jetbrains.anko.toast

class CardListRecyclerAdapter(private var listener: CardListeners): RecyclerView.Adapter<CardListRecyclerAdapter.CardViewHolder>(){
    lateinit var cardList:List<PaymentActivity.CardData>

    interface CardListeners{
        fun deleteCard(cardId:String)
        fun selectCard(cardId:String)
    }

    fun updateCardData(cardList: List<PaymentActivity.CardData>){
        this.cardList = cardList
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindView(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview_credit_card, parent, false))
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        @BindView(R.id.creditCardNumber) lateinit var cardNumber:TextView
        @BindView(R.id.creditCardNetwork) lateinit var cardNet:ImageView
        @BindView(R.id.removeCreditCardButton) lateinit var deleteCard:ImageView
        @BindView(R.id.cardCardView) lateinit var cardView:CardView

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bindView(position: Int){
            this.cardNumber.text = itemView.context.getString(R.string.credit_card_number,cardList[position].cardNumber)
            this.cardNet.image = itemView.context.getDrawable(cardList[position].cardNetwork)
            this.deleteCard.setOnClickListener {
                listener.deleteCard(cardList[position].cardId)
                itemView.context.toast("delete").show()
            }
            this.cardView.setOnClickListener {
                listener.selectCard(cardList[position].cardId)
                itemView.context.toast("select").show()
            }
        }
    }
}