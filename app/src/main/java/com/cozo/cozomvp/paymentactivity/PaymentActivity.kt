package com.cozo.cozomvp.paymentactivity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cozo.cozomvp.R
import com.cozo.cozomvp.paymentactivity.CardListFragment.CardListFragment
import com.cozo.cozomvp.paymentactivity.CardListFragment.CardListInterface

class PaymentActivity : AppCompatActivity(), CardListInterface.CardActivityListener {

    data class CardData(
        val cardId:String,
        val name: String,
        val cardNumber: String,
        val cardNetwork: Int
    )

    override fun updateCardList() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAddCardButtonClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCardDeleted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        supportFragmentManager.beginTransaction().add(R.id.payment_frame,
                CardListFragment.newInstance(),CardListFragment.TAG)
                .addToBackStack(CardListFragment.TAG)
                .commit()
    }


}
